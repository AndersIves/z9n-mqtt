package cn.z9n.mqtt.service;

import cn.z9n.mqtt.MqttSubscribeProcessor;
import cn.z9n.mqtt.annos.MqttSubscriber;
import cn.z9n.mqtt.config.MqttSubscriberConfig;
import cn.z9n.mqtt.enums.QosEnum;
import cn.z9n.mqtt.model.MqttProcessObject;
import cn.z9n.mqtt.model.SubscriberInfo;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 15:52
 */
@Slf4j
public class MqttSubscribeClientService {

    private final MqttSubscriberConfig config;

    private MqttClient mqttClient;

    private final MqttConnectOptions options;

    private final ApplicationContext applicationContext;

    private final MqttCallback mqttCallback;

    private final List<SubscriberInfo> subscriberInfoList;

    private final String serverUrl;

    public MqttSubscribeClientService(ApplicationContext applicationContext, MqttSubscriberConfig config, MqttSubscriberRegister mqttSubscriberRegister) {
        this.config = config;
        this.applicationContext = applicationContext;

        serverUrl = "tcp://" + config.getIp() + ":" + config.getPort();

        subscriberInfoList = convert(mqttSubscriberRegister.getMqttSubscribeProcessorBeanNames());

        options = new MqttConnectOptions();
        options.setConnectionTimeout(config.getConnectionTimeout());
        if (StringUtils.hasText(config.getUserName()) && StringUtils.hasText(config.getPassword())) {
            options.setUserName(config.getUserName());
            options.setPassword(config.getPassword().toCharArray());
        }
        options.setCleanSession(config.isEnableCleanSession());

        mqttCallback = getCallback(subscriberInfoList);
    }

