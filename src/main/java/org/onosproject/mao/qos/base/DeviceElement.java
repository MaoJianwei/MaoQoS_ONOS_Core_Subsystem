package org.onosproject.mao.qos.base;

import org.onosproject.mao.qos.impl.MaoPipelineManager;
import org.onosproject.net.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by mao on 4/1/16.
 */
public final class DeviceElement {

    private static final Logger log = LoggerFactory.getLogger(DeviceElement.class);

    public enum State {
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
         * socketchannel down, DE should not be used
         */
//        SOCKET_DOWN,

        /**
         * is going to destroy, DE should not be used
         */
        DESTROY,

        /**
         * this DE is ready for JAVA gc.
         */
        FINISH
    }


    private static final ExecutorService executorService = Executors.newCachedThreadPool(); //new ThreadPoolExecutor(0, 10, 10L, TimeUnit.SECONDS, new SynchronousQueue<>());


    private MaoPipelineManager boss;
    private Device device;
//    private String dpid;

    private SocketChannel socketChannel;
    private BufferedInputStream recvInputStream;
    private AtomicReference<State> stateMachine;
    private Map<Integer, String> portMap;


    //below is for debug

    public State getState() {
        return stateMachine.get();
    }

    public Map<Integer, String> getPortMap() {
        return portMap;
    }

    public static int getActiveCount(){
        return ((ThreadPoolExecutor)executorService).getActiveCount();
    }
    public static int getPoolSize(){
        return ((ThreadPoolExecutor)executorService).getPoolSize();
    }
    public static long getTaskCount(){
        return ((ThreadPoolExecutor)executorService).getTaskCount();
    }
    public static long getCompletedTaskCount(){
        return ((ThreadPoolExecutor)executorService).getCompletedTaskCount();
    }

    //above is for debug








    public static void closeThreadPool(boolean isTerminate) {

        executorService.shutdown();
        log.info("executorService shutdown set");
        if (isTerminate && !(executorService.isShutdown())) {
            executorService.shutdownNow();
            log.info("executorService shutdown NOW set");
        }

        int count = 10;
        try {
            while (!executorService.awaitTermination(MaoPipelineManager.THREADPOOL_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS) && (count--) > 0) {
                log.info("executorService wait for Termination...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.warn("executorService wait for Termination Exception!!!");
        }
        log.info("executorService is going down");
    }

    public DeviceElement(Device device, SocketChannel socketChannel, MaoPipelineManager boss) {

        stateMachine = new AtomicReference<>(State.INIT);

        this.boss = boss;
        this.device = device;
//        this.dpid = dpid;
        this.socketChannel = socketChannel;
        this.portMap = new HashMap<>();


        recvInputStream = new BufferedInputStream(Channels.newInputStream(socketChannel));

        stateMachine.set(State.INIT_WAIT_PORT);
    }


    public String getPortName(int deviceIntfNumber){
        return portMap.getOrDefault(deviceIntfNumber, "");
    }

    public void removeDeviceElement() {

        if (stateMachine.get() == State.FINISH) {
            log.error("removeDeviceElement ERROR, {} has been FINISH !!!", device.id());
            return;
        }

        stateMachine.set(State.DESTROY);

        try {
            recvInputStream.close();
            log.info("{} close recvInputStream!  when {}", device.id(), stateMachine.get());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socketChannel.close();
            log.info("{} close socket!  when {}", device.id(), stateMachine.get());
        } catch (IOException e) {
            e.printStackTrace();
        }

        portMap.clear();
        log.info("{} clear ports!  when {}", device.id(), stateMachine.get());

        stateMachine.set(State.FINISH);
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }


    public void recvSubmit(SelectionKey key) {
        executorService.submit(new RecvTask(key));
    }


    private class RecvTask implements Callable {

        private final SelectionKey key;

        public RecvTask(SelectionKey key) {
            this.key = key;
        }

        @Override
        public Integer call() {

            //TODO - read socketchannel

            switch (stateMachine.get()) {
                case INIT:

                    log.error("DE is INIT !!!");

                    break;

                case INIT_WAIT_PORT:

                    try {
                        StringBuilder portsBuilder = new StringBuilder();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1);

                        while (true) {
                            byteBuffer.clear();
                            int ret = socketChannel.read(byteBuffer);
                            if (ret == -1) {
                                log.info("{} receive -1, when {}", device.id(), stateMachine.get());
                                stateMachine.set(State.DESTROY);
                                recvSubmit(key);
                                return -1;
                            } else if (ret == 0) {
                                //FIXME should not ret == 0
                                log.error("{} receive 0, when {}", device.id(), stateMachine.get());
                                stateMachine.set(State.DESTROY);
                                recvSubmit(key);
                                return -2;
                            }

                            byte[] one = byteBuffer.array();
//                            log.info("{} get byte {}, when {}", dpid, new String(one), stateMachine.get());
                            if (one[0] != '\n') {
                                portsBuilder.append(new String(one));
                            } else {
                                break;
                            }
                        }

                        String dpPorts = portsBuilder.toString();
                        log.info("{} get ports info: {}", device.id(), dpPorts);

                        String[] dpPortsList = dpPorts.split(",");
                        for (String str : dpPortsList) {
                            String[] sss = str.split("-eth");
                            if (sss.length > 1) {
                                portMap.put(Integer.valueOf(sss[1]), str);
                            }
                        }

                        stateMachine.set(State.STANDBY);
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                        log.info("key set OP_READ on");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case STANDBY:
                    try {

//                        StringBuilder portsBuilder = new StringBuilder();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1);


                        byteBuffer.clear();
                        int ret = socketChannel.read(byteBuffer);

                        if (ret == -1) {
                            log.info("{} receive -1, when {}", device.id(), stateMachine.get());
                            stateMachine.set(State.DESTROY);
                            recvSubmit(key);
                            return -1;
                        } else if (ret == 0) {
                            log.info("{} receive 0, when {}", device.id(), stateMachine.get());
                            stateMachine.set(State.DESTROY);
                            recvSubmit(key);
                            return -2;
                        }


                        byte[] one = byteBuffer.array();
                        log.info("{} get byte {}, when {}", device.id(), new String(one), stateMachine.get());


                        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                        log.info("key set OP_READ on");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case DESTROY:

                    removeDeviceElement();
                    recvSubmit(key);
                    break;

                case FINISH:

                    boss.removeDeviceElement(DeviceElement.this.device.id());
                    log.info("{} remove self from DEmap, when {}", device.id(), stateMachine.get());

                    break;

                default:
                    log.warn("error statemachine!!!");
            }

            return 0;
        }
    }
}
