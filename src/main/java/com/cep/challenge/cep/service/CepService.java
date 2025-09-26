package com.cep.challenge.cep.service;

import com.cep.challenge.cep.client.CepClient;
import com.cep.challenge.cep.domain.QueryLog;
import com.cep.challenge.cep.dto.CepResponse;
import com.cep.challenge.cep.repository.QueryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CepService {

    private final CepClient cepClient;
    private final QueryLogRepository queryLogRepository;

    public CepResponse getCep(String cep) {
        log.info("🚀 Iniciando busca do CEP: {}", cep);
        
        long startTime = System.currentTimeMillis();
        boolean success = true;
        
        try {
            log.info("📡 Chamando API externa para CEP: {}", cep);
            CepResponse response = cepClient.findByCep(cep);
            
            Long responseTime = System.currentTimeMillis() - startTime;
            log.info("⏱️  Tempo de resposta da API: {}ms", responseTime);
            
            // Salva log da consulta
            QueryLog logEntry = new QueryLog(null, cep, LocalDateTime.now(), responseTime, success);
            queryLogRepository.save(logEntry);
            log.info("💾 Log salvo no banco de dados - CEP: {}, Tempo: {}ms, Sucesso: {}", 
                    cep, responseTime, success);
            
            log.info("🎯 CEP {} processado com sucesso!", cep);
            return response;
            
        } catch (Exception e) {
            success = false;
            Long responseTime = System.currentTimeMillis() - startTime;
            
            log.error("❌ Erro ao processar CEP {}: {}", cep, e.getMessage());
            
            // Salva log do erro
            QueryLog logEntry = new QueryLog(null, cep, LocalDateTime.now(), responseTime, success);
            queryLogRepository.save(logEntry);
            log.info("💾 Log de erro salvo no banco - CEP: {}, Tempo: {}ms, Sucesso: {}", 
                    cep, responseTime, success);
            
            throw e;
        }
    }
}
