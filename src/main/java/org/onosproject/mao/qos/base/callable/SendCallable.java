package org.onosproject.mao.qos.base.callable;

import org.onosproject.mao.qos.api.impl.MaoQosObj;
import org.onosproject.mao.qos.base.DeviceElement;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/1/16.
 */
public class SendCallable implements Callable {

    LinkedBlockingQueue policyQueue;
    AtomicBoolean needShutdown;
    ConcurrentMap DeviceElementMap;


    final int QUEUE_POLL_TIMEOUT = 500;


    public SendCallable(LinkedBlockingQueue policyQueue, AtomicBoolean needShutdown, ConcurrentMap DeviceElementMap){

        this.policyQueue = policyQueue;
        this.needShutdown = needShutdown;
        this.DeviceElementMap = DeviceElementMap;
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