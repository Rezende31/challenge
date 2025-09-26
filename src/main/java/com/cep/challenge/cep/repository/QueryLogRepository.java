package com.cep.challenge.cep.repository;

import com.cep.challenge.cep.domain.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {
}


