package org.onosproject.mao.qos.api.impl.classify;

import org.onosproject.mao.qos.api.intf.MaoQosClassObj;

/**
 * Created by mao on 4/19/16.
 */
public class MaoHtbClassObj extends MaoQosClassObj {

    private String parent;
    private int classId;
    private long rate;
    private long ceil;
    private long burst;
    private long cburst;
    private int priority;

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

    private MaoHtbClassObj(){

    }


    public static Builder builder(){
        return new Builder();
    }

    public static final class Builder {

        private MaoHtbClassObj ret;

        private Builder(){
            ret = new MaoHtbClassObj();
        }

        public Builder parent(String parent){
            ret.parent = parent;
            return this;
        }
        public Builder classId(int classId){
            ret.classId = classId;
            return this;
        }
        public Builder rate(long rate){
            ret.rate = rate;
            return this;
        }
        public Builder ceil(long ceil){
            ret.ceil = ceil;
            return this;
        }
        public Builder burst(long burst){
            ret.burst = burst;
            return this;
        }
        public Builder cburst(long cburst){
            ret.cburst = cburst;
            return this;
        }
        public Builder priority(int priority){
            ret.priority = priority;
            return this;
        }


        public MaoHtbClassObj build(){
            return ret;
        }

    }
}
