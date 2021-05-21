package cn.z9n.mqtt.annos;

import cn.z9n.mqtt.enums.QosEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 15:13
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MqttSubscriber {
    String topic();
    QosEnum qos();
    int cacheCapacity() default -1;
}
