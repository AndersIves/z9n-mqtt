package cn.z9n.test.publisher;

import cn.z9n.mqtt.MqttPublishProcessor;
import com.alibaba.fastjson.JSON;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 17:28
 */
@Component
public class TestPublisher implements ApplicationRunner {
    @Autowired
    MqttPublishProcessor mqttPublishProcessor;

    @Override
    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        mqttPublishProcessor.publish("test1/"+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), "hello mqtt");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
