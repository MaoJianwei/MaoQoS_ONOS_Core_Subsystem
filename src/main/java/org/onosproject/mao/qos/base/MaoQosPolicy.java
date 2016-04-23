package org.onosproject.mao.qos.base;

import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mao on 4/6/16.
 */
public class MaoQosPolicy {

    private static final Logger log = LoggerFactory.getLogger(MaoQosPolicy.class);

    DeviceId deviceId;
    int deviceIntfNumber;
    String qosCmdHead;
    String qosCmdTail;

    public MaoQosPolicy(DeviceId deviceId, int deviceIntfNumber,
                        String qosCmdHead, String qosCmdTail){
        this.deviceId = deviceId;
        this.deviceIntfNumber = deviceIntfNumber;
        this.qosCmdHead = qosCmdHead;
        this.qosCmdTail = qosCmdTail;
    }

    public boolean checkValid()
    {
        if(this.getDeviceId().equals(DeviceId.NONE)){
            log.error("DeviceId.NONE");
            return false;
        }
        if(this.getDeviceIntfNumber() < 0){
            log.error("DeviceIntfNumber < 0");
            return false;
        }
        if(this.getQosCmdHead().equals("")){
            log.error("CmdHead not set");
            return false;
        }
        if(this.getQosCmdTail().equals("")){
            log.error("CmdTail not set");
            return false;
        }

        return true;
    }

    /**
     * @return dpid string
     */
    public DeviceId getDeviceId(){
        return deviceId != null ? deviceId : DeviceId.NONE;
    }


    public int getDeviceIntfNumber(){
        return deviceIntfNumber;
    }

    /**
     *
     * @return cmd string
     */
    public String getQosCmdHead(){
        return qosCmdHead != null ? qosCmdHead : "";
    }

    /**
     *
     * @return cmd string
     */
    public String getQosCmdTail(){
        return qosCmdTail != null ? qosCmdTail : "";

    }
}
