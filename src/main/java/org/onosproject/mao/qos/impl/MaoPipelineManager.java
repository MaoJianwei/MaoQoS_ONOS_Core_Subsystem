package org.onosproject.mao.qos.impl;

import com.google.common.util.concurrent.AtomicLongMap;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.api.impl.MaoQosObj;
import org.onosproject.mao.qos.base.DeviceElement;
import org.onosproject.mao.qos.intf.MaoPipelineService;
import org.onosproject.net.Device;
import org.onosproject.net.Port;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultFlowEntry;
import org.onosproject.net.flow.FlowEntry;
import org.osgi.service.component.ComponentContext;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 3/27/16.
 */
@Component(immediate = true)
@Service
public class MaoPipelineManager implements MaoPipelineService {

    Selector recvSelector;
    LinkedBlockingQueue<String> policyQueue;
    AtomicBoolean needShutdown;
    ConcurrentMap<String, DeviceElement> DeviceElementMap;

    final int SELECT_TIMEOUT = 500;


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;




    @Activate
    public void activate(ComponentContext context) {
        coreService.registerApplication("org.onosproject.mao.qos");

    }

    @Deactivate
    public void deactivate() {

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
     */
    private class DeviceCallable implements Callable {

        Selector acceptSelector;
        ServerSocketChannel listenSocketChannel;

        final int LISTEN_PORT = 5511;
        final int DPID_MESSAGE_LENGTH = 16;

        public DeviceCallable(){


        }


        @Override
        public Integer call(){

            init();

            while(true){

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

                            //TODO - register DE into DEmap


                            socketChannel.configureBlocking(false);
                            //socketChannel.register()

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if(needShutdown.get()) {
                        break;
                    }
                }




                break;
            }

            destroy();

            return 0;
        }


        private void init(){

            //FIXME - acceptSelector and listenSocketChannel should be put together

            try {
                acceptSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                listenSocketChannel = ServerSocketChannel.open();
                listenSocketChannel.socket().bind(new InetSocketAddress(LISTEN_PORT));
                listenSocketChannel.configureBlocking(false);
                listenSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void destroy(){

        }

        private Device getDeviceByDpid(String deviceMac){

            Iterable<Device> devices =  deviceService.getDevices();
            Iterator<Device> iterDevice = devices.iterator();
            while(iterDevice.hasNext()){
                ;
            }
            return null;
        }

    }

    /**
     * Created by mao on 4/1/16.
     */
    private class RecvCallable implements Callable {

        public RecvCallable(){

        }

        @Override
        public Integer call(){

            init();

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

                            DeviceElement deviceElement = (DeviceElement) key.attachment();
                            deviceElement.recvSubmit();
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


        private void init(){

            try {
                recvSelector = Selector.open();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void destroy(){

        }
    }


    /**
     * Created by mao on 4/1/16.
     */
    public class SendCallable implements Callable {


        final int QUEUE_POLL_TIMEOUT = 500;


        public SendCallable(){

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
                    MaoQosObj policy =  (MaoQosObj) objPolicy;



                    Object objDeviceElement = DeviceElementMap.getOrDefault(policy.getDevice(),null);
                    if(objDeviceElement == null){
                        continue;
                    }
                    DeviceElement deviceElement = (DeviceElement) objDeviceElement;

                    SocketChannel socketChannel = deviceElement.getSocketChannel();


                    StringBuilder cmd = encapsulate(policy);

                    ByteBuffer buf = ByteBuffer.allocate(cmd.length());
                    buf.put(cmd.toString().getBytes());


                    try {

                        socketChannel.write(buf);

                    } catch (IOException e) {
                        e.printStackTrace();
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

        }

        private void destroy(){

        }

        private StringBuilder encapsulate(MaoQosObj policy){
            //TODO
            return new StringBuilder("");
        }
    }

}
