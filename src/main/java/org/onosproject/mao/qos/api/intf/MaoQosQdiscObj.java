package org.onosproject.mao.qos.api.intf;

/**
 * Created by mao on 4/19/16.
 */
public abstract class MaoQosQdiscObj extends MaoQosObj {

    public MaoQosQdiscObj(){
        setObjType(ObjType.QDISC);
    }
}
