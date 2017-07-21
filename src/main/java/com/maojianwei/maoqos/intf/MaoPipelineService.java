package com.maojianwei.maoqos.intf;

import com.maojianwei.maoqos.base.MaoQosPolicy;
import com.maojianwei.maoqos.base.DeviceElement;
import org.onosproject.net.DeviceId;

import java.util.Map;

/**
 * Created by mao on 3/27/16.
 */
public interface MaoPipelineService {

    Map<DeviceId, DeviceElement> debug();

    boolean pushQosPolicy(MaoQosPolicy qosPolicy);

}
