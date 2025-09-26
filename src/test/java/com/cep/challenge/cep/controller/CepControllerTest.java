package com.cep.challenge.cep.controller;

import com.cep.challenge.cep.dto.CepResponse;
import com.cep.challenge.cep.service.CepService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CepController.class)
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    //@MockBean
    private CepService cepService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getCep_WithValidCep_ShouldReturnCepResponse() throws Exception {
        // Given
        String cep = "01001000";
        CepResponse expectedResponse = createCepResponse(cep, "Praça da Sé", "Sé", "São Paulo", "SP");
        
        when(cepService.getCep(cep)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/api/ceps/{cep}", cep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cep").value(cep))
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"));
    }

    @Test
    void searchCep_WithValidCep_ShouldReturnCepResponse() throws Exception {
        // Given
        String cep = "20040020";
        CepResponse expectedResponse = createCepResponse(cep, "Rua Primeiro de Março", "Centro", "Rio de Janeiro", "RJ");
        
        when(cepService.getCep(cep)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/api/ceps/search")
                        .param("cep", cep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cep").value(cep))
                .andExpect(jsonPath("$.logradouro").value("Rua Primeiro de Março"))
                .andExpect(jsonPath("$.bairro").value("Centro"))
                .andExpect(jsonPath("$.localidade").value("Rio de Janeiro"))
                .andExpect(jsonPath("$.uf").value("RJ"));
    }

    @Test
    void getCep_WithDifferentCep_ShouldReturnCorrectResponse() throws Exception {
        // Given
        String cep = "30112000";
        CepResponse expectedResponse = createCepResponse(cep, "Rua da Bahia", "Centro", "Belo Horizonte", "MG");
        
        when(cepService.getCep(cep)).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(get("/api/ceps/{cep}", cep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value(cep))
                .andExpect(jsonPath("$.logradouro").value("Rua da Bahia"))
                .andExpect(jsonPath("$.bairro").value("Centro"))
                .andExpect(jsonPath("$.localidade").value("Belo Horizonte"))
                .andExpect(jsonPath("$.uf").value("MG"));
    }

    @Test
    void searchCep_WithMissingCepParam_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ceps/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    private CepResponse createCepResponse(String cep, String logradouro, String bairro, String localidade, String uf) {
        CepResponse response = new CepResponse();
        response.setCep(cep);
        response.setLogradouro(logradouro);
        response.setBairro(bairro);
        response.setLocalidade(localidade);
        response.setUf(uf);
        return response;
    }
}
