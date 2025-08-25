package com.zicca.zcoupon.merchant.admin;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zicca
 */
@SpringBootTest
public class CanalRocketMQConsumerTest {


    public static void main(String[] args) throws MQClientException, InterruptedException {
        // 创建消费者，指定消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("canal-consumer-group");

        // 设置 RocketMQ nameserver 地址
        consumer.setNamesrvAddr("192.168.17.128:9876");

        // 订阅 Canal 推送的 Topic
        consumer.subscribe("example", "*");

        // 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : msgs) {
                    String body = new String(msg.getBody(), StandardCharsets.UTF_8);
                    System.out.println("收到 Canal 消息: " + body);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动消费者
        consumer.start();
        System.out.println("🚀 Canal RocketMQ 消费者已启动，正在监听 zcoupon-coupon-template-canal Topic...");

        // 持续运行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.shutdown();
            System.out.println("🚪 Canal RocketMQ 消费者已关闭");
        }));
    }



}
