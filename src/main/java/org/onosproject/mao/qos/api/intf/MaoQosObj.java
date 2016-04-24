package org.onosproject.mao.qos.api.intf;

import org.onosproject.net.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mao on 4/1/16.
 */
public abstract class MaoQosObj {

    protected static final Logger log = LoggerFactory.getLogger(MaoQosObj.class);

    public static final String ROOT_NAME = "root";
    public static final int INVALID_INT = -1;


    public static final MaoQosObj ROOT = new MaoQosObj(ROOT_NAME) {

        // for compatibility
        @Override
        public boolean checkValid() {
            return true;
        }
    };


    public static final String RATE_BIT = "bit";
    public static final String RATE_KBIT = "kbit";
    public static final String RATE_MBIT = "mbit";
    public static final String RATE_GBIT = "gbit";
    public static final String RATE_TBIT = "tbit";

    public static final String RATE_BYTE = "bps";
    public static final String RATE_KBYTE = "kbps";
    public static final String RATE_MBYTE = "mbps";
    public static final String RATE_GBYTE = "gbps";
    public static final String RATE_TBYTE = "tbps";


    public static final String SIZE_KBIT = "kbit";
    public static final String SIZE_MBIT = "mbit";
    public static final String SIZE_GBIT = "gbit";


    public static final String SIZE_BYTE = "b";
    public static final String SIZE_KBYTE = "k";
    public static final String SIZE_MBYTE = "m";
    public static final String SIZE_GBYTE = "g";


    public enum ObjType {
        NULL,
        QDISC,
        CLASS
    }

    public enum ScheduleType {
        NULL,
        HTB
    }

    public enum OperateType {
        NULL,
        ADD,
        DELETE
    }


    private ObjType objType;
    private ScheduleType scheduleType;
    private OperateType operateType;

    private DeviceId deviceId;
    private int deviceIntfNumber;

    private MaoQosObj parent;
    private String handleOrClassId;


    protected MaoQosObj() {
        objType = ObjType.NULL;
        scheduleType = ScheduleType.NULL;
        operateType = OperateType.NULL;
    }

    private MaoQosObj(String root) {
        this.handleOrClassId = root;
    }


    // should be Override and invoke super.checkValid()
    public boolean checkValid() {
        if (this.getObjType().equals(ObjType.NULL)) {
            log.error("ObjType.NULL");
            return false;
        }
        if (this.getScheduleType().equals(ScheduleType.NULL)) {
            log.error("ScheduleType.NULL");
            return false;
        }
        if (this.getOperateType().equals(OperateType.NULL)) {
            log.error("OperateType.NULL");
            return false;
        }
        if (this.getDeviceId().equals(DeviceId.NONE)) {
            log.error("DeviceId.NONE");
            return false;
        }
        if (this.getDeviceIntfNumber() < 0) {
            log.error("DeviceIntfNumber < 0");
            return false;
        }

        //FIXME - attention -  not all need parent and handleOrClassId

        return true;
    }


    public ObjType getObjType() {
        return objType != null ? objType : ObjType.NULL;
    }

    public ScheduleType getScheduleType() {
        return scheduleType != null ? scheduleType : ScheduleType.NULL;
    }

    public OperateType getOperateType() {
        return operateType != null ? operateType : OperateType.NULL;
    }

    public DeviceId getDeviceId() {
        return deviceId != null ? deviceId : DeviceId.NONE;
    }

    public int getDeviceIntfNumber() {
        return deviceIntfNumber;
    }

    public String getParentId() {
        return parent.getHandleOrClassId();
    }

    public String getHandleOrClassId() {
        return handleOrClassId;
    }

    //for inherit
    protected static abstract class Builder<SubBuilderClass> {

        private ObjType objType;
        private ScheduleType scheduleType;
        private OperateType operateType;

        private DeviceId deviceId;
        private int deviceIntfNumber;

        private MaoQosObj parent;
        private String handleOrClassId;

//        private SubBuilderClass subBuilder;
//        protected void initBuilder(){
//
//        }

        protected SubBuilderClass setObjType(ObjType type) {
            this.objType = type;
            return (SubBuilderClass)this;
        }

        protected SubBuilderClass setScheduleType(ScheduleType type) {
            this.scheduleType = type;
            return (SubBuilderClass)this;
        }

        public SubBuilderClass setDeviceId(DeviceId deviceId) {
            this.deviceId = deviceId;
            return (SubBuilderClass)this;
        }

        public SubBuilderClass setDeviceIntfNumber(int deviceIntfNumber) {
            this.deviceIntfNumber = deviceIntfNumber;
            return (SubBuilderClass)this;
        }

        public SubBuilderClass setParent(MaoQosObj qosObj) {
            this.parent = qosObj;
            return (SubBuilderClass)this;
        }

        public SubBuilderClass setHandleOrClassId(String handleOrClassId) {
            this.handleOrClassId = handleOrClassId;
            return (SubBuilderClass)this;
        }

        public SubBuilderClass add() {
            this.operateType = OperateType.ADD;
            return (SubBuilderClass)this;
        }

        public SubBuilderClass delete() {
            this.operateType = OperateType.DELETE;
            return (SubBuilderClass)this;
        }

        protected MaoQosObj build(MaoQosObj maoQosObj) {

            maoQosObj.objType = this.objType;
            maoQosObj.scheduleType = this.scheduleType;
            maoQosObj.operateType = this.operateType;
            maoQosObj.deviceId = this.deviceId;
            maoQosObj.deviceIntfNumber = this.deviceIntfNumber;
            maoQosObj.parent = this.parent;
            maoQosObj.handleOrClassId = this.handleOrClassId;

            return maoQosObj;
        }
    }
}




























