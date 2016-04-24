package org.onosproject.mao.qos.api.intf;

/**
 * Created by mao on 4/19/16.
 */
public abstract class MaoQosQdiscObj extends MaoQosObj {

    protected MaoQosQdiscObj() {}

    protected static abstract class Builder<SubBuilderClass> extends MaoQosObj.Builder<SubBuilderClass> {

        protected Builder(){
            setObjType(ObjType.QDISC);
        }

        protected MaoQosQdiscObj build(MaoQosQdiscObj maoQosQdiscObj){

            return (MaoQosQdiscObj)super.build(maoQosQdiscObj);
        }
    }
}
