package org.onosproject.mao.qos.api.impl.classify;

import org.onosproject.mao.qos.api.intf.MaoQosClassObj;
import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultFlowEntry;
import org.onosproject.net.flow.DefaultTrafficSelector;

/**
 * Created by mao on 4/19/16.
 */
public class MaoHtbClassObj extends MaoQosClassObj {

    private long rate;
    private String rateUnit;
    private long ceil;
    private String ceilUnit;
    private long burst;
    private String burstUnit;
    private long cburst;
    private String cburstUnit;
    private int priority;


    private MaoHtbClassObj(){
        rate = INVALID_INT;
        ceil = INVALID_INT;
        burst = INVALID_INT;
        cburst = INVALID_INT;
        priority = INVALID_INT;
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



    public long getRate(){
        return rate;
    }
    public String getRateUnit(){
        return rateUnit;
    }
    public long getCeil(){
        return ceil;
    }
    public String getCeilUnit(){
        return ceilUnit;
    }
    public long getBurst(){
        return burst;
    }
    public String getBurstUnit(){
        return burstUnit;
    }
    public long getCburst(){
        return cburst;
    }
    public String getCburstUnit(){
        return cburstUnit;
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

        public Builder parent(MaoQosObj parent){
            ret.setParent(parent);
            return this;
        }
        public Builder classId(String classId){
            ret.setHandleOrClassId(classId);
            return this;
        }
        public Builder rate(long rate, String rateUnit){
            ret.rate = rate;
            ret.rateUnit = rateUnit;
            return this;
        }
        public Builder ceil(long ceil, String ceilUnit){
            ret.ceil = ceil;
            ret.ceilUnit = ceilUnit;
            return this;
        }
        public Builder burst(long burst, String burstUnit){
            ret.burst = burst;
            ret.burstUnit = burstUnit;
            return this;
        }
        public Builder cburst(long cburst, String cburstUnit){
            ret.cburst = cburst;
            ret.cburstUnit = cburstUnit;
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
