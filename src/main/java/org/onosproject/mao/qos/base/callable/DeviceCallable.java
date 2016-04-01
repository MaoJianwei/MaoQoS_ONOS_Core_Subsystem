package org.onosproject.mao.qos.base.callable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/1/16.
 */
public class DeviceCallable implements Callable {

    Selector acceptSelector;
    ServerSocketChannel listenSocketChannel;
    AtomicBoolean needShutdown;

    final int LISTEN_PORT = 5511;


    public DeviceCallable(AtomicBoolean needShutdown){

        this.needShutdown = needShutdown;
    }


    @Override
    public Integer call(){

        init();

        while(true){
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


}
