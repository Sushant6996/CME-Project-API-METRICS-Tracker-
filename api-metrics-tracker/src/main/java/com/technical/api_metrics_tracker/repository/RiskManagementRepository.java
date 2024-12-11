package com.technical.api_metrics_tracker.repository;

import com.technical.api_metrics_tracker.model.RiskManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface RiskManagementRepository extends JpaRepository<RiskManagement, Long> {

    Optional<RiskManagement> findByTransaction_TransactionId(Long transactionId);
    boolean existsByTransaction_TransactionId(Long transactionId);


    // Additional custom queries can be added if required
}
