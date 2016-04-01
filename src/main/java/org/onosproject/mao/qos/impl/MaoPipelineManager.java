package org.onosproject.mao.qos.impl;

import com.google.common.util.concurrent.AtomicLongMap;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onosproject.core.CoreService;
import org.onosproject.mao.qos.base.DeviceElement;
import org.onosproject.mao.qos.intf.MaoPipelineService;
import org.onosproject.net.Device;
import org.onosproject.net.Port;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.DefaultFlowEntry;
import org.onosproject.net.flow.FlowEntry;
import org.osgi.service.component.ComponentContext;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mao on 3/27/16.
 */
@Component(immediate = true)
@Service
public class MaoPipelineManager implements MaoPipelineService {

    LinkedBlockingQueue<String> policyQueue;
    AtomicBoolean needShutdown;
    ConcurrentMap<String, DeviceElement> DeviceElementMap;


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

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


    private void startModule() {

    }

    private void shutdownModule() {

    }



    public void pushQosPolicy(){

    }








//    private class InnerDeviceListener implements DeviceListener {
//
//
//        @Override
//        public void event(DeviceEvent ev) {
//
//            switch (ev.type()) {
//                case DEVICE_ADDED:
//                case DEVICE_UPDATED:
//                case DEVICE_REMOVED:
//                case DEVICE_SUSPENDED:
//                case DEVICE_AVAILABILITY_CHANGED:
//                    Device device = ev.subject();
//                    DeviceEvent.Type type = ev.type();
//                    Port port = ev.port();
//                    long time = ev.time();
//                    String str = ev.toString();
//                    break;
//                default:
//                    break;
//
//            }
//
//        }
//    }

}
