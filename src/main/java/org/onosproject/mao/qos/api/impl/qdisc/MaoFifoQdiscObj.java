package org.onosproject.mao.qos.api.impl.qdisc;

import org.onosproject.mao.qos.api.intf.MaoQosQdiscObj;

/**
 * Created by mao on 4/25/16.
 */
public class MaoFifoQdiscObj extends MaoQosQdiscObj {

    public enum FifoType{
        NULL,
        PACKET_FIFO,
        BYTE_FIFO
    }

    private FifoType fifoType;
    private int limit;

    public FifoType getFifoType(){return this.fifoType;}
    public int getLimit(){return this.limit;}

    public static MaoFifoQdiscObj.Builder builder(){
        return new Builder();
    }

    public static final class Builder extends MaoQosQdiscObj.Builder<MaoFifoQdiscObj.Builder> {

        private FifoType fifoType;
        private int limit;

        private Builder(){
            setScheduleType(ScheduleType.FIFO);
            fifoType = FifoType.NULL;
            limit = INVALID_INT;
        }

        public Builder setFifoType(FifoType fifoType){
            this.fifoType = fifoType;
            return this;
        }

        public Builder setLimit(int limit){
            this.limit = limit;
            return this;
        }

        public MaoFifoQdiscObj build(){

            MaoFifoQdiscObj maoFifoQdiscObj = new MaoFifoQdiscObj();
            maoFifoQdiscObj.fifoType = this.fifoType;
            maoFifoQdiscObj.limit = this.limit;

            return (MaoFifoQdiscObj)super.build(maoFifoQdiscObj);
        }
    }
}
