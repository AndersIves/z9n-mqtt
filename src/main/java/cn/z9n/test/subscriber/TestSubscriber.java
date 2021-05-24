package cn.z9n.test.subscriber;

import cn.z9n.mqtt.MqttSubscribeProcessor;
import cn.z9n.mqtt.annos.MqttSubscriber;
import cn.z9n.mqtt.enums.QosEnum;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 15:37
 */
@Slf4j
@MqttSubscriber(topic = "test1/#", qos = QosEnum.Q_2_EXACTLY_ONCE)
public class TestSubscriber implements MqttSubscribeProcessor {
    @Override
    public void process(String topic, MqttMessage message) throws Exception {
        log.info("topic:{}, message:{}", topic, new String(message.getPayload()));
    }
}
