package org.onosproject.mao.qos.intf;

import org.onosproject.mao.qos.base.DeviceElement;

import java.util.Map;

/**
 * Created by mao on 3/27/16.
 */
public interface MaoPipelineService {

    Map<String, DeviceElement> debug();

    void pushQosPolicy();

}
