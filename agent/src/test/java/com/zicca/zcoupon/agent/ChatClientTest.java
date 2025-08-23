package com.zicca.zcoupon.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

@SpringBootTest
public class ChatClientTest {

    @Test
    public void testChatClient(@Autowired OpenAiChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        String content = chatClient.prompt().user("你好你是谁？").call().content();
        System.out.println(content);
    }

    @Test
    public void testDeepSeekChatClient(@Autowired OpenAiChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Flux<String> content = chatClient.prompt().user("你好你是谁？").stream().content();
        content.toIterable().forEach(System.out::println);
    }



}
