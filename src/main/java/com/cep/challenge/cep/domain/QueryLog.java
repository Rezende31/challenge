package com.cep.challenge.cep.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "query_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cep", nullable = false)
    private String cep;
    
    @Column(name = "query_time", nullable = false)
    private LocalDateTime queryTime;
    
    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    
    @Column(name = "success")
    private Boolean success;
}
