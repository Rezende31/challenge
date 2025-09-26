package com.cep.challenge.cep.controller;

import com.cep.challenge.cep.dto.CepResponse;
import com.cep.challenge.cep.service.CepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ceps")
@RequiredArgsConstructor
@Slf4j
public class CepController {

    private final CepService cepService;

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponse> getCep(@PathVariable String cep) {
        log.info("🔍 Recebida requisição para buscar CEP: {}", cep);
        
        CepResponse response = cepService.getCep(cep);
        
        log.info("✅ CEP {} encontrado: {} - {}, {}", 
                cep, response.getLogradouro(), response.getBairro(), response.getLocalidade());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<CepResponse> searchCep(@RequestParam String cep) {
        log.info("🔍 Recebida requisição de busca por CEP via query param: {}", cep);
        
        CepResponse response = cepService.getCep(cep);
        
        log.info("✅ CEP {} encontrado via search: {} - {}, {}", 
                cep, response.getLogradouro(), response.getBairro(), response.getLocalidade());
        
        return ResponseEntity.ok(response);
    }
}
