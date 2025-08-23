package com.zicca.zcoupon.agent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class OpenAiTest {

    @Test
    public void testOpenAi(@Autowired OpenAiChatModel chatModel) {
        String content = chatModel.call("你好你是谁？");
        System.out.println(content);
    }


}
