package cn.z9n.mqtt.enums;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 14:45
 */
public enum QosEnum {
    /**
     * MQTT设计了一套保证消息稳定传输的机制，包括消息应答、存储和重传。
     * 在这套机制下，提供了三种不同层次QoS（Quality of Service）：
     * <p>
     * QoS0，At most once，至多一次；
     * QoS1，At least once，至少一次；
     * QoS2，Exactly once，确保只有一次。
     * <p>
     * QoS 是消息的发送方（Sender）和接受方（Receiver）之间达成的一个协议：
     * <p>
     * QoS0 代表，Sender 发送的一条消息，Receiver 最多能收到一次，
     * 也就是说 Sender 尽力向 Receiver 发送消息，如果发送失败，也就算了；
     * <p>
     * QoS1 代表，Sender 发送的一条消息，Receiver 至少能收到一次，
     * 也就是说 Sender 向 Receiver 发送消息，如果发送失败，会继续重试，直到 Receiver 收到消息为止，
     * 但是因为重传的原因，Receiver 有可能会收到重复的消息；
     * <p>
     * QoS2 代表，Sender 发送的一条消息，Receiver 确保能收到而且只收到一次，
     * 也就是说 Sender 尽力向 Receiver 发送消息，如果发送失败，会继续重试，
     * 直到 Receiver 收到消息为止，同时保证 Receiver 不会因为消息重传而收到重复的消息。
     * <p>
     * 注意：
     * QoS是Sender和Receiver之间的协议，而不是Publisher和Subscriber之间的协议。
     * 换句话说，Publisher发布了一条QoS1的消息，只能保证Broker能至少收到一次这个消息；
     * 而对于Subscriber能否至少收到一次这个消息，还要取决于Subscriber在Subscibe的时候和Broker协商的QoS等级。
     */
    Q_0_AT_MOST_ONCE(0),
    Q_1_AT_LEAST_ONCE(1),
    Q_2_EXACTLY_ONCE(2);
    private final int qos;

    QosEnum(int qos) {
        this.qos = qos;
    }

    public int getQos() {
        return qos;
    }
}
