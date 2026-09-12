package com.jmn.rag;

import com.jmn.rag.comm.constant.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * 电力营销知识库RAG问答服务启动类
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@SpringBootApplication(scanBasePackages = {AppConstant.BASE_PACKAGE})
@Slf4j
public class RagApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(RagApplication.class);
		ConfigurableApplicationContext act = app.run(args);
		Environment env = act.getEnvironment();
		logApplicationStartup(env);
	}

	private static void logApplicationStartup(Environment env) {
		String protocol = "http";
		String host = "localhost";
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			log.warn("获取主机地址失败: {}", e.getMessage());
		}
		String port = env.getProperty("server.port", "8802");
		String path = env.getProperty("server.servlet.context-path", "/");
		log.info("""
				
				----------------------------------------------------------
				应用程序正在运行中...
				接口文档: http://{}:{}{}/doc.html
				知识库目录: {}
				----------------------------------------------------------
				""", host, port, path, env.getProperty("rag.data-dir", "./data/docs"));
	}
}
