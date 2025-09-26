package com.cep.challenge.cep.client;

import com.cep.challenge.cep.dto.CepResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CepClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CepClient cepClient;

    private CepResponse expectedResponse;

    @BeforeEach
    void setUp() {
        expectedResponse = new CepResponse();
        expectedResponse.setCep("01001000");
        expectedResponse.setLogradouro("Praça da Sé");
        expectedResponse.setBairro("Sé");
        expectedResponse.setLocalidade("São Paulo");
        expectedResponse.setUf("SP");
    }

    @Test
    void findByCep_WithValidCep_ShouldReturnCepResponse() {
        // Given
        String cep = "01001000";
        setupWebClientMocks(expectedResponse);

        // When
        CepResponse result = cepClient.findByCep(cep);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCep()).isEqualTo(cep);
        assertThat(result.getLogradouro()).isEqualTo("Praça da Sé");
        assertThat(result.getBairro()).isEqualTo("Sé");
        assertThat(result.getLocalidade()).isEqualTo("São Paulo");
        assertThat(result.getUf()).isEqualTo("SP");

        verify(webClient).get();
        verify(requestHeadersUriSpec).uri("/cep/{cep}", cep);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(CepResponse.class);
    }

    @Test
    void findByCep_WithDifferentCep_ShouldCallCorrectEndpoint() {
        // Given
        String cep = "20040020";
        CepResponse response = new CepResponse();
        response.setCep(cep);
        response.setLogradouro("Rua Primeiro de Março");
        response.setLocalidade("Rio de Janeiro");

        setupWebClientMocks(response);

        // When
        CepResponse result = cepClient.findByCep(cep);

        // Then
        assertThat(result.getCep()).isEqualTo(cep);
        verify(requestHeadersUriSpec).uri("/cep/{cep}", cep);
    }

    @Test
    void findByCep_WhenWebClientThrowsException_ShouldThrowRuntimeException() {
        // Given
        String cep = "99999999";
        WebClientResponseException exception = WebClientResponseException.create(404, "Not Found", null, null, null);
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/cep/{cep}", cep)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CepResponse.class)).thenReturn(Mono.error(exception));

        // When & Then
        assertThatThrownBy(() -> cepClient.findByCep(cep))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao consultar CEP na API externa");
    }

    @Test
    void findByCep_WhenApiReturnsError_ShouldPropagateException() {
        // Given
        String cep = "00000000";
        RuntimeException exception = new RuntimeException("API indisponível");
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/cep/{cep}", cep)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CepResponse.class)).thenReturn(Mono.error(exception));

        // When & Then
        assertThatThrownBy(() -> cepClient.findByCep(cep))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao consultar CEP na API externa");
    }

    private void setupWebClientMocks(CepResponse response) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CepResponse.class)).thenReturn(Mono.just(response));
    }
}
