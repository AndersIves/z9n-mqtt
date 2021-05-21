package cn.z9n.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 12:37
 */
@Data
@ConfigurationProperties(prefix = "z9n.mqtt.subscriber")
public class MqttSubscriberConfig {

    @PostConstruct
    private void init() {
        if (enable) {
            String prefix = this.getClass().getAnnotation(ConfigurationProperties.class).prefix();
            if (!StringUtils.hasText(ip)) {
                throw new RuntimeException("subscriber未配置mqtt broker ip: {" + prefix + ".ip}");
            }
            if (!StringUtils.hasText(port)) {
                throw new RuntimeException("subscriber未配置mqtt broker 端口: {" + prefix + ".port}");
            }
            if (enableRandomClientId) {
                clientId = UUID.randomUUID().toString().replaceAll("-", "");
            } else if (!StringUtils.hasText(clientId)) {
                throw new RuntimeException("subscriber mqtt客户端id是否随机生成配置为false时, 未配置mqtt 客户端id: {" + prefix + ".port}");
            }
            if (subscriberQueueCapacity <= 0) {
                throw new RuntimeException("subscriber mqtt消费队列大小必须大于0: {" + prefix + ".port}");
            }
        }
    }

    /**
     * 是否开启mqtt客户端
     */
    private boolean enable = false;

    /**
     * mqtt broker ip
     */
    private String ip;

    /**
     * mqtt broker 端口
     */
    private String port;

    /**
     * mqtt broker 用户名
     */
    private String userName;

    /**
     * mqtt broker 密码
     */
    private String password;

    /**
     * mqtt 客户端id是否随机生成
     */
    private boolean enableRandomClientId = true;

    /**
     * mqtt 客户端id
     */
    private String clientId;

    /**
     * 连接超时时间 秒
     */
    private int connectionTimeout = 5;

    /**
     * 是否为 临时会话, 客户端断开broker不再推送
     */
    private boolean enableCleanSession = true;

    /**
     * 每个消费者缓存队列大小
     */
    private int subscriberQueueCapacity = 100;

    /**
     * 消费客户端重连最大次数
     */
    private int subscriberClientReconnectTimes = 5;
}
