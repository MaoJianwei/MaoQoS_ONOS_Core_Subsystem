package org.onosproject.mao.qos.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.api.impl.qdisc.MaoHtbQdiscObj;
import org.onosproject.mao.qos.api.intf.MaoQosObj;
import org.onosproject.mao.qos.intf.MaoQosService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by mao on 4/1/16.
 */
@Component(immediate = true)
@Service
public class MaoQosManager implements MaoQosService {

    private final Logger log = getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;


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

        StringBuilder command = new StringBuilder();
        command.append("tc ");

        if(qosObj.getObjType() == MaoQosObj.ObjType.QDISC) {

            dealQdisc(qosObj, command);
        } else if (qosObj.getObjType() == MaoQosObj.ObjType.CLASS) {

            dealClass(qosObj, command);
        } else {
            log.info("invalid ObjType");
            return false;
        }





        return true;
    }

    private boolean dealQdisc(MaoQosObj qosObj, StringBuilder command){

        command.append("qdisc ");


        if(qosObj.getOperateType() == MaoQosObj.OperateType.ADD){

            command.append("add ");
        } else if(qosObj.getOperateType() == MaoQosObj.OperateType.DELETE) {

            command.append("delete ");
        } else {

            log.warn("invalid operation");
            return false;
        }

        if(qosObj.getDeviceIntf().equals("")){
            log.warn("invalid deviceIntf");
            return false;
        }
        command.append("dev " + qosObj.getDeviceIntf() + " ");


        switch(qosObj.getScheduleType()) {

            case HTB:

                dealQdiscHtb(qosObj, command);
                break;

            default:
                log.warn("invalid scheduleType");
        }


        return true;
    }

    private boolean dealQdiscHtb(MaoQosObj qosObj, StringBuilder command){

        MaoHtbQdiscObj maoHtbQdiscObj = (MaoHtbQdiscObj) qosObj;

        command.append("parent ");
        int parent = maoHtbQdiscObj.getParent();
        if(parent == 0){
            command.append("root ");
        } else {
            command.append(parent + " ");
        }

        int handle = maoHtbQdiscObj.getHandle();
        command.append("handle " + handle + " ");

        int defaultId = maoHtbQdiscObj.getDefaultId();
        command.append("default "+ defaultId + " ");

        return true;
    }





    private void dealClass(MaoQosObj qosObj, StringBuilder command){

        switch(qosObj.getScheduleType()) {

            case HTB:

                break;
        }
    }
}







































