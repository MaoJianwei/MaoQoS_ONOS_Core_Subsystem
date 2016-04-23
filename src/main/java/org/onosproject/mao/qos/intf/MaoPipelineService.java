package org.onosproject.mao.qos.intf;

import org.onosproject.mao.qos.base.DeviceElement;
import org.onosproject.mao.qos.base.MaoQosPolicy;
import org.onosproject.net.DeviceId;

import java.util.Map;

/**
 * Created by mao on 3/27/16.
 */
public interface MaoPipelineService {

    Map<DeviceId, DeviceElement> debug();

    boolean pushQosPolicy(MaoQosPolicy qosPolicy);

}
