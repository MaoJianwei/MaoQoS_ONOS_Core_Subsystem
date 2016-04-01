package org.onosproject.mao.qos.base;

import org.onosproject.net.Device;

import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;

/**
 * Created by mao on 4/1/16.
 */
public class DeviceElement {

    Device device;
    SocketChannel socketChannel;

    public DeviceElement(Device device, SocketChannel socketChannel) {
        this.device = device;
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel(){
        return socketChannel;
    }


    public void recvSubmit(){

    }


}
