package com.example.bestbuyservice;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;

class BestbuyChatControllerTest {
    private final ChatClient client = mock(ChatClient.class, RETURNS_DEEP_STUBS);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new BestbuyChatController(client)).build();

    @Test void returnsModelResponse() throws Exception {
        when(client.prompt().user("Hello").call().content()).thenReturn("Hello there");
        mvc.perform(post("/bestbuy/chat").contentType(MediaType.APPLICATION_JSON).content("{\"prompt\":\"Hello\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.response").value("Hello there"));
    }

    @Test void rejectsBlankPrompt() throws Exception {
        for (String body : new String[]{"{}", "{\"prompt\":\"  \"}"}) {
            mvc.perform(post("/bestbuy/chat").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(client);
    }

    @Test void hidesUpstreamFailureDetails() throws Exception {
        when(client.prompt().user("Hello").call().content()).thenThrow(new IllegalStateException("private details"));
        mvc.perform(post("/bestbuy/chat").contentType(MediaType.APPLICATION_JSON).content("{\"prompt\":\"Hello\"}"))
                .andExpect(status().isBadGateway()).andExpect(content().string(not(containsString("private details"))));
    }
}
