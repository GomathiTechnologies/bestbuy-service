package com.example.bestbuyservice;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider catalogTools) {
        return builder.defaultToolCallbacks(catalogTools.getToolCallbacks())
                .defaultSystem("""
                    You are a shopping assistant. Use the available catalog tools when appropriate.
                    The catalog contains sample internet plans and the cart tool creates a sample order;
                    do not claim that an order was placed or a cart was saved.
                    """)
                .build();
    }
}
