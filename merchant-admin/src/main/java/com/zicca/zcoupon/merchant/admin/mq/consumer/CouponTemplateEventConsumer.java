package com.zicca.zcoupon.merchant.admin.mq.consumer;

import com.alibaba.fastjson2.JSON;
import com.zicca.zcoupon.merchant.admin.mq.base.MessageWrapper;
import com.zicca.zcoupon.merchant.admin.mq.event.CouponTemplateEvent;
import com.zicca.zcoupon.merchant.admin.service.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.zicca.zcoupon.merchant.admin.common.constants.MQConstant.COUPON_TEMPLATE_EVENT_CONSUMER_GROUP;
import static com.zicca.zcoupon.merchant.admin.common.constants.MQConstant.COUPON_TEMPLATE_EVENT_TOPIC;

/**
 * 优惠券模板事件消费者
 *
 * @author zicca
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = COUPON_TEMPLATE_EVENT_TOPIC,
        consumerGroup = COUPON_TEMPLATE_EVENT_CONSUMER_GROUP
)
@Slf4j
public class CouponTemplateEventConsumer implements RocketMQListener<MessageWrapper<CouponTemplateEvent>> {

    private final CouponTemplateService couponTemplateService;

    @Override
    public void onMessage(MessageWrapper<CouponTemplateEvent> message) {
        log.info("[消费者] 优惠券模板状态变更事件执行@变更记录发送状态 - 执行消费逻辑，消息体：{}", JSON.toJSONString(message));
        CouponTemplateEvent event = message.getMessage();
        switch (event.getOperationType()) {
            case "ACTIVE":
                couponTemplateService.activeCouponTemplate(event.getCouponTemplateId());
                log.info("优惠券模板激活成功：{}", event);
                break;
            case "EXPIRE":
                couponTemplateService.terminateCouponTemplate(event.getCouponTemplateId());
                log.info("优惠券模板过期成功：{}", event);
                break;
            default:
                break;
        }
    }
}
