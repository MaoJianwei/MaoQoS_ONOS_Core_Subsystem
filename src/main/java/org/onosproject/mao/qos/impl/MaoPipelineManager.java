package org.onosproject.mao.qos.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.base.DeviceElement;
import org.onosproject.mao.qos.base.MaoQosPolicy;
import org.onosproject.mao.qos.intf.MaoPipelineService;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceProvider;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 3/27/16.
 */
@Component(immediate = true)
@Service
public class MaoPipelineManager implements MaoPipelineService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    LinkedBlockingQueue<MaoQosPolicy> policyQueue;
    AtomicBoolean needShutdown;
    Map<DeviceId, DeviceElement> deviceElementMap; // "0000000000000001" : DE object

    DeviceCallable deviceCallable;
    RecvCallable recvCallable;
    SendCallable sendCallable;

    ExecutorService threadPool;


    public static final int WAIT_INIT_TIMEOUT = 500;
    public static final int SELECT_TIMEOUT = 500;
    public static final int THREADPOOL_AWAIT_TIMEOUT = 500; // milliseconds


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;


    private ApplicationId appId;

    @Activate
    public void activate(ComponentContext context) {

        appId = coreService.registerApplication("org.onosproject.mao.qos");

        log.info("Init member...", appId.id());

        policyQueue = new LinkedBlockingQueue<>();
        needShutdown = new AtomicBoolean(false);
        deviceElementMap = new ConcurrentHashMap<>();

        log.info("Init callable...", appId.id());

        deviceCallable = new DeviceCallable();
        recvCallable = new RecvCallable();
        sendCallable = new SendCallable();

        log.info("Submit callable...", appId.id());

        // submit order should be considered!
        threadPool = Executors.newFixedThreadPool(3);
        threadPool.submit(recvCallable);
        threadPool.submit(sendCallable);
        threadPool.submit(deviceCallable);


        log.info("Let's Go!", appId.id());
    }

    @Deactivate
    public void deactivate() {


        needShutdown.set(true);

        log.info("shutdown flag is set...", appId.id());

        threadPool.shutdown();
        try {
            while (!threadPool.awaitTermination(THREADPOOL_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                log.info("wait for threads shutdown...", appId.id());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("threads shutdown, destroying...", appId.id());


        log.info("closing DeviceElement thread pool...", appId.id());
        DeviceElement.closeThreadPool(true);
        log.info("closing DeviceElement thread pool OK!", appId.id());

        for(Map.Entry<DeviceId, DeviceElement> ele : deviceElementMap.entrySet()){
            ele.getValue().removeDeviceElement();
        }
        deviceElementMap.clear();
        deviceElementMap = null;


        //TODO - enhance them, not set null
        deviceCallable = null;
        recvCallable = null;
        sendCallable = null;


        threadPool = null;


        policyQueue.clear();
        policyQueue = null;

        needShutdown = null;

        log.info("Good Bye!", appId.id());
    }

    @Modified
    public void modify(ComponentContext context) {

    }

    @Override
    public Map<DeviceId, DeviceElement> debug(){
        return deviceElementMap;
    }

    /**
     * blocking until putting success.
     * @param qosPolicy
     * @return
     */
    @Override
    public boolean pushQosPolicy(MaoQosPolicy qosPolicy) {

        if(!qosPolicy.checkValid()){
            return false;
        }

        try {
            policyQueue.put(qosPolicy);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeDeviceElement(DeviceId dpid){
        deviceElementMap.remove(dpid);
    }


    /**
     * Created by mao on 4/1/16.
     * primary coding OK
     */
    private class DeviceCallable implements Callable {

        Selector acceptSelector;
        ServerSocketChannel listenSocketChannel;
        AtomicBoolean initReady;

        final int LISTEN_PORT = 5511;
        final int DPID_MESSAGE_LENGTH = 16;

        public DeviceCallable(){

            initReady = new AtomicBoolean();
            initReady.set(false);
        }


        @Override
        public Integer call(){

            Thread.currentThread().setName("DeviceCallable");

            if(!init()) {
                log.error("DeviceCallable init fail!", appId.id());
                return -1;
            }

            while(true) {

                try {

                    int readyCount = acceptSelector.select(SELECT_TIMEOUT);

                    if(needShutdown.get()){
                        break;
                    }

                    if(readyCount == 0){
                        continue;
                    }

                    Iterator<SelectionKey> keyIter = acceptSelector.selectedKeys().iterator();

                    while(keyIter.hasNext()){

                        SelectionKey key = keyIter.next();

                        if(key.isAcceptable()){

                            SocketChannel socketChannel = ((ServerSocketChannel)key.channel()).accept();
                            socketChannel.configureBlocking(true);
//                            BufferedInputStream bufferedInputStream = new BufferedInputStream(Channels.newInputStream(socketChannel));

                            ByteBuffer deviceIdByteBuffer = ByteBuffer.allocate(DPID_MESSAGE_LENGTH);
                            int ret = socketChannel.read(deviceIdByteBuffer);
                            if(ret == -1){
                                keyIter.remove();
                                continue;
                            }

                            // TODO - Attention! - check if it will trigger socketchannel's NonReadableChannelException Exception?

                            String deviceId = new String(deviceIdByteBuffer.array());
                            Device device = getDeviceByDpid(deviceId);
                            if(device == null){
                                //FIXME
                            }

                            DeviceElement deviceElement = new DeviceElement(device, socketChannel, MaoPipelineManager.this);
                            deviceElementMap.put(device.id(), deviceElement);
                            log.info("accept new DeviceElement {}", deviceId);



                            socketChannel.configureBlocking(false);
                            try {
                                recvCallable.socketChannelRegister(socketChannel, SelectionKey.OP_READ, deviceElement);
                                log.info("register DeviceElement {} recvSelector OK!", deviceId);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }else{
                            //TODO - DEBUG - REPORT
                        }
                        keyIter.remove();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(needShutdown.get()) {
                        break;
                    }
                }
            }

            destroy();

            return 0;
        }


        private Boolean init(){

            //FIXME - acceptSelector and listenSocketChannel should be put together

            try {
                acceptSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            try {

                listenSocketChannel = ServerSocketChannel.open();
                listenSocketChannel.socket().bind(new InetSocketAddress(LISTEN_PORT));
                listenSocketChannel.configureBlocking(false);
                listenSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            initReady.set(true);
            return true;
        }

        private void destroy(){

            initReady.set(false);

            try {
                listenSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                acceptSelector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Device getDeviceByDpid(String dpid){

            DeviceId deviceId = DeviceId.deviceId("of:" + dpid);
            return deviceService.getDevice(deviceId);
        }

    }

    /**
     * Created by mao on 4/1/16.
     */
    private class RecvCallable implements Callable {

        Selector recvSelector;
        AtomicBoolean initReady;

        public RecvCallable(){
            initReady = new AtomicBoolean(false);
        }

        public void socketChannelRegister(SocketChannel channel, int ops, DeviceElement deviceElement) throws ClosedChannelException, InterruptedException {
            while(!initReady.get()){
                    Thread.sleep(WAIT_INIT_TIMEOUT);
            }
            recvSelector.wakeup();
            channel.register(recvSelector, ops, deviceElement);
        }

        @Override
        public Integer call(){

            Thread.currentThread().setName("RecvCallable");

            if(!init()){

                log.error("RecvCallable init fail!", appId.id());
                return -1;
            }


            while(true){

                try {

                    int readyCount = recvSelector.select(SELECT_TIMEOUT);

                    if(needShutdown.get()){
                        break;
                    }

                    if(readyCount == 0){
                        try {
                            Thread.sleep(100);// for socketchannel.register
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }

                    Iterator<SelectionKey> keyIter = recvSelector.selectedKeys().iterator();

                    while(keyIter.hasNext()){

                        SelectionKey key = keyIter.next();

                        if(key.isReadable()){

                            DeviceElement deviceElement = (DeviceElement) key.attachment();
                            if(key.isValid()){
                                key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));
                                log.info("key set OP_READ off");
                            }else{
                                log.info("key is not valid");
                            }
                            deviceElement.recvSubmit(key);
                            log.info("recv Submit");

                        }else if(key.isWritable()){

                            int ops = key.readyOps();
                            log.warn("recv Writable");

                        }else if(key.isAcceptable()){

                            int ops = key.readyOps();
                            log.warn("recv Acceptable");

                        }else if(key.isValid()){

                            int ops = key.readyOps();
                            log.warn("recv is valid");

                        }else{
                            int ops = key.readyOps();
                            log.warn("recv unknown");
                        }
                        keyIter.remove();
                        log.info("keyIter remove");
                    }
                    log.info("finish keyIter");

                } catch (IOException e) {
                    e.printStackTrace();
                    if(needShutdown.get()) {
                        break;
                    }
                }
            }

            destroy();

            return 0;
        }


        private boolean init() {

            try {
                recvSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            initReady.set(true);
            return true;
        }

        private void destroy(){

            initReady.set(false);

            try {
                recvSelector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Created by mao on 4/1/16.
     */
    private class SendCallable implements Callable {

        private static final int QUEUE_POLL_TIMEOUT = 500;
        private static final int QUEUE_REPUSH_DELAY = 100;
        private static final String DEVICE_ELEMENT_NOT_READY = "DE not ready";
        private static final String DEVICE_PORT_NOT_READY = "PORT not ready";


        AtomicBoolean initReady;


        public SendCallable(){

            initReady = new AtomicBoolean(false);
        }


        @Override
        public Integer call(){

            Thread.currentThread().setName("SendCallable");

            init();

            while(true) {
                try {

                    Object objPolicy = policyQueue.poll(QUEUE_POLL_TIMEOUT, TimeUnit.MILLISECONDS);


                    if(needShutdown.get()) {
                        break;
                    }


                    if (objPolicy == null){
                        continue;
                    }

                    MaoQosPolicy policy =  (MaoQosPolicy) objPolicy;

                    log.info("SendCallable, Get a Policy!", appId.id());

                    //FIXME - below is for test
                    Object objDeviceElement = deviceElementMap.getOrDefault(policy.getDeviceId(), null);

                    if(objDeviceElement == null){ // TODO - put Policy back for ONOS wait, if nessesary, add countTimeout

                        log.warn("DeviceElement is not existed !");

                        rePushPolicy(policy);
                        continue;
                    }
                    DeviceElement deviceElement = (DeviceElement) objDeviceElement;

                    SocketChannel socketChannel = deviceElement.getSocketChannel();

                    log.info("SendCallable, Get socket channel!", appId.id());

                    String cmd = encapsulate(policy);
                    if(cmd.equals(DEVICE_PORT_NOT_READY) || cmd.equals(DEVICE_ELEMENT_NOT_READY)){
                        rePushPolicy(policy);
                        continue;
                    }

                    ByteBuffer buf = ByteBuffer.allocate(cmd.length());
                    buf.put(cmd.getBytes());
                    buf.flip();

                    log.info("SendCallable, Get cmd and buf ready!", appId.id());

                    try {


                        log.info("SendCallable, send cmd...", appId.id());
                        int ret = socketChannel.write(buf);
                        log.info("SendCallable, send cmd OK!", appId.id());

                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error("SendCallable, send cmd Wrong!", appId.id());
                        //TODO - Mao
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if(needShutdown.get()) {
                        break;
                    }
                }
            }

            destroy();

            return 0;
        }

        private void init() {
            initReady.set(true);
        }

        private void destroy() {
            initReady.set(false);
        }

        private void rePushPolicy(MaoQosPolicy maoQosPolicy) throws InterruptedException{
            if(pushQosPolicy(maoQosPolicy)){
                log.warn("repush policy success");
                Thread.sleep(QUEUE_REPUSH_DELAY);
            }else{
                log.error("repush policy fail !!!");
            }
        }

        private String encapsulate(MaoQosPolicy policy){

            DeviceElement deviceElement = deviceElementMap.getOrDefault(policy.getDeviceId(), null);
            if(deviceElement == null){

                log.warn("DeviceElement is not existed !");
                return DEVICE_ELEMENT_NOT_READY;
            }
            String portName = deviceElement.getPortName(policy.getDeviceIntfNumber());
            if(portName.equals("")){

                log.warn("DeviceElement_Port is not ready !");
                return DEVICE_PORT_NOT_READY;
            }

            StringBuilder cmdBuilder = new StringBuilder();

            cmdBuilder.append(policy.getQosCmdHead());
            cmdBuilder.append(portName);
            cmdBuilder.append(policy.getQosCmdTail());


            int length = cmdBuilder.length();

            byte [] lengthByte = new byte[4];
            for(int i = 0; i<4; i++){
                lengthByte[i] = (byte) ( ( length >> (i*8) ) & 0xFF );
            }

            StringBuilder protocolBuilder = new StringBuilder();
            protocolBuilder.append(new String(lengthByte));
            protocolBuilder.append(cmdBuilder.toString());

            return protocolBuilder.toString();
        }
    }
}
