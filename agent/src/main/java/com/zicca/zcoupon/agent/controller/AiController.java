package com.zicca.zcoupon.agent.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author zicca
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AiController {

    private final ChatClient chatClient;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam(value = "message") String message) {
        Flux<String> content = chatClient.prompt().user(message).stream().content();
        return content;
    }



}
