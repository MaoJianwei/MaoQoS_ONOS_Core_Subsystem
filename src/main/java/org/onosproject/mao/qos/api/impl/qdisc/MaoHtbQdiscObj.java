package org.onosproject.mao.qos.api.impl.qdisc;

import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.api.intf.MaoQosQdiscObj;
import org.onosproject.net.DeviceId;

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



    public static final class Builder {

        private MaoHtbQdiscObj ret;

        private Builder(){
            ret = new MaoHtbQdiscObj();
        }

        public Builder parent(MaoQosObj parent){
            ret.setParent(parent);
            return this;
        }
        public Builder handle(String handle){
            ret.setHandleOrClassId(handle);
            return this;
        }
        public Builder defaultId(int defaultId){
            ret.defaultId = defaultId;
            return this;
        }
        public Builder deviceIntfNumber(int deviceIntfNumber) {
            ret.setDeviceIntfNumber(deviceIntfNumber);
            return this;
        }


        public MaoHtbQdiscObj build(){
            return ret;
        }
    }
}
