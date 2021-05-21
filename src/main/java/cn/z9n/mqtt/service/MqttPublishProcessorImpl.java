package cn.z9n.mqtt.service;

import cn.z9n.mqtt.MqttPublishProcessor;
import cn.z9n.mqtt.config.MqttPublisherConfig;
import cn.z9n.mqtt.service.pool.MqttPublishClientPool;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 14:51
 */
public class MqttPublishProcessorImpl implements MqttPublishProcessor {

    private final MqttPublisherConfig config;

    private final MqttPublishClientPool mqttPublishClientPool;

    public MqttPublishProcessorImpl(MqttPublisherConfig config) {
        this.config = config;
        mqttPublishClientPool = new MqttPublishClientPool(config);
    }

    @PostConstruct
    private void init() {
        mqttPublishClientPool.init();
    }

    @Override
    public void publish(String topic, MqttMessage message) throws Exception {
        mqttPublishClientPool.publish(topic, message);
    }

    @Override
    public void publish(String topic, String message) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes(StandardCharsets.UTF_8));
        mqttMessage.setQos(config.getQos());
        mqttPublishClientPool.publish(topic, mqttMessage);
    }

    @Override
    public void publish(String topic, byte[] message) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message);
        mqttMessage.setQos(config.getQos());
        mqttPublishClientPool.publish(topic, mqttMessage);
    }
}
