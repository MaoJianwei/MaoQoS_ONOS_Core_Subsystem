package org.onosproject.mao.qos.api.intf;

/**
 * Created by mao on 4/1/16.
 */
public abstract class MaoQosObj {

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
    private String deviceIntf;


    public ObjType getObjType() {
        return objType != null ? objType : ObjType.NULL;
    }

    public ScheduleType getScheduleType() {
        return scheduleType != null ? scheduleType : ScheduleType.NULL;
    }

    public OperateType getOperateType(){
        return operateType != null ? operateType : OperateType.NULL;
    }

    public String getDeviceIntf(){
        return deviceIntf != null ? deviceIntf : "";
    }



    public void setObjType(ObjType type) {
        objType = type;
    }

    public void setScheduleType(ScheduleType type) {
        scheduleType = type;
    }

    public void setDeviceIntf(String deviceIntf){
        this.deviceIntf = deviceIntf;
    }

    public void add() {
        operateType = OperateType.ADD;
    }

    public void delete() {
        operateType = OperateType.DELETE;
    }
}




























