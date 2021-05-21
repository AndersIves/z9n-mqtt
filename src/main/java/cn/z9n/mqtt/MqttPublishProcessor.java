package cn.z9n.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * mqtt 消息发送者
 * <p>
 * 若需要推送则注入该接口 调用publish方法
 *
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 14:51
 */
public interface MqttPublishProcessor {
    /**
     * 发送消息
     *
     * @param topic
     * @param message
     */
    void publish(String topic, MqttMessage message) throws  Exception;

    void publish(String topic, String message) throws Exception;

    void publish(String topic, byte[] message) throws Exception;
}
