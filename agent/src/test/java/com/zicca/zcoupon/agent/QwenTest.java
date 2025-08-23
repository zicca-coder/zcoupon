package com.zicca.zcoupon.agent;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xmlunit.builder.Input;

import java.io.InputStream;

@SpringBootTest
public class QwenTest {


    @Test
    public void testQwen(@Autowired DashScopeChatModel chatModel) {
        String content = chatModel.call("你好你是谁？");
        System.out.println(content);
    }


    /**
     * 文生图模型
     */
    @Test
    public void text2Img(@Autowired DashScopeImageModel imageModel) {
        ImagePrompt prompt = new ImagePrompt("优惠券APP的图标");
        ImageResponse imageResponse = imageModel.call(prompt);
        // 图片 url
        String url = imageResponse.getResult().getOutput().getUrl();
        // 图片 base64
        String b64Json = imageResponse.getResult().getOutput().getB64Json();
        System.out.println(url);
        System.out.println(b64Json);
    }

}
