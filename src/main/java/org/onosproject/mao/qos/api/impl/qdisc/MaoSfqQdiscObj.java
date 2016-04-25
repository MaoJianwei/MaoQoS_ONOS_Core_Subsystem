package org.onosproject.mao.qos.api.impl.qdisc;

import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.api.intf.MaoQosQdiscObj;

/**
 * Created by mao on 4/25/16.
 */
public class MaoSfqQdiscObj extends MaoQosQdiscObj {


    private int perturb;

    public int getPerturb(){return this.perturb;}


    public static final class Builder extends MaoQosQdiscObj.Builder<MaoSfqQdiscObj.Builder> {

        private int perturb;

        private Builder(){
            setScheduleType(ScheduleType.SFQ);
            this.perturb = INVALID_INT;
        }

        public Builder setPerturb(int perturb){
            this.perturb = perturb;
            return this;
        }

        public MaoSfqQdiscObj build(){

            MaoSfqQdiscObj maoSfqQdiscObj = new MaoSfqQdiscObj();
            maoSfqQdiscObj.perturb = this.perturb;

            return (MaoSfqQdiscObj)super.build(maoSfqQdiscObj);
        }
    }
}
