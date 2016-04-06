package org.onosproject.mao.qos.base;

import org.onosproject.net.Device;

import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by mao on 4/1/16.
 */
public class DeviceElement {

    Device device;
    SocketChannel socketChannel;
    ExecutorService executorService;

    public DeviceElement(Device device, SocketChannel socketChannel) {
        this.device = device;
        this.socketChannel = socketChannel;
        executorService = Executors.newSingleThreadExecutor();
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
        }

    }


}
