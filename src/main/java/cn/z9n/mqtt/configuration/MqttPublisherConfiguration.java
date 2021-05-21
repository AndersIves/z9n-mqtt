package cn.z9n.mqtt.configuration;

import cn.z9n.mqtt.MqttPublishProcessor;
import cn.z9n.mqtt.config.MqttPublisherConfig;
import cn.z9n.mqtt.service.MqttPublishProcessorImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 18:08
 */
@Configuration
@ConditionalOnProperty(prefix = "z9n.mqtt.publisher", name = "enable", havingValue = "true")
public class MqttPublisherConfiguration {

    @Bean("mqttPublisherConfig")
    protected MqttPublisherConfig mqttPublisherConfig() {
        return new MqttPublisherConfig();
    }

    @Bean("mqttPublishProcessor")
    protected MqttPublishProcessor mqttPublishProcessor(
            @Qualifier("mqttPublisherConfig") MqttPublisherConfig config) {
        return new MqttPublishProcessorImpl(config);
    }
}
