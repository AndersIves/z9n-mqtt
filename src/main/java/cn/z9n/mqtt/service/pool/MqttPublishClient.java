package cn.z9n.mqtt.service.pool;

import cn.z9n.mqtt.config.MqttPublisherConfig;
import cn.z9n.mqtt.model.SubscriberInfo;
import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/21 9:48
 */
@Slf4j
public class MqttPublishClient {

    private final MqttPublisherConfig config;
    private final String clientId;

    private final String serverUrl;

    private final MqttConnectOptions options;

    public MqttPublishClient(int clientId, MqttPublisherConfig config) {
        this.config = config;
        this.clientId = config.getClientId() + "-" + clientId;
        serverUrl = "tcp://" + config.getIp() + ":" + config.getPort();

        options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(config.getConnectionTimeout());
        if (StringUtils.hasText(config.getUserName()) && StringUtils.hasText(config.getPassword())) {
            options.setUserName(config.getUserName());
            options.setPassword(config.getPassword().toCharArray());
        }
    }

    private MqttClient mqttClient;


    public void connect() throws MqttException {
        mqttClient = new MqttClient(serverUrl, clientId, new MemoryPersistence());
        // 连接
        mqttClient.connect(options);
        log.debug("mqtt publish客户端连接成功, ip:{}, 端口:{}, clientId:{}", config.getIp(), config.getPort(), clientId);
    }

    public void publish(String topic, MqttMessage message) throws MqttException {
        mqttClient.publish(topic, message);
    }
}
