package com.cep.challenge.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class WebClientConfig {

	@Bean
	public WebClient cepWebClient(AppProperties props) {
		return WebClient.builder()
				.baseUrl(props.getBaseUrl())
				.build();
	}
}


