package com.cep.challenge.cep.service;

import com.cep.challenge.cep.client.CepClient;
import com.cep.challenge.cep.domain.QueryLog;
import com.cep.challenge.cep.dto.CepResponse;
import com.cep.challenge.cep.repository.QueryLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepServiceTest {

    @Mock
    private CepClient cepClient;

    @Mock
    private QueryLogRepository queryLogRepository;

    @InjectMocks
    private CepService cepService;

    private CepResponse cepResponse;

    @BeforeEach
    void setUp() {
        cepResponse = new CepResponse();
        cepResponse.setCep("01001000");
        cepResponse.setLogradouro("Praça da Sé");
        cepResponse.setBairro("Sé");
        cepResponse.setLocalidade("São Paulo");
        cepResponse.setUf("SP");
    }

    @Test
    void getCep_WithValidCep_ShouldReturnResponseAndSaveLog() {
        // Given
        String cep = "01001000";
        when(cepClient.findByCep(cep)).thenReturn(cepResponse);
        when(queryLogRepository.save(any(QueryLog.class))).thenReturn(new QueryLog());

        // When
        CepResponse result = cepService.getCep(cep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCep()).isEqualTo(cep);
        assertThat(result.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(result.getLocalidade()).isEqualTo("São Paulo");

        // Verify that log was saved
        verify(queryLogRepository, times(1)).save(any(QueryLog.class));
        verify(cepClient, times(1)).findByCep(cep);
    }

    @Test
    void getCep_WithValidCep_ShouldSaveLogWithCorrectData() {
        // Given
        String cep = "01001000";
        when(cepClient.findByCep(cep)).thenReturn(cepResponse);
        when(queryLogRepository.save(any(QueryLog.class))).thenReturn(new QueryLog());

        // When
        cepService.getCep(cep);

        // Then
        ArgumentCaptor<QueryLog> logCaptor = ArgumentCaptor.forClass(QueryLog.class);
        verify(queryLogRepository).save(logCaptor.capture());

        QueryLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getCep()).isEqualTo(cep);
        assertThat(savedLog.getSuccess()).isTrue();
        assertThat(savedLog.getQueryTime()).isNotNull();
    }

    @Test
    void getCep_WhenClientThrowsException_ShouldSaveErrorLogAndRethrow() {
        // Given
        String cep = "99999999";
        RuntimeException exception = new RuntimeException("CEP não encontrado");
        when(cepClient.findByCep(cep)).thenThrow(exception);
        when(queryLogRepository.save(any(QueryLog.class))).thenReturn(new QueryLog());

        // When & Then
        assertThatThrownBy(() -> cepService.getCep(cep))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("CEP não encontrado");

        // Verify that error log was saved
        ArgumentCaptor<QueryLog> logCaptor = ArgumentCaptor.forClass(QueryLog.class);
        verify(queryLogRepository).save(logCaptor.capture());

        QueryLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getCep()).isEqualTo(cep);
        assertThat(savedLog.getSuccess()).isFalse();
        assertThat(savedLog.getResponseTimeMs()).isGreaterThan(0);
    }

    @Test
    void getCep_WithDifferentCep_ShouldCallClientWithCorrectCep() {
        // Given
        String cep = "20040020";
        CepResponse response = new CepResponse();
        response.setCep(cep);
        response.setLogradouro("Rua Primeiro de Março");
        response.setLocalidade("Rio de Janeiro");

        when(cepClient.findByCep(cep)).thenReturn(response);
        when(queryLogRepository.save(any(QueryLog.class))).thenReturn(new QueryLog());

        // When
        CepResponse result = cepService.getCep(cep);

        // Then
        assertThat(result.getCep()).isEqualTo(cep);
        verify(cepClient, times(1)).findByCep(cep);
        verify(queryLogRepository, times(1)).save(any(QueryLog.class));
    }

    @Test
    void getCep_ShouldMeasureResponseTime() {
        // Given
        String cep = "01001000";
        when(cepClient.findByCep(cep)).thenReturn(cepResponse);
        when(queryLogRepository.save(any(QueryLog.class))).thenReturn(new QueryLog());

        // When
        cepService.getCep(cep);

        // Then
        ArgumentCaptor<QueryLog> logCaptor = ArgumentCaptor.forClass(QueryLog.class);
        verify(queryLogRepository).save(logCaptor.capture());

        QueryLog savedLog = logCaptor.getValue();
        assertThat(savedLog.getResponseTimeMs()).isNotNull();
        assertThat(savedLog.getResponseTimeMs()).isGreaterThanOrEqualTo(0);
    }
}
