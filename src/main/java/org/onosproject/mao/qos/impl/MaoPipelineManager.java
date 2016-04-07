package org.onosproject.mao.qos.impl;

import com.google.common.util.concurrent.AtomicLongMap;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.api.impl.MaoQosObj;
import org.onosproject.mao.qos.base.DeviceElement;
import org.onosproject.mao.qos.base.MaoQosPolicy;
import org.onosproject.mao.qos.intf.MaoPipelineService;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultFlowEntry;
import org.onosproject.net.flow.FlowEntry;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
    ConcurrentMap<String, DeviceElement> DeviceElementMap; // "0000000000000001" : DE object

    DeviceCallable deviceCallable;
    RecvCallable recvCallable;
    SendCallable sendCallable;

    ExecutorService threadPool;


    final int WAIT_INIT_TIMEOUT = 500;
    final int SELECT_TIMEOUT = 500;
    final int THREADPOOL_AWAIT_TIMEOUT = 500; // milliseconds


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
        DeviceElementMap = new ConcurrentHashMap<>();

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

        //TODO - enhance them, not set null
        deviceCallable = null;
        recvCallable = null;
        sendCallable = null;

        threadPool = null;


        policyQueue = null;
        needShutdown = null;
        DeviceElementMap = null;

        log.info("Good Bye!", appId.id());
    }

    @Modified
    public void modify(ComponentContext context) {

    }


    private void startModule() {

    }

    private void shutdownModule() {

    }



    public void pushQosPolicy(){

    }








