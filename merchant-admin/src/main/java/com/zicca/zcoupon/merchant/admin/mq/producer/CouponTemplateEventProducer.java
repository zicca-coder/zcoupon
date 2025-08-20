package com.zicca.zcoupon.merchant.admin.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.zicca.zcoupon.framework.exeception.ServiceException;
import com.zicca.zcoupon.merchant.admin.mq.base.BaseSendExtendDTO;
import com.zicca.zcoupon.merchant.admin.mq.base.MessageWrapper;
import com.zicca.zcoupon.merchant.admin.mq.event.CouponTemplateEvent;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

import static com.zicca.zcoupon.merchant.admin.common.constants.MQConstant.COUPON_TEMPLATE_EVENT_TOPIC;

/**
 * 优惠券模板消息生产者
 *
 * @author zicca
 */
@Component
public class CouponTemplateEventProducer extends AbstractCommonProducerTemplate<CouponTemplateEvent> {

    private static final String ACTIVE_TOPIC = "coupon-template-active-event-topic";
    private static final String EXPIRE_TOPIC = "coupon-template-expire-event-topic";

    public CouponTemplateEventProducer(@Autowired RocketMQTemplate rocketMQTemplate) {
        super(rocketMQTemplate);
    }

    @Override
    protected BaseSendExtendDTO buildBaseSendExtendParam(CouponTemplateEvent messageSendEvent) {

        BaseSendExtendDTO param = buildParam(messageSendEvent);
        if (Objects.isNull(param)) {
            throw new ServiceException("未知优惠券模型发送事件类型...");
        }
        return param;
    }

    @Override
    protected Message<?> buildMessage(CouponTemplateEvent messageSendEvent, BaseSendExtendDTO extendParam) {
        String keys = StrUtil.isEmpty(extendParam.getKeys()) ? UUID.randomUUID().toString() : extendParam.getKeys();
        return MessageBuilder.withPayload(new MessageWrapper(keys, messageSendEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, keys)
                .setHeader(MessageConst.PROPERTY_TAGS, extendParam.getTag())
                .build();
    }

    private BaseSendExtendDTO buildParam(CouponTemplateEvent event) {
        switch (event.getOperationType()) {
            case "ACTIVE":
                return BaseSendExtendDTO.builder()
                        .eventName("优惠券模板激活事件")
                        .topic(COUPON_TEMPLATE_EVENT_TOPIC)
                        .tag("ACTIVE")
                        .keys(event.getOperationType() + ":" + event.getCouponTemplateId())
                        .sentTimeout(2000L)
                        .build();
            case "EXPIRE":
                return BaseSendExtendDTO.builder()
                        .eventName("优惠券模板过期事件")
                        .topic(COUPON_TEMPLATE_EVENT_TOPIC)
                        .tag("EXPIRE")
                        .keys(event.getOperationType() + ":" + event.getCouponTemplateId())
                        .sentTimeout(2000L)
                        .delayTime(event.getOperationTime())
                        .build();
            default:
                return null;
        }

    }


}
