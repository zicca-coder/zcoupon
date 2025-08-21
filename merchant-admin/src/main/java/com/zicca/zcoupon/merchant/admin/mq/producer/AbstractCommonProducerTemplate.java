package com.zicca.zcoupon.merchant.admin.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.zicca.zcoupon.merchant.admin.mq.base.BaseSendExtendDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;

/**
 * RocketMQ 抽象公共生产者模版
 *
 * @author zicca
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCommonProducerTemplate<T> {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 构建消息发送事件基础扩充属性
     *
     * @param messageSendEvent 消息发送事件
     * @return 消息发送事件基础扩充属性
     */
    protected abstract BaseSendExtendDTO buildBaseSendExtendParam(T messageSendEvent);


    /**
     * 构建消息基本参数
     *
     * @param messageSendEvent 消息发送事件
     * @param extendParam      消息发送事件基础扩充属性
     * @return 消息基本参数
     */
    protected abstract Message<?> buildMessage(T messageSendEvent, BaseSendExtendDTO extendParam);


    /**
     * 发送消息
     *
     * @param messageSendEvent 消息发送事件
     * @return 发送结果
     */
    public SendResult sendMessage(T messageSendEvent) {
        BaseSendExtendDTO baseSendExtendParam = buildBaseSendExtendParam(messageSendEvent);
        SendResult sendResult;
        try {
            StringBuilder destinationTopic = StrUtil.builder().append(baseSendExtendParam.getTopic());
            if (StrUtil.isNotBlank(baseSendExtendParam.getTag())) {
                destinationTopic.append(":").append(baseSendExtendParam.getTag());
            }

            // 如果延迟时间不为空，发送延时消息
            if (baseSendExtendParam.getDelayTime() != null) {
                sendResult = rocketMQTemplate.syncSendDeliverTimeMills(
                        destinationTopic.toString(),
                        buildMessage(messageSendEvent, baseSendExtendParam),
                        baseSendExtendParam.getDelayTime()
                );
            } else { // 发送普通消息
                sendResult = rocketMQTemplate.syncSend(
                        destinationTopic.toString(),
                        buildMessage(messageSendEvent, baseSendExtendParam),
                        baseSendExtendParam.getSentTimeout()
                );
            }
            log.info("[生产者] {} - 发送结果：{}，消息ID：{}，消息Keys：{}", baseSendExtendParam.getEventName(), sendResult.getSendStatus(), sendResult.getMsgId(), baseSendExtendParam.getKeys());
        } catch (Exception e) {
            log.error("[生产者] {} - 消息发送失败，消息体：{}", baseSendExtendParam.getEventName(), JSON.toJSONString(messageSendEvent), e);
            throw e;
        }
        return sendResult;
    }


}
