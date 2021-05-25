package cn.z9n.test.publisher;

import cn.z9n.mqtt.MqttPublishProcessor;
import com.alibaba.fastjson.JSON;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
        List<String> ipList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface networkInterface;
            Enumeration<InetAddress> inetAddresses;
            InetAddress inetAddress;
            String ip;
            while (networkInterfaces.hasMoreElements()) {
                networkInterface = networkInterfaces.nextElement();
                inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address) {
                        ip = inetAddress.getHostAddress();
                        ipList.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        mqttPublishProcessor.publish("test2/"+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), "hello mqtt, im: "+ipList);
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
