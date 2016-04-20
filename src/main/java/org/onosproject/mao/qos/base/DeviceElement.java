package org.onosproject.mao.qos.base;

import org.onosproject.mao.qos.impl.MaoPipelineManager;
import org.onosproject.net.Device;
import org.onosproject.store.service.AtomicValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by mao on 4/1/16.
 */
public class DeviceElement {

    private final Logger log = LoggerFactory.getLogger(getClass());

    enum State {
        /**
         * is initing, DE should not be used
         */
        INIT,

        INIT_WAIT_PORT,

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


    Device device;
    String dpid;

    private static final ExecutorService executorService = new ThreadPoolExecutor(0, 10, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    SocketChannel socketChannel;
    BufferedInputStream recvInputStream;
    AtomicReference<State> stateMachine;
    Set<String> ports;


    public static void closeThreadPool(boolean isTerminate) {

        executorService.shutdown();
        if (isTerminate && !(executorService.isShutdown())) {
            executorService.shutdownNow();
        }

        try {
            while (!executorService.awaitTermination(MaoPipelineManager.THREADPOOL_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DeviceElement(Device device, String dpid, SocketChannel socketChannel) {

        stateMachine = new AtomicReference<State>();
        stateMachine.set(State.INIT);


        this.device = device;
        this.dpid = dpid;
        this.socketChannel = socketChannel;
        this.ports = new HashSet<>();

        recvInputStream = new BufferedInputStream(Channels.newInputStream(socketChannel));

        stateMachine.set(State.INIT_WAIT_PORT);
    }

    public void removeDeviceElement() {
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

        ports.clear();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }


    public void recvSubmit() {
        executorService.submit(new RecvTask());
    }


    private class RecvTask implements Callable {

        public RecvTask() {

        }

        @Override
        public Integer call() {

            //TODO - read socketchannel

            switch (stateMachine.get()) {
                case INIT:

                    break;

                case INIT_WAIT_PORT:


                    try {
                        StringBuilder portsBuilder = new StringBuilder();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1);

                        while (true) {
                            byteBuffer.clear();
                            socketChannel.read(byteBuffer);

                            byte[] one = byteBuffer.array();
                            if (one[0] != '\n') {
                                portsBuilder.append(new String(one));
                            } else {
                                break;
                            }
                        }

                        String dpPorts = portsBuilder.toString();
                        log.info("{} get ports info: {}", dpid, dpPorts);

                        String[] dpPortsList = dpPorts.split(",");
                        for (String str : dpPortsList) {
                            ports.add(str);
                        }

                        stateMachine.set(State.STANDBY);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case STANDBY:
                case DESTROY:
                case FINISH:
                default:

            }

            return 0;
        }

    }
}