//    private class InnerDeviceListener implements DeviceListener {
//
//
//        @Override
//        public void event(DeviceEvent ev) {
//
//            switch (ev.type()) {
//                case DEVICE_ADDED:
//                case DEVICE_UPDATED:
//                case DEVICE_REMOVED:
//                case DEVICE_SUSPENDED:
//                case DEVICE_AVAILABILITY_CHANGED:
//                    Device device = ev.subject();
//                    DeviceEvent.Type type = ev.type();
//                    Port port = ev.port();
//                    long time = ev.time();
//                    String str = ev.toString();
//                    break;
//                default:
//                    break;
//
//            }
//
//        }
//    }


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

            if(!init()) {
                //TODO - report!
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
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(Channels.newInputStream(socketChannel));


                            byte [] dpidBuf = new byte[DPID_MESSAGE_LENGTH];
                            int dpidReadRet = bufferedInputStream.read(dpidBuf, 0, DPID_MESSAGE_LENGTH);// TODO - Attention! - check if it will trigger socketchannel's NonReadableChannelException Exception?
                            String deviceId = new String(dpidBuf);

                            Device device = getDeviceByDpid(deviceId);
                            if(device == null){
                                //FIXME
                            }

                            DeviceElement deviceElement = new DeviceElement(device, socketChannel);
                            DeviceElementMap.put(deviceId, deviceElement);


                            socketChannel.configureBlocking(false);
                            try {
                                recvCallable.socketChannelRegister(socketChannel, SelectionKey.OP_READ, deviceElement);


                                MaoQosPolicy policy = new MaoQosPolicy("0000000000000001", "tc qdisc add dev s1-eth1 root handle 1 htb");

                                int length = policy.getQosCmd().length();

                                byte [] lengthByte = new byte[4];
                                for(int i = 0; i<4; i++){
                                    lengthByte[i] = (byte) ( ( length >> (i*8) ) & 0xFF );
                                }

                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(new String(lengthByte));
                                stringBuilder.append(policy.getQosCmd());


                                ByteBuffer buf = ByteBuffer.allocate(stringBuilder.length());
                                buf.put(stringBuilder.toString().getBytes());

                                boolean bbb = buf.hasRemaining();
                                buf.flip();
                                bbb = buf.hasRemaining();
                                int www = 0;
                                while(buf.hasRemaining()){
                                    www += socketChannel.write(buf);
                                }
                                bbb = buf.hasRemaining();
                                int a = 0;

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//                            while (!policyQueue.offer(new MaoQosPolicy("0000000000000001", "sudo tc qdisc add dev s1-eth1 root handle 1 htb"))) {//FIXME - this is for
//                                ;
//                            }
//                            log.info("DeviceCallable, offer Policy", appId.id());


                        }else{
                            //TODO - DEBUG - REPORT
                        }
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

        private Device getDeviceByDpid(String deviceMac){

            DeviceId deviceId = DeviceId.deviceId("of:" + deviceMac);
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
            initReady = new AtomicBoolean();
            initReady.set(false);
        }

        public void socketChannelRegister(SocketChannel channel, int ops, DeviceElement deviceElement) throws ClosedChannelException, InterruptedException {
            while(!initReady.get()){
                    Thread.sleep(WAIT_INIT_TIMEOUT);
            }
            //recvSelector.wakeup()
            channel.register(recvSelector, ops, deviceElement);
        }

        @Override
        public Integer call(){

            try {
                init();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }

            while(true){

                try {

                    int readyCount = recvSelector.select(SELECT_TIMEOUT);

                    if(needShutdown.get()){
                        break;
                    }

                    if(readyCount == 0){
                        continue;
                    }

                    Iterator<SelectionKey> keyIter = recvSelector.selectedKeys().iterator();

                    while(keyIter.hasNext()){

                        SelectionKey key = keyIter.next();

                        if(key.isReadable()){
                            //TODO - FIXME - attention!! - if OVS shutdown first, will trigger Readable, and read return -1

                            ByteBuffer bufff = ByteBuffer.allocate(100);
                            int recvrecv = ((SocketChannel)(key.channel())).read(bufff);
                            int a = 0;



                            DeviceElement deviceElement = (DeviceElement) key.attachment();
                            deviceElement.recvSubmit();
                        }else if(key.isWritable()){

                            int ops = key.readyOps();

                        }else if(key.isAcceptable()){

                            int ops = key.readyOps();

                        }else if(key.isValid()){

                            int ops = key.readyOps();

                        }else{
                            int ops = key.readyOps();
                        }
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


        private void init() throws IOException {


            recvSelector = Selector.open();

            initReady.set(true);
        }

        private void destroy(){

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

        final int QUEUE_POLL_TIMEOUT = 500;
        AtomicBoolean initReady;


        public SendCallable(){

            initReady = new AtomicBoolean();
            initReady.set(false);
        }


        @Override
        public Integer call(){

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
                    Object objDeviceElement = null;
                    do {
                        objDeviceElement = DeviceElementMap.getOrDefault(policy.getDeviceId(), null);
                    }while(objDeviceElement == null);
                    if(objDeviceElement == null){ // TODO - put Policy back for ONOS wait, if nessesary, add countTimeout
                        continue;
                    }
                    DeviceElement deviceElement = (DeviceElement) objDeviceElement;

                    SocketChannel socketChannel = deviceElement.getSocketChannel();

                    log.info("SendCallable, Get socket channel!", appId.id());

                    StringBuilder cmd = encapsulate(policy);

                    ByteBuffer buf = ByteBuffer.allocate(cmd.length());
                    buf.put(cmd.toString().getBytes());

                    log.info("SendCallable, Get cmd and buf ready!", appId.id());


                    try {

                        log.info("SendCallable, send cmd...", appId.id());
                        socketChannel.write(buf);
                        log.debug("SendCallable, send cmd OK!", appId.id());

                    } catch (IOException e) {
                        e.printStackTrace();
                        log.info("SendCallable, send cmd Wrong!", appId.id());
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

        private void init(){
            initReady.set(true);
        }

        private void destroy(){

        }

        private StringBuilder encapsulate(MaoQosPolicy policy){

            //FIXME - below is test code

            int length = policy.getQosCmd().length();

            byte [] lengthByte = new byte[4];
            for(int i = 0; i<4; i++){
                lengthByte[i] = (byte) ( ( length >> (i*8) ) & 0xFF );
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new String(lengthByte));
            stringBuilder.append(policy.getQosCmd());

            return stringBuilder;
        }
    }

}
