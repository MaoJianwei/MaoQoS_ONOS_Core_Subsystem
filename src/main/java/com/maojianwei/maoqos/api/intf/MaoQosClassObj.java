package com.maojianwei.maoqos.api.intf;

/**
 * Created by mao on 4/19/16.
 */
public abstract class MaoQosClassObj extends MaoQosObj {

    protected MaoQosClassObj() {}

    protected static abstract class Builder<SubBuilderClass> extends MaoQosObj.Builder<SubBuilderClass> {

        protected Builder(){
            setObjType(ObjType.CLASS);
        }

        protected MaoQosClassObj build(MaoQosClassObj maoQosClassObj){

            return (MaoQosClassObj)super.build(maoQosClassObj);
        }
    }
}