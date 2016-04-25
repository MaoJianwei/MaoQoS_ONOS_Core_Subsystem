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


    public static final class Builder extends MaoQosClassObj.Builder<MaoHtbClassObj.Builder> {

        private long rate;
        private String rateUnit;
        private long ceil;
        private String ceilUnit;
        private long burst;
        private String burstUnit;
        private long cburst;
        private String cburstUnit;
        private int priority;

        private Builder(){
            setScheduleType(ScheduleType.HTB);

            rate = INVALID_INT;
            ceil = INVALID_INT;
            burst = INVALID_INT;
            cburst = INVALID_INT;
            priority = INVALID_INT;
        }

        public Builder rate(long rate, String rateUnit){
            this.rate = rate;
            this.rateUnit = rateUnit;
            return this;
        }
        public Builder ceil(long ceil, String ceilUnit){
            this.ceil = ceil;
            this.ceilUnit = ceilUnit;
            return this;
        }
        public Builder burst(long burst, String burstUnit){
            this.burst = burst;
            this.burstUnit = burstUnit;
            return this;
        }
        public Builder cburst(long cburst, String cburstUnit){
            this.cburst = cburst;
            this.cburstUnit = cburstUnit;
            return this;
        }
        public Builder priority(int priority){
            this.priority = priority;
            return this;
        }


        public MaoHtbClassObj build(){
            MaoHtbClassObj maoHtbClassObj = new MaoHtbClassObj();
            maoHtbClassObj.rate = this.rate;
            maoHtbClassObj.rateUnit = this.rateUnit;
            maoHtbClassObj.ceil = this.ceil;
            maoHtbClassObj.ceilUnit = this.ceilUnit;
            maoHtbClassObj.burst = this.burst;
            maoHtbClassObj.burstUnit = this.burstUnit;
            maoHtbClassObj.cburst = this.cburst;
            maoHtbClassObj.cburstUnit = this.cburstUnit;
            maoHtbClassObj.priority = this.priority;


            return (MaoHtbClassObj)super.build(maoHtbClassObj);
        }

    }
}
