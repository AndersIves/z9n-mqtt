package cn.z9n.mqtt.configuration;

import cn.z9n.mqtt.config.MqttSubscriberConfig;
import cn.z9n.mqtt.service.MqttSubscribeClientService;
import cn.z9n.mqtt.service.MqttSubscriberRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 18:08
 */
@Configuration
@ConditionalOnProperty(prefix = "z9n.mqtt.subscriber", name = "enable", havingValue = "true")
public class MqttSubscriberConfiguration {

    @Bean("mqttSubscriberRegister")
    protected static MqttSubscriberRegister mqttSubscriberRegister() {
        return new MqttSubscriberRegister();
    }

    @Bean("mqttSubscriberConfig")
    protected MqttSubscriberConfig mqttSubscriberConfig() {
        return new MqttSubscriberConfig();
    }

    @Bean("mqttSubscribeClientService")
    protected MqttSubscribeClientService mqttSubscribeClientService(
            @Autowired ApplicationContext applicationContext,
            @Qualifier("mqttSubscriberConfig") MqttSubscriberConfig mqttSubscriberConfig,
            @Qualifier("mqttSubscriberRegister") MqttSubscriberRegister mqttSubscriberRegister) {
        return new MqttSubscribeClientService(applicationContext, mqttSubscriberConfig, mqttSubscriberRegister);
    }
}
