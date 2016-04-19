package org.onosproject.mao.qos.api.impl.qdisc;

import org.onosproject.mao.qos.api.intf.MaoQosQdiscObj;

/**
 * Created by mao on 4/19/16.
 */
public class MaoHtbQdiscObj extends MaoQosQdiscObj {

    private int parent;
    private int handle;
    private int defaultId;


//    private MaoHtbQdiscObj(
//            int parent,
//            int handle,
//            int defaultId,
//            String device,
//            String intf){
//
//        this.parent = parent;
//        this.handle = handle;
//        this.defaultId = defaultId;
//        this.device = device;
//        this.intf = intf;
//    }

    private MaoHtbQdiscObj(){

    }


    /**
     * 0 for root
     * @return
     */
    public int getParent(){
        return parent;
    }
    public int getHandle(){
        return handle;
    }
    public int getDefaultId(){
        return defaultId;
    }


    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {

        private MaoHtbQdiscObj ret;

        private Builder(){
            ret = new MaoHtbQdiscObj();
        }

        public Builder parent(int parent){
            ret.parent = parent;
            return this;
        }
        public Builder handle(int handle){
            ret.handle = handle;
            return this;
        }
        public Builder defaultId(int defaultId){
            ret.defaultId = defaultId;
            return this;
        }
        public Builder deviceIntf(String deviceIntf) {
            ret.setDeviceIntf(deviceIntf);
            return this;
        }


        public MaoHtbQdiscObj build(){
            return ret;
        }
    }
}
