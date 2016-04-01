package org.onosproject.mao.qos.base.callable;

import org.onosproject.mao.qos.base.DeviceElement;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 4/1/16.
 */
public class RecvCallable implements Callable {

    Selector recvSelector;
    AtomicBoolean needShutdown;

    final int SELECT_TIMEOUT = 500;


    public RecvCallable(AtomicBoolean needShutdown){
        this.needShutdown = needShutdown;
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