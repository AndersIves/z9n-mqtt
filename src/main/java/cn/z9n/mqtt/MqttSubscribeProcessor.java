package cn.z9n.mqtt;

import cn.z9n.mqtt.service.MqttSubscribeClientService;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * mqtt 消息消费者
 * <p>
 * 若需要消费则实现该接口 并打上MqttSubscriber注解
 * <p>
 * MqttSubscriber注解：
 * @see MqttPublishProcessor
 * <p>
 * 用于在初始化bean前加载需要订阅的类
 * 扫描注解的类
 * @see cn.z9n.mqtt.service.MqttSubscriberRegister
 * <p>
 * 扫描注解后加载MqttClientService bean的时候会在初始化方法中完成mqtt客户端的建立及连接
 * 并且MqttClientService实现了topic分发方法
 * MqttClientService:
 * @see MqttSubscribeClientService
 *
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 15:32
 */
public interface MqttSubscribeProcessor {
    /**
     * 消费方法
     *  @param topic
     * @param message
     */
    void process(String topic, MqttMessage message) throws Exception;
}
