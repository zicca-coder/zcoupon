package com.zicca.zcoupon.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.zicca.zcoupon.agent.demo.ReReadingAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @author zicca
 */
@SpringBootTest
public class AdvisorTest {



//    @BeforeEach
//    public void init(@Autowired DashScopeChatModel chatModel) {
//        ChatClient chatClient = ChatClient.builder(chatModel)
//                .defaultAdvisors(
//                        new SimpleLoggerAdvisor() // 日志记录
//                )
//                .build();
//    }


//    @Test
//    public void testChatOptions() {
//        Flux<String> response = chatClient.prompt().user("你好").stream().content();
//        response.toIterable().forEach(System.out::print);
//    }


    @Test
    public void testChatOptions2(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).defaultAdvisors(new ReReadingAdvisor()).build();
        Flux<String> response = chatClient.prompt().user("中国有多大？").stream().content();
        response.toIterable().forEach(System.out::print);
    }


}
