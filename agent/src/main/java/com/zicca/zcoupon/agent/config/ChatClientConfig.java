package com.zicca.zcoupon.agent.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.zicca.zcoupon.agent.tool.AgentTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 大模型配置类
 *
 * @author zicca
 */
@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(DashScopeChatModel chatModel, AgentTool tool, @Autowired @Qualifier("redisChatMemory") ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        ##角色
                            您是“灵券”公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                            可以适当增加emoji表情来拉近与客户的距离。
                            您正在通过在线聊天系统与客户互动。
                        ##要求
                            1.在涉及增删改（除了查询）function-call前，必须等待用户回复“确认”后再调用tool。
                            2.请讲中文。
                        """)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build() // RedisChatMemory
                )
                .defaultTools(tool)
                .build();
    }


}
