package com.example.bestbuyservice;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.ai.mcp.server.autoconfigure.McpWebFluxServerAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

class McpConnectionTest {
    @Test void discoversAndCallsToolOverSse() {
        try (var server = new SpringApplicationBuilder(TestServer.class).run(
                "--server.port=0", "--server.shutdown=immediate", "--spring.ai.mcp.client.enabled=false",
                "--spring.ai.openai.api-key=test")) {
            int port = ((ServletWebServerApplicationContext) server).getWebServer().getPort();
            try (var client = new SpringApplicationBuilder(BestbuyServiceApplication.class).run(
                    "--server.port=0", "--spring.ai.mcp.server.enabled=false",
                    "--spring.ai.openai.api-key=test",
                    "--spring.ai.mcp.client.sse.connections.product-catalog.url=http://localhost:" + port)) {
                var tools = client.getBean(ToolCallbackProvider.class).getToolCallbacks();
                var tool = Arrays.stream(tools).filter(t -> t.getToolDefinition().name().endsWith("getProvinceProducts"))
                        .findFirst().orElseThrow();
                assertThat(tool.call("{\"province\":\"Ontario\"}")).contains("Ontario sample plans");
            }
        }
    }

    @TestConfiguration
    @EnableAutoConfiguration(exclude = McpWebFluxServerAutoConfiguration.class)
    @Import(TestTools.class)
    static class TestServer {
        @Bean ToolCallbackProvider testTools(TestTools tools) {
            return MethodToolCallbackProvider.builder().toolObjects(tools).build();
        }
    }

    public static class TestTools {
        @Tool(description = "Find sample plans by province")
        public String getProvinceProducts(@ToolParam(description = "Province name") String province) {
            return province + " sample plans";
        }
    }
}
