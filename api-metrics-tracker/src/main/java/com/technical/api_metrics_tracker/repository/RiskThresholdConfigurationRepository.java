package com.technical.api_metrics_tracker.repository;
import com.technical.api_metrics_tracker.model.RiskThresholdConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiskThresholdConfigurationRepository extends JpaRepository<RiskThresholdConfiguration, Long> {

    // Fetch threshold configuration based on contract type (Option/Future)
    Optional<RiskThresholdConfiguration> findByContractType(String contractType);

    // Additional custom queries can be added if required
}

