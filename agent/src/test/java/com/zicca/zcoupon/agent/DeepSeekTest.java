package com.zicca.zcoupon.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@SpringBootTest
public class DeepSeekTest {

    /**
     * 测试 DeepSeek | 同步响应模式
     */
    @Test
    public void testDeepSeek(@Autowired DeepSeekChatModel chatModel) {
        String content = chatModel.call("你好请问你是谁？");
        System.out.println(content);
    }

    /**
     * 测试 DeepSeek | 流式响应模式
     */
    @Test
    public void testDeepSeekStream(@Autowired DeepSeekChatModel chatModel) {
        Flux<String> stream = chatModel.stream("你好请问你是谁？");
        stream.toIterable().forEach(System.out::println);
    }


    @Test
    public void testChatOptions(@Autowired DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .temperature(0.9) // 模型生成的随机性 0-2, 越小越 deterministic(确定)
                .maxTokens(1024) // 模型生成的最大字符数，默认32k,，最大64k
                .stop(List.of("Human:", "AI:")) // 模型生成停止符，遇到该符合停止输出
                .build();
        Prompt prompt = new Prompt("请写一首诗。", options);
        ChatResponse response = chatModel.call(prompt);
        System.out.println(response.getResult().getOutput().getText());
    }

    @Test
    public void testDeepSeekReasoning(@Autowired DeepSeekChatModel chatModel) {
        Prompt prompt = new Prompt("你好你是谁？");
        ChatResponse response = chatModel.call(prompt);
        DeepSeekAssistantMessage assistantMessage = (DeepSeekAssistantMessage) response.getResult().getOutput();
        String reasoningContent = assistantMessage.getReasoningContent();
        String content = assistantMessage.getText();
        System.out.println(reasoningContent);
        System.out.println("---------------------------");
        System.out.println(content);
    }

    @Test
    public void testDeepSeekReasoningStream(@Autowired DeepSeekChatModel chatModel) {
        Prompt prompt = new Prompt("你好你是谁？");
        Flux<ChatResponse> stream = chatModel.stream(prompt);
        stream.toIterable().forEach(chatResponse -> {
            DeepSeekAssistantMessage assistantMessage = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
            System.out.print(assistantMessage.getReasoningContent());
        });
        System.out.println("-----------------------------");
        stream.toIterable().forEach(chatResponse -> {
            DeepSeekAssistantMessage assistantMessage = (DeepSeekAssistantMessage) chatResponse.getResult().getOutput();
            System.out.print(assistantMessage.getText());
        });
    }

}
