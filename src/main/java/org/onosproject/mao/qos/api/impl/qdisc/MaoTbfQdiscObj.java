package org.onosproject.mao.qos.api.impl.qdisc;

import org.onosproject.mao.qos.api.intf.MaoQosQdiscObj;

/**
 * Created by mao on 4/25/16.
 */
public class MaoTbfQdiscObj extends MaoQosQdiscObj {

    private long rate;
    private String rateUnit;
    private long burst;
    private String burstUnit;
    private long latency;
    private String latencyUnit;
    private long limit;
    private String limitUnit;

    @Override
    public boolean checkValid()
    {
        if(!super.checkValid()){
            return false;
        }

        if(rate == INVALID_INT && burst == INVALID_INT){
            return false;
        }

        if(!(latency != INVALID_INT || limit != INVALID_INT) || (latency != INVALID_INT && limit != INVALID_INT)){
            return false;
        }

        return true;
    }

    public long getRate(){
        return rate;
    }
    public String getRateUnit(){
        return rateUnit;
    }
    public long getBurst(){
        return burst;
    }
    public String getBurstUnit(){
        return burstUnit;
    }
    public long getLatency(){
        return latency;
    }
    public String getLatencyUnit(){
        return latencyUnit;
    }
    public long getLimit(){
        return limit;
    }
    public String getLimitUnit(){
        return limitUnit;
    }

    public static MaoTbfQdiscObj.Builder builder(){
        return new Builder();
    }

    public static final class Builder extends MaoQosQdiscObj.Builder<MaoTbfQdiscObj.Builder> {

        private long rate;
        private String rateUnit;
        private long burst;
        private String burstUnit;
        private long latency;
        private String latencyUnit;
        private long limit;
        private String limitUnit;

        private Builder(){
            setScheduleType(ScheduleType.TBF);
            this.rate = INVALID_INT;
            this.burst = INVALID_INT;
            this.latency = INVALID_INT;
            this.limit = INVALID_INT;
        }

        public Builder rate(int rate, String rateUnit){
            this.rate = rate;
            this.rateUnit = rateUnit;
            return this;
        }
        public Builder burst(int burst, String burstUnit){
            this.burst = burst;
            this.burstUnit = burstUnit;
            return this;
        }
        public Builder latency(int latency, String latencyUnit){
            this.latency = latency;
            this.latencyUnit = latencyUnit;
            return this;
        }
        public Builder limit(int limit, String limitUnit){
            this.limit = limit;
            this.limitUnit = limitUnit;
            return this;
        }

        public MaoTbfQdiscObj build(){

            MaoTbfQdiscObj maoTbfQdiscObj = new MaoTbfQdiscObj();
            maoTbfQdiscObj.rate = this.rate;
            maoTbfQdiscObj.rateUnit = this.rateUnit;
            maoTbfQdiscObj.burst = this.burst;
            maoTbfQdiscObj.burstUnit = this.burstUnit;
            maoTbfQdiscObj.latency = this.latency;
            maoTbfQdiscObj.latencyUnit = this.latencyUnit;
            maoTbfQdiscObj.limit = this.limit;
            maoTbfQdiscObj.limitUnit = this.limitUnit;

            return (MaoTbfQdiscObj)super.build(maoTbfQdiscObj);
        }
    }
}
