package org.onosproject.mao.qos.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.api.impl.MaoQosObj;
import org.onosproject.mao.qos.intf.MaoQosService;
import org.osgi.service.component.ComponentContext;

/**
 * Created by mao on 4/1/16.
 */
@Component(immediate = true)
@Service
public class MaoQosManager implements MaoQosService {


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
    public void Apply(MaoQosObj policy){

    }
}
