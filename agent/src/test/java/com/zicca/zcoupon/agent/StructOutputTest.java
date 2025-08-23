package com.zicca.zcoupon.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * 结构化输出
 *
 * @author zicca
 */
@SpringBootTest
public class StructOutputTest {
    
    record Address(
            String name,
            String phone,
            String province,
            String city,
            String district,
            String detail
    ){}

    @Test
    public void testBoolOut(@Autowired ChatClient chatClient) {
        Boolean response = chatClient.prompt()
                .system("""
                                请判断用户信息是否表达了投诉意图?
                                只能用 true 或 false 回答，不要输出多余内容
                                """
                ).user("你们家的快递迟迟不到，我要退货！")
                .call().entity(Boolean.class);
        if (Boolean.TRUE.equals(response)) {
            System.out.println("投诉意图");
        } else {
            System.out.println("非投诉意图");
        }
    }

    @Test
    public void testEntityOut(@Autowired ChatClient chatClient) {
        Address address = chatClient.prompt().system(
                        """
                             请从下面这条文本中提取收货信息
                             """)
                .user("收货人：张三，电话13588888888，地址：浙江省杭州市西湖区文一西路100号8幢202室")
                .call()
                .entity(Address.class);
        System.out.println(address);
    }


}
