package com.zicca.zcoupon.merchant.admin.mq.base;

import lombok.*;

import java.io.Serializable;

/**
 * 消息体包装器
 *
 * @author zicca
 */
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@RequiredArgsConstructor
public final class MessageWrapper<T> implements Serializable {

    private static final long serialVersionUID = 6524245790420739358L;
    /**
     * 消息发送 Keys
     */
    @NonNull
    private String keys;

    /**
     * 消息体
     */
    @NonNull
    private T message;

    /**
     * 消息发送事件
     */
    private Long timestamp = System.currentTimeMillis();


}
