package com.zicca.zcoupon.agent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.model.deepseek.autoconfigure.DeepSeekChatProperties;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 大模型配置类
 *
 * @author zicca
 */
@Configuration
public class AiConfig {

//
//    @Bean("deepSeekClient")
//    public ChatClient deepSeekClient(DeepSeekChatModel chatModel) {
//        return ChatClient.builder(chatModel).build();
//    }
//
//    @Bean("qwenClient")
//    public ChatClient qwenClient(DashScopeChatModel chatModel) {
//        return ChatClient.builder(chatModel).build();
//    }
//
//    @Bean("ollamaClient")
//    public ChatClient ollamaClient(OllamaChatModel chatModel) {
//        return ChatClient.builder(chatModel).build();
//    }


}
