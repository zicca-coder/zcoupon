package com.zicca.zcoupon.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@SpringBootTest
public class PromptTest {


    /**
     * 使用 ChatClient.Builder 设置默认系统提示词
     */
    @Test
    public void testPromptWithDefaultSystemMessage(@Autowired DashScopeChatModel chatModel) {
        // 创建系统提示词
        String systemText = """
                你是一个友好的 AI 助手，帮助人们寻找信息。
                你的名字是 {name}。
                你应该用你的名字回复用户的请求，并以一种 {voice} 的风格进行回复。
                """;
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
        Message systemMessage = systemPromptTemplate.createMessage(
                Map.of("name", "zicca", "voice", "zh-CN-XiaoxiaoNeural")
        );

        // 创建 ChatClient 时设置默认系统提示词
        ChatClient chatClient = ChatClient.builder(chatModel).defaultSystem(systemMessage.getText()).build();

        String response = chatClient.prompt().user("请告诉我三位著名的海盗，他们的黄金时代和他们的动机。").call().content();
        System.out.println(response);
    }

    /**
     * 使用PromptTemplate设置系统提示词
     */
    @Test
    public void testPromptWithTemplate(@Autowired DashScopeChatModel chatModel) {
        // 定义系统提示模板
        String systemText = """
            你是一个友好的 AI 助手，帮助人们寻找信息。
            你的名字是 {name}。
            你应该用你的名字回复用户的请求，并以一种 {voice} 的风格进行回复。
            """;
        // 定义用户提示模板
        String userText = """
            请告诉我三位著名的海盗，他们的黄金时代和他们的动机。
            每位海盗至少写一句话。
            """;

        ChatClient chatClient = ChatClient.builder(chatModel).defaultSystem(systemText).build();
        Flux<String> response = chatClient.prompt()
                .system(s -> s.text(systemText) // 通过 Consumer 函数接口设置系统提示
                        .param("name", "zicca")
                        .param("voice", "zh-CN-XiaoxiaoNeural"))
                .user(userText)
                .stream()
                .content();
        response.toIterable().forEach(System.out::print);
    }

    @Test
    public void testWithConfiguredChatClient(@Autowired ChatClient chatClient) {
        String userText = """
                请告诉我三位著名的 {role}，他们的经典传奇故事。
                """;
        Flux<String> response = chatClient.prompt().user(u -> u.text(userText)
                        .param("role", "皇帝"))
                .stream().content();
        response.toIterable().forEach(System.out::print);
    }


}
