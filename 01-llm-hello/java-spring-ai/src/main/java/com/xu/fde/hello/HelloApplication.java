package com.xu.fde.hello;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 最小可用的大模型调用：演示 ChatClient 阻塞式调用。
 * 对应公司 tripod-llm 组件的"统一接入层"个人复刻版——后续在此仓库演进为
 * 流式输出、Function Calling、多模型切换的完整 demo（见 README 规划）。
 */
@SpringBootApplication
public class HelloApplication implements CommandLineRunner {

    private final ChatClient chatClient;

    HelloApplication(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(HelloApplication.class, args)));
    }

    @Override
    public void run(String... args) {
        String reply = chatClient.prompt()
                .user("用一句话解释什么是FDE（前向部署）工程师")
                .call()
                .content();
        System.out.println("模型回答：" + reply);
    }
}
