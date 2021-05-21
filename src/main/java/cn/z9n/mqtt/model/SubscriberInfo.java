package cn.z9n.mqtt.model;

import cn.z9n.mqtt.MqttSubscribeProcessor;
import cn.z9n.mqtt.enums.QosEnum;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 16:31
 */
@Data
@Builder
public class SubscriberInfo {
    private String topic;
    private QosEnum qos;
    private MqttSubscribeProcessor mqttSubscribeProcessor;
    private BlockingQueue<MqttProcessObject> messageQueue;
}
