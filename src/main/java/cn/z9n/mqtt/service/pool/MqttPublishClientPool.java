package cn.z9n.mqtt.service.pool;

import cn.z9n.mqtt.config.MqttPublisherConfig;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/21 9:48
 */
@Slf4j
public class MqttPublishClientPool {

    private final MqttPublisherConfig config;

    public MqttPublishClientPool(MqttPublisherConfig config) {
        this.config = config;
    }

    private ArrayBlockingQueue<MqttPublishClient> mqttPublishClientQueue;

    public void init() {
        log.debug("mqtt publisher连接池 开始初始化, 连接池大小:{}", config.getConnectPoolSize());
        mqttPublishClientQueue = new ArrayBlockingQueue<>(
                config.getConnectPoolSize(),
                false,
                IntStream.range(0, config.getConnectPoolSize())
                        .parallel()
                        .mapToObj(i -> {
                            MqttPublishClient mqttPublishClient = new MqttPublishClient(i, config);
                            try {
                                mqttPublishClient.connect();
                            } catch (MqttException e) {
                                throw new RuntimeException(e);
                            }
                            return mqttPublishClient;
                        })
                        .collect(Collectors.toList())
        );
        log.info("mqtt publisher连接池初始化完成, ip:{}, 端口:{}, clientId前缀:{}-, 连接池大小:{}", config.getIp(), config.getPort(), config.getClientId(), mqttPublishClientQueue.size());
    }

    public void publish(String topic, MqttMessage message) throws Exception {
//        System.out.println(mqttPublishClientQueue.size()+ "/"+config.getConnectPoolSize());
        MqttPublishClient client = mqttPublishClientQueue.poll(config.getConnectPoolPollTimeout(), TimeUnit.MILLISECONDS);
        if (client == null) {
            log.error("mqtt publisher连接池暂无就绪客户端, 当前连接池大小:{}", config.getConnectPoolSize());
            throw new RuntimeException("mqtt publisher连接池暂无就绪客户端");
        }
        try {
            client.publish(topic, message);
            mqttPublishClientQueue.put(client);
        } catch (MqttException e) {
            mqttPublishClientQueue.put(client);
            throw e;
        } catch (Exception e) {
            mqttPublishClientQueue.put(client);
        }
    }
}
