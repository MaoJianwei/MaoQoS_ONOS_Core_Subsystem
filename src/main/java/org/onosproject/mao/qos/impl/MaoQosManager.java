package org.onosproject.mao.qos.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.api.impl.classify.MaoHtbClassObj;
import org.onosproject.mao.qos.api.impl.qdisc.MaoHtbQdiscObj;
import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.base.MaoQosPolicy;
import org.onosproject.mao.qos.intf.MaoPipelineService;
import org.onosproject.mao.qos.intf.MaoQosService;
import org.onosproject.net.DeviceId;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by mao on 4/1/16.
 */
@Component(immediate = false)
@Service
public class MaoQosManager implements MaoQosService {

    private final Logger log = getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MaoPipelineService maoPipelineService;


    @Activate
    public void activate(ComponentContext context) {
        coreService.registerApplication("org.onosproject.mao.qos");

    }

    @Deactivate
    public void deactivate() {

    }

    @Modified
    public void modify(ComponentContext context) {

    }

    @Override
    public boolean Apply(MaoQosObj qosObj){

        //TODO - check if it will invoke subclass.checkValid
        if(!qosObj.checkValid()){
            return false;
        }


        StringBuilder commandHead = new StringBuilder();

        StringBuilder commandTail = new StringBuilder();

        if(!dealHead(qosObj, commandHead)){
            return false;
        }

        if(!dealTail(qosObj, commandTail)){
            return false;
        }

        MaoQosPolicy qosPolicy = new MaoQosPolicy(qosObj.getDeviceId(),
                                                  qosObj.getDeviceIntfNumber(),
                                                  commandHead.toString(),
                                                  commandTail.toString());

        return maoPipelineService.pushQosPolicy(qosPolicy);
    }
    private boolean dealHead(MaoQosObj qosObj, StringBuilder commandHead){

        commandHead.append("tc ");

        if(qosObj.getObjType() == MaoQosObj.ObjType.QDISC) {

            commandHead.append("qdisc ");
        } else if (qosObj.getObjType() == MaoQosObj.ObjType.CLASS) {

            commandHead.append("class ");
        } else {
            log.error("invalid ObjType");
            return false;
        }

        if(qosObj.getOperateType() == MaoQosObj.OperateType.ADD){

            commandHead.append("add ");
        } else if(qosObj.getOperateType() == MaoQosObj.OperateType.DELETE) {

            commandHead.append("delete ");
        } else {

            log.warn("invalid operation");
            return false;
        }

        commandHead.append("dev ");
        return true;
    }

    private boolean dealTail(MaoQosObj qosObj, StringBuilder commandTail){

        commandTail.append(" ");// " " after Dev Name

        if(qosObj.getObjType() == MaoQosObj.ObjType.QDISC) {

            return dealQdisc(qosObj, commandTail);
        } else if (qosObj.getObjType() == MaoQosObj.ObjType.CLASS) {

            return dealClass(qosObj, commandTail);
        } else {
            log.error("invalid ObjType");
        }

        return false;
    }


    private boolean dealQdisc(MaoQosObj qosObj, StringBuilder commandTail){

        switch(qosObj.getScheduleType()) {

            case HTB:
                return dealQdiscHtb(qosObj, commandTail);

            default:
                log.warn("not handle scheduleType");
        }

        return false;
    }

    private boolean dealQdiscHtb(MaoQosObj qosObj, StringBuilder commandTail){

        MaoHtbQdiscObj maoHtbQdiscObj = (MaoHtbQdiscObj) qosObj;

        commandTail.append("parent ");
        String parent = maoHtbQdiscObj.getParent();
//        if(parent == 0){
//            commandTail.append("root ");
//        } else {
//            commandTail.append(parent + " ");
//        }
        commandTail.append(parent + " ");

        String handle = maoHtbQdiscObj.getHandle();
        commandTail.append("handle " + handle + " ");

        int defaultId = maoHtbQdiscObj.getDefaultId();
        commandTail.append("default "+ defaultId + " ");

        return true;
    }


    private boolean dealClass(MaoQosObj qosObj, StringBuilder commandTail){

        switch(qosObj.getScheduleType()) {

            case HTB:
                return dealClassHtb(qosObj, commandTail);
            default:
                log.warn("not handle scheduleType");
        }

        return false;
    }

    private boolean dealClassHtb(MaoQosObj qosObj, StringBuilder commandTail){

        MaoHtbClassObj maoHtbClassObj = (MaoHtbClassObj) qosObj;

        String parent = maoHtbClassObj.getParent();
        commandTail.append("parent " + parent + " ");

        String classId = maoHtbClassObj.getclassId();
        commandTail.append("classid " + classId + " ");

        commandTail.append("htb ");


        //FIXME - danwei
        long rate = maoHtbClassObj.getRate();
        commandTail.append("rate " + rate + " ");

        long ceil = maoHtbClassObj.getCeil();
        commandTail.append("parent " + parent + " ");

        long burst = maoHtbClassObj.getBurst();
        commandTail.append("parent " + parent + " ");

        long cburst = maoHtbClassObj.getCburst();
        commandTail.append("parent " + parent + " ");

        int priority = maoHtbClassObj.getPriority();
        commandTail.append("parent " + parent + " ");






        return true;
    }
}
