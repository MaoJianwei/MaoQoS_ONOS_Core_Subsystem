package org.onosproject.mao.qos.api.intf;

/**
 * Created by mao on 4/19/16.
 */
public abstract class MaoQosClassObj extends MaoQosObj {

    protected MaoQosClassObj() {}

    protected static abstract class Builder extends MaoQosObj.Builder {

        protected Builder(){
            setObjType(ObjType.CLASS);
        }

        protected MaoQosClassObj build(MaoQosClassObj maoQosClassObj){

            return (MaoQosClassObj)super.build(maoQosClassObj);
        }
    }
}