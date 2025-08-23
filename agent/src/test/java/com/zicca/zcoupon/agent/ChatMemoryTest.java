package com.zicca.zcoupon.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @author zicca
 */
@SpringBootTest
public class ChatMemoryTest {


    @Test
    public void testChatOptions(@Autowired DashScopeChatModel chatModel) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();
        Flux<String> response1 = chatClient.prompt().user("我叫zicca").stream().content();
        response1.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response2 = chatClient.prompt().user("我叫什么？").stream().content();
        response2.toIterable().forEach(System.out::print);
    }

    @Test
    public void testChatMemeory(@Autowired DashScopeChatModel chatModel, @Autowired ChatMemory chatMemory) {
        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build() // 使用 PromptChatMemoryAdvisor 进行对话记忆拦截
                )
                .build();

        Flux<String> response1 = chatClient.prompt().user("我叫zicca").stream().content();
        response1.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response2 = chatClient.prompt().user("我叫什么？").stream().content();
        response2.toIterable().forEach(System.out::print);

    }


    @Test
    public void testChatMemory2(@Autowired DashScopeChatModel chatModel, @Autowired ChatMemoryRepository chatMemoryRepository) {
        // MessageWindowChatMemory 是 ChatMemroy 的实现类
        // MessageWindowChatMemory 默认使用 ChatMemoryRepository 进行对话记忆
        // ChatMemoryRepository 是一个接口，用于存储对话记忆
        // InMemoryChatMemoryRepository 是 ChatMemoryRepository 的实现类，使用内存进行对话记忆
        // InMemoryChatMemoryRepository 中的成员属性 chatMemoryStore 是一个 Map<String, List<Message>> 类型，用于存储对话记忆
        // 其中 key 为 conversationId，value 为 List<Message> 对话列表
        MessageWindowChatMemory chatMemeory =
                MessageWindowChatMemory.builder()
                        .maxMessages(2)
                        .chatMemoryRepository(chatMemoryRepository)
                        .build();
        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemeory).build() // 使用 PromptChatMemoryAdvisor 进行对话记忆拦截
                )
                .build();

        Flux<String> response1 = chatClient.prompt().user("我叫zicca").stream().content();
        response1.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response2 = chatClient.prompt().user("我叫什么？").stream().content();
        response2.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response3 = chatClient.prompt().user("中国有多大").stream().content();
        response3.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response4 = chatClient.prompt().user("我叫什么？").stream().content();
        response4.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
    }

    @Test
    public void testChatMemory3(@Autowired DashScopeChatModel chatModel, @Autowired ChatMemoryRepository chatMemoryRepository) {
        MessageWindowChatMemory chatMemeory =
                MessageWindowChatMemory.builder()
                        .maxMessages(10) // 每个会话最多存储的消息数量
                        .chatMemoryRepository(chatMemoryRepository)
                        .build();
        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemeory).build()
                )
                .build();

        Flux<String> response1 = chatClient.prompt()
                .user("我叫zicca")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1"))
                .stream()
                .content();
        response1.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");

        Flux<String> response2 = chatClient.prompt()
                .user("我叫什么")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "1"))
                .stream()
                .content();
        response2.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");

        Flux<String> response3 = chatClient.prompt()
                .user("我叫什么")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, "2"))
                .stream()
                .content();
        response3.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
    }


    @Test
    public void testJdbcChatMemory(@Autowired DashScopeChatModel chatModel, @Autowired @Qualifier("jdbcChatMemory") ChatMemory chatMemory) {
        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();

        Flux<String> response1 = chatClient.prompt().user("我叫zicca").stream().content();
        response1.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response2 = chatClient.prompt().user("我叫什么？").stream().content();
        response2.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response3 = chatClient.prompt().user("中国有多大").stream().content();
        response3.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response4 = chatClient.prompt().user("我叫什么？").stream().content();
        response4.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
    }

    @Test
    public void testRedisChatMemory(@Autowired DashScopeChatModel chatModel, @Autowired @Qualifier("redisChatMemory") ChatMemory redisChatMemory) {
        ChatClient chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(redisChatMemory).build()
                )
                .build();

        Flux<String> response1 = chatClient.prompt().user("我叫zicca").stream().content();
        response1.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response2 = chatClient.prompt().user("我叫什么？").stream().content();
        response2.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response3 = chatClient.prompt().user("世界上有多少国家？").stream().content();
        response3.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
        Flux<String> response4 = chatClient.prompt().user("我叫什么？").stream().content();
        response4.toIterable().forEach(System.out::print);
        System.out.println();
        System.out.println("------------------------------------------");
    }


}
