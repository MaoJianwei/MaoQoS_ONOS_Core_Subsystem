package com.maojianwei.maoqos.api.impl.qdisc;

import com.maojianwei.maoqos.api.intf.MaoQosQdiscObj;
import org.onosproject.net.DeviceId;
import org.onosproject.net.flow.DefaultTrafficSelector;

/**
 * Created by mao on 4/19/16.
 */
public class MaoHtbQdiscObj extends MaoQosQdiscObj {


    private int defaultId;


    private MaoHtbQdiscObj(){
    }

    @Override
    public boolean checkValid()
    {
        if(!super.checkValid()){
            return false;
        }
//        if(defaultId == INVALID_INT){
//            return false;
//        }

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


    public int getDefaultId(){
        return defaultId;
    }

    public static MaoHtbQdiscObj.Builder builder(){
        return new Builder();
    }


    public static final class Builder extends MaoQosQdiscObj.Builder<MaoHtbQdiscObj.Builder> {

        private int defaultId;

        private Builder(){
            setScheduleType(ScheduleType.HTB);
            defaultId = INVALID_INT;
        }

        public Builder setDefaultId(int defaultId){
            this.defaultId = defaultId;
            return this;
        }

        public MaoHtbQdiscObj build(){

            MaoHtbQdiscObj maoHtbQdiscObj = new MaoHtbQdiscObj();
            maoHtbQdiscObj.defaultId = this.defaultId;

            return (MaoHtbQdiscObj)super.build(maoHtbQdiscObj);
        }
    }
}
