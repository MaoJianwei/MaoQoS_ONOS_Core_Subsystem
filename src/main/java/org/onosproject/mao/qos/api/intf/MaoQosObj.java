package org.onosproject.mao.qos.api.intf;

import org.onosproject.net.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mao on 4/1/16.
 */
public abstract class MaoQosObj {

    protected static final Logger log = LoggerFactory.getLogger(MaoQosObj.class);


    public enum ObjType{
        NULL,
        QDISC,
        CLASS
    }

    public enum ScheduleType{
        NULL,
        HTB
    }

    public enum OperateType{
        NULL,
        ADD,
        DELETE
    }


    private ObjType objType;
    private ScheduleType scheduleType;
    private OperateType operateType;

    private DeviceId deviceId;
    private int deviceIntfNumber;


    // should be Override and invoke super.checkValid()
    public boolean checkValid()
    {
        if(this.getObjType().equals(ObjType.NULL)){
            log.error("ObjType.NULL");
            return false;
        }
        if(this.getScheduleType().equals(ScheduleType.NULL)){
            log.error("ScheduleType.NULL");
            return false;
        }
        if(this.getOperateType().equals(OperateType.NULL)){
            log.error("OperateType.NULL");
            return false;
        }
        if(this.getDeviceId().equals(DeviceId.NONE)){
            log.error("DeviceId.NONE");
            return false;
        }
        if(this.getDeviceIntfNumber() < 0){
            log.error("DeviceIntfNumber < 0");
            return false;
        }

        return true;
    }



    public ObjType getObjType() {
        return objType != null ? objType : ObjType.NULL;
    }

    public ScheduleType getScheduleType() {
        return scheduleType != null ? scheduleType : ScheduleType.NULL;
    }

    public OperateType getOperateType(){
        return operateType != null ? operateType : OperateType.NULL;
    }

    public DeviceId getDeviceId(){
        return deviceId != null ? deviceId : DeviceId.NONE;
    }

    public int getDeviceIntfNumber(){
        return deviceIntfNumber;
    }





    public void setObjType(ObjType type) {
        objType = type;
    }

    public void setScheduleType(ScheduleType type) {
        scheduleType = type;
    }

    public void setDeviceId(DeviceId deviceId){
        this.deviceId = deviceId;
    }

    public void setDeviceIntfNumber(int deviceIntfNumber) {
        this.deviceIntfNumber = deviceIntfNumber;
    }

    public void add() {
        operateType = OperateType.ADD;
    }

    public void delete() {
        operateType = OperateType.DELETE;
    }
}




























