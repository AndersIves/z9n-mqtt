package cn.z9n.mqtt.service;

import cn.z9n.mqtt.MqttSubscribeProcessor;
import cn.z9n.mqtt.annos.MqttSubscriber;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: 张子玄(罗小黑) YCKJ3690
 * @Date: 2021/5/20 14:53
 */
public class MqttSubscriberRegister implements PriorityOrdered, BeanFactoryPostProcessor {

    private List<MqttSubscribeProcessor> mqttSubscribeProcessorList;

    protected List<MqttSubscribeProcessor> getMqttSubscribeProcessorList() {
        return mqttSubscribeProcessorList;
    }

    /**
     * 初始化mqtt客户端并连接mqtt broker
     */

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        mqttSubscribeProcessorList = Arrays.stream(beanFactory.getBeanNamesForAnnotation(MqttSubscriber.class))
                .map(beanFactory::getBean)
                .filter(i -> i instanceof MqttSubscribeProcessor)
                .map(i -> (MqttSubscribeProcessor) i)
                .collect(Collectors.toList());
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
