package com.example.bestbuyservice;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import static org.mockito.Mockito.*;

class ChatConfigTest {
    @Test void registersMcpCallbacksWithChatClient() {
        ChatClient.Builder builder = mock(ChatClient.Builder.class);
        ToolCallback callback = mock(ToolCallback.class);
        ToolCallbackProvider provider = () -> new ToolCallback[]{callback};
        when(builder.defaultToolCallbacks(any(ToolCallback[].class))).thenReturn(builder);
        when(builder.defaultSystem(anyString())).thenReturn(builder);
        new ChatConfig().chatClient(builder, provider);
        verify(builder).defaultToolCallbacks(same(callback));
        verify(builder).build();
    }
}
