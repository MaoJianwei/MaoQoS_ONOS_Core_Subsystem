package org.onosproject.mao.qos.api.impl;

/**
 * Created by mao on 4/1/16.
 */
public class MaoQosObj {

    private MaoQosObj(){

    }

    public String getDevice(){
        return "";
    }





    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {

        public MaoQosObj build(){
            return new MaoQosObj();
        }

    }

}
