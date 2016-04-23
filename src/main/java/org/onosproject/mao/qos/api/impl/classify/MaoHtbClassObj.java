package org.onosproject.mao.qos.api.impl.classify;

import org.onosproject.mao.qos.api.intf.MaoQosClassObj;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultFlowEntry;
import org.onosproject.net.flow.DefaultTrafficSelector;

/**
 * Created by mao on 4/19/16.
 */
public class MaoHtbClassObj extends MaoQosClassObj {

    private String parent;
    private String classId;
    private long rate;
    private long ceil;
    private long burst;
    private long cburst;
    private int priority;


    private MaoHtbClassObj(){
        parent = "";
        classId = "";
        rate = -1;
        ceil = -1;
        burst = -1;
        cburst = -1;
        priority = -1;
    }

    @Override
    public boolean checkValid()
    {
        if(!super.checkValid()){
            return false;
        }

//        if(this.getObjType().equals(ObjType.NULL)){
//            log.error("ObjType.NULL");
//            return false;
//        }
//        if(this.getScheduleType().equals(ScheduleType.NULL)){
//            log.error("ScheduleType.NULL");
//            return false;
//        }
//        if(this.getOperateType().equals(OperateType.NULL)){
//            log.error("OperateType.NULL");
//            return false;
//        }
//        if(this.getDeviceId().equals(DeviceId.NONE)){
//            log.error("DeviceId.NONE");
//            return false;
//        }
//        if(this.getDeviceIntfNumber() < 0){
//            log.error("DeviceIntfNumber < 0");
//            return false;
//        }

        return true;
    }



    public String getParent(){
        return parent;
    }
    public String getclassId(){
        return classId;
    }
    public long getRate(){
        return rate;
    }
    public long getCeil(){
        return ceil;
    }
    public long getBurst(){
        return burst;
    }
    public long getCburst(){
        return cburst;
    }
    public int getPriority(){
        return priority;
    }


    public static MaoHtbClassObj.Builder builder() {
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
        public Builder classId(String classId){
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
