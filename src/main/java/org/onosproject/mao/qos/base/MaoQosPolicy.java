package org.onosproject.mao.qos.base;

import org.onosproject.mao.qos.api.impl.MaoQosObj;
import org.onosproject.net.Device;

/**
 * Created by mao on 4/6/16.
 */
public class MaoQosPolicy {

    String dpid;
    String qosCmd;

    public MaoQosPolicy(String dpid, String qosCmd){
        this.dpid = dpid;
        this.qosCmd = qosCmd;
    }

    /**
     * @return dpid string
     */
    public String getDeviceId(){
        return dpid;
    }


    /**
     *
     * @return cmd string
     */
    public String getQosCmd(){
        return qosCmd;
    }
}
