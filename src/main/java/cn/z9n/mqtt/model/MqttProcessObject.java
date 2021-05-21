package cn.z9n.mqtt.model;

import lombok.Builder;
import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 18:44
 */
@Data
@Builder
public class MqttProcessObject {
    private String topic;
    private MqttMessage message;
}
