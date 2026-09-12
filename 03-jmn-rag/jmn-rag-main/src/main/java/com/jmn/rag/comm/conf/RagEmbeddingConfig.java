package com.jmn.rag.comm.conf;

import com.jmn.rag.rag.embed.EmbeddingClient;
import com.jmn.rag.rag.embed.LocalHashingEmbedder;
import com.jmn.rag.rag.embed.OpenAiEmbeddingClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 向量化客户端装配：按 rag.embedding.mode 选择实现
 *
 * @author viscen(徐文程) 2026年09月12日
 */
@Configuration
@Slf4j
public class RagEmbeddingConfig {

	@Bean
	@Primary
	public EmbeddingClient embeddingClient(
			@Value("${rag.embedding.mode:local}") String mode,
			ObjectProvider<LocalHashingEmbedder> localProvider,
			ObjectProvider<OpenAiEmbeddingClient> openaiProvider) {
		EmbeddingClient client = "openai".equalsIgnoreCase(mode)
				? openaiProvider.getObject()
				: localProvider.getObject();
		log.info("向量化实现: {} (mode={})", client.name(), mode);
		return client;
	}
}