    @PostConstruct
    private void init() throws Exception {
        if (CollectionUtils.isEmpty(subscriberInfoList)) {
            log.info("未扫描到Subscriber, 不初始化mqtt subscribe客户端");
            return;
        }
        try {
            log.debug("开始初始化mqtt subscribe客户端, config:{}", JSON.toJSONString(config, true));
            mqttClient = new MqttClient(serverUrl, config.getClientId(), new MemoryPersistence());

            // 设定分发器
            mqttClient.setCallback(mqttCallback);

            // 连接
            mqttClient.connect(options);
            log.info("mqtt subscribe客户端连接成功, ip:{}, 端口:{}, clientId:{}", config.getIp(), config.getPort(), config.getClientId());

            // 注册subscribers
            for (SubscriberInfo subscriberInfo : subscriberInfoList) {
                String topic = subscriberInfo.getTopic();
                int qos = subscriberInfo.getQos().getQos();
                mqttClient.subscribe(topic, qos);
                // 新建消费者 无需线程池，每个消费者用一个线程
                //noinspection AlibabaAvoidManuallyCreateThread
                new Thread(new ConsumeRunnable(subscriberInfo)).start();
                log.debug("mqtt subscribe客户端注册Subscriber topic:{},qos:{}", topic, qos);
            }

            log.info("mqtt subscribe客户端 所有Subscriber注册成功, topics:{}",
                    subscriberInfoList.stream().map(SubscriberInfo::getTopic).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("初始化mqtt subscribe客户端失败, message:{}", e.getMessage(), e);
            throw e;
        }
    }

    private void reInit(List<SubscriberInfo> subscriberInfoList) throws MqttException {
        mqttClient.close(true);

        mqttClient = new MqttClient(serverUrl, config.getClientId(), new MemoryPersistence());
        // 设定分发器
        mqttClient.setCallback(mqttCallback);
        // 连接
        mqttClient.connect(options);

        // 注册subscribers
        for (SubscriberInfo subscriberInfo : subscriberInfoList) {
            String topic = subscriberInfo.getTopic();
            int qos = subscriberInfo.getQos().getQos();
            mqttClient.subscribe(topic, qos);
        }
    }


    /**
     * 回调函数
     *
     * @param orgSubscriberInfoList
     * @return
     */
    private MqttCallback getCallback(List<SubscriberInfo> orgSubscriberInfoList) {
        return new MqttCallback() {
            private final String ip = config.getIp();

            private final String port = config.getPort();

            private final int maxTimes = config.getSubscriberClientReconnectTimes();

            private final List<SubscriberInfo> subscriberInfoList = orgSubscriberInfoList;

            @Override
            public void connectionLost(Throwable throwable) {
                log.error("mqtt 失去连接 message:{}", throwable.getMessage(), throwable);
                if (throwable instanceof MqttException) {
                    MqttException mqttException = (MqttException) throwable;
                    if (mqttException.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST) {
                        // 断开连接 重连
                        for (int i = 1; i <= maxTimes; i++) {
                            try {
                                log.info("mqtt subscribe客户端第{}/{}次尝试重连, 连接信息 ip:{}, 端口:{}", i, maxTimes, ip, port);
                                reInit(subscriberInfoList);
                                log.info("mqtt subscribe客户端第{}次重连成功, 连接信息 ip:{}, 端口:{}", i, ip, port);
                                return;
                            } catch (Exception e) {
                                log.error("mqtt subscribe客户端第{}/{}次尝试重连失败, 连接信息 ip:{}, 端口:{}, message:{}", i, maxTimes, ip, port, e.getMessage(), e);
                            }
                        }
                        log.error("mqtt subscribe客户端重连失败, 重连次数达到{}次, 连接信息 ip:{}, 端口:{}", maxTimes, ip, port);
                    }
                }
            }

            @Override
            public synchronized void messageArrived(String topic, MqttMessage message) {
                // 分发逻辑
                for (int i = 0; i < subscriberInfoList.size(); i++) {
                    SubscriberInfo subscriberInfo = subscriberInfoList.get(i);
                    String matchStr = subscriberInfo.getTopic();
                    if (matchStr.equals(topic) || (matchStr.endsWith("#") && topic.startsWith(matchStr.substring(0, matchStr.length() - 1)))) {
                        // 分发到该队列
                        log.debug("mqtt dispatch message, topic:{}, qos:{}, aimTopic:{}", topic, message.getQos(), matchStr);
                        try {
                            subscriberInfo.getMessageQueue().add(MqttProcessObject.builder()
                                    .topic(topic)
                                    .message(message)
                                    .build());
                        } catch (IllegalStateException e) {
                            log.error("提交消息到队列失败, topic:{}, subscriber:{}, message:{}",
                                    topic,
                                    subscriberInfo.getMqttSubscribeProcessor().getClass().getName(),
                                    e.getMessage(), e);
                        }
                        // 前移一位
                        if (i != 0) {
                            SubscriberInfo previous = subscriberInfoList.get(i - 1);
                            subscriberInfoList.set(i - 1, subscriberInfo);
                            subscriberInfoList.set(i, previous);
                        }
                        return;
                    }
                }
                log.warn("The message has no corresponding subscription processor, which is caused by some unknown issues. topic:{}", topic);
            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        };
    }

    /**
     * 转换
     *
     * @param mqttSubscribeProcessorList
     * @return
     */
    private List<SubscriberInfo> convert(List<String> mqttSubscribeProcessorList) {
        return mqttSubscribeProcessorList.stream()
                .map(i -> (MqttSubscribeProcessor) applicationContext.getBean(i))
                .map(i -> {
                    MqttSubscriber mqttSubscriber = i.getClass().getAnnotation(MqttSubscriber.class);
                    String topic = mqttSubscriber.topic();
                    QosEnum qos = mqttSubscriber.qos();
                    int cacheCapacity = mqttSubscriber.cacheCapacity();
                    if (cacheCapacity <= 0) {
                        log.warn("{} 中 @MqttSubscriber(cacheCapacity={}), 该参数必须大于0, 已更改为配置文件默认值:{}",
                                i.getClass().getName(),
                                cacheCapacity,
                                config.getSubscriberQueueCapacity());
                        cacheCapacity = config.getSubscriberQueueCapacity();
                    }
                    return SubscriberInfo.builder()
                            .topic(topic)
                            .qos(qos)
                            .mqttSubscribeProcessor(i)
                            .messageQueue(new ArrayBlockingQueue<>(cacheCapacity))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static class ConsumeRunnable implements Runnable {
        private final SubscriberInfo subscriberInfo;

        public ConsumeRunnable(SubscriberInfo subscriberInfo) {
            this.subscriberInfo = subscriberInfo;
        }

        @Override
        public void run() {
            while (true) {
                MqttProcessObject mqttProcessObject;
                try {
                    mqttProcessObject = subscriberInfo.getMessageQueue().take();
                } catch (InterruptedException e) {
                    log.error("mqtt 消费缓冲队列异常, message:{}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                try {
                    subscriberInfo.getMqttSubscribeProcessor().process(mqttProcessObject.getTopic(), mqttProcessObject.getMessage());
                } catch (Exception e) {
                    log.error("mqtt 消费异常, message:{}", e.getMessage(), e);
                }
            }
        }
    }
}
