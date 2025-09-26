package com.cep.challenge.cep.client;

import com.cep.challenge.cep.dto.CepResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
public class CepClient {

	private final WebClient webClient;

	public CepClient(WebClient cepWebClient) {
		this.webClient = cepWebClient;
	}

	public CepResponse findByCep(String cep) {
		log.info("🔎 Buscando dados do CEP {} na API externa (Mockoon)", cep);
		
		try {
			// Chama API externa do Mockoon
			CepResponse response = webClient
					.get()
					.uri("/cep/{cep}", cep)
					.retrieve()
					.bodyToMono(CepResponse.class)
					.block();
			
			log.info("📋 Dados encontrados para CEP {}: {} - {}", 
					cep, response.getLogradouro(), response.getLocalidade());
			
			return response;
			
		} catch (Exception e) {
			log.error("❌ Erro ao buscar CEP {} na API externa: {}", cep, e.getMessage());
			throw new RuntimeException("Erro ao consultar CEP na API externa", e);
		}
	}
}


