package org.onosproject.mao.qos.base;

import org.onosproject.mao.qos.impl.MaoPipelineManager;
import org.onosproject.net.Device;
import org.onosproject.store.service.AtomicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by mao on 4/1/16.
 */
public class DeviceElement {

    private final Logger log;


    Device device;
    String dpid;

    ExecutorService executorService;
    SocketChannel socketChannel;
    BufferedInputStream recvInputStream;
    AtomicReference<State> stateMachine;

    enum State{
        /**
         * is initing, DE should not be used
         */
        INIT,

        /**
         * DE ready for use
         */
        STANDBY,

        /**
         * is going to destroy, DE should not be used
         */
        DESTROY,

        /**
         * this DE is ready for JAVA gc.
         */
        FINISH
    }

    public DeviceElement(Device device, String dpid, SocketChannel socketChannel) {

        stateMachine = new AtomicReference<State>();
        stateMachine.set(State.INIT);

        log = LoggerFactory.getLogger(getClass());


        this.device = device;
        this.dpid = dpid;
        this.socketChannel = socketChannel;
        executorService = Executors.newSingleThreadExecutor();

        recvInputStream = new BufferedInputStream(Channels.newInputStream(socketChannel));

        stateMachine.set(State.STANDBY);
    }

    public void removeDeviceElement(){
        stateMachine.set(State.DESTROY);

        try {
            recvInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(MaoPipelineManager.THREADPOOL_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                log.info("DeviceElement wait for threads shutdown...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //dpid
    }

    public SocketChannel getSocketChannel(){
        return socketChannel;
    }


    public void recvSubmit(){
        executorService.submit(new RecvTask());
    }

    private class RecvTask implements Callable{



        public RecvTask(){

        }


        @Override
        public Integer call(){

            //TODO - read socketchannel

            return 0;
//            try {
//                byte [] lenBuf = new byte[1];
//                int lenRet = 0;
//                lenRet = recvInputStream.read(lenBuf, 0, 1);
//                if(lenRet == 0){
//                    removeDeviceElement();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//
//            return 0;
        }

    }
}
