package com.technical.api_metrics_tracker.service;

import com.technical.api_metrics_tracker.model.RiskManagement;
import com.technical.api_metrics_tracker.model.RiskThresholdConfiguration;
import com.technical.api_metrics_tracker.model.Transaction;
import com.technical.api_metrics_tracker.repository.DerivativeContractRepository;
import com.technical.api_metrics_tracker.repository.RiskManagementRepository;
import com.technical.api_metrics_tracker.repository.RiskThresholdConfigurationRepository;
import com.technical.api_metrics_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class RiskManagementService {

    private final RiskManagementRepository riskManagementRepository;
    private final RiskThresholdConfigurationRepository riskThresholdConfigurationRepository;
    private final TransactionRepository transactionRepository;
    private final DerivativeContractRepository derivativeContractRepository;

    @Autowired
    public RiskManagementService(
            RiskManagementRepository riskManagementRepository,
            RiskThresholdConfigurationRepository riskThresholdConfigurationRepository,
            TransactionRepository transactionRepository,
            DerivativeContractRepository derivativeContractRepository) {
        this.riskManagementRepository = riskManagementRepository;
        this.riskThresholdConfigurationRepository = riskThresholdConfigurationRepository;
        this.transactionRepository = transactionRepository;
        this.derivativeContractRepository = derivativeContractRepository;
    }

    // Calculate and save or update the risk management entry
    public void calculateAndSaveRisk(long transactionId, String contractType, int quantity, double transactionPrice) {
        BigDecimal price = BigDecimal.valueOf(transactionPrice);

        // Calculate the volume
        BigDecimal volume = BigDecimal.valueOf(quantity).multiply(price);

        // Validate contract type
        if (!isValidContractType(contractType)) {
            throw new IllegalArgumentException("Invalid contract type: " + contractType);
        }

        // Fetch risk thresholds for the given contract type
        Optional<RiskThresholdConfiguration> thresholdConfig = riskThresholdConfigurationRepository.findByContractType(contractType);

        if (!thresholdConfig.isPresent()) {
            throw new IllegalArgumentException("Risk threshold configuration not found for contract type: " + contractType);
        }

        // Retrieve thresholds and market factor
        RiskThresholdConfiguration config = thresholdConfig.get();
        BigDecimal transactionPriceThreshold = config.getTransactionPriceThreshold();
        Integer volumeThreshold = config.getVolumeThreshold();
        BigDecimal marketFactor = config.getMarketFactor();

        // Calculate the risk level
        String riskLevel = calculateRiskLevel(price, volume, transactionPriceThreshold, volumeThreshold, marketFactor);

        // Define mitigation strategy
        String mitigationStrategy = defineMitigationStrategy(riskLevel);

        // Fetch the Transaction object based on the transactionId
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found for ID: " + transactionId));

        // Check if RiskManagement entry already exists for the given transaction
        RiskManagement riskManagement = riskManagementRepository.findByTransaction_TransactionId(transactionId)
                .orElse(new RiskManagement());

        // Update or create the RiskManagement entity
        riskManagement.setTransaction(transaction); // Set the full Transaction object
        riskManagement.setTransactionPriceThreshold(transactionPriceThreshold);
        riskManagement.setVolumeThreshold(volumeThreshold);
        riskManagement.setRiskLevel(riskLevel);
        riskManagement.setMitigationStrategy(mitigationStrategy);

        // Save the updated or new RiskManagement entity
        riskManagementRepository.save(riskManagement);
    }

    // Validate contract type
    private boolean isValidContractType(String contractType) {
        return "Futures".equals(contractType) || "Options".equals(contractType);
    }

    // Adjusted risk calculation logic
    public String calculateRiskLevel(BigDecimal transactionPrice, BigDecimal volume, BigDecimal priceThreshold, Integer volumeThreshold, BigDecimal marketFactor) {
        if (priceThreshold.compareTo(BigDecimal.ZERO) == 0 || volumeThreshold == 0) {
            throw new IllegalArgumentException("Threshold values cannot be zero.");
        }

        // Calculate ratios
        BigDecimal priceRatio = transactionPrice.divide(priceThreshold, 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal volumeRatio = volume.divide(BigDecimal.valueOf(volumeThreshold), 4, BigDecimal.ROUND_HALF_UP);

        // Normalize ratios (scale them between 0 and 1)
        BigDecimal normalizedPriceRatio = priceRatio.min(BigDecimal.ONE);
        BigDecimal normalizedVolumeRatio = volumeRatio.min(BigDecimal.ONE);

        // Calculate weighted contributions
        BigDecimal weightedPrice = normalizedPriceRatio.multiply(BigDecimal.valueOf(0.7)); // Price contributes 70%
        BigDecimal weightedVolume = normalizedVolumeRatio.multiply(BigDecimal.valueOf(0.3)); // Volume contributes 30%

        // Final risk score (factor in market influence but limit its impact)
        BigDecimal riskScore = weightedPrice.add(weightedVolume).add(marketFactor.multiply(BigDecimal.valueOf(0.1)));

        // Risk classification
        if (riskScore.compareTo(BigDecimal.valueOf(1.0)) > 0) {
            return "High Risk";
        } else if (riskScore.compareTo(BigDecimal.valueOf(0.6)) > 0) {
            return "Medium Risk";
        } else {
            return "Low Risk";
        }
    }

    // Define mitigation strategy
    public String defineMitigationStrategy(String riskLevel) {
        switch (riskLevel) {
            case "High Risk":
                return "Increase monitoring and apply stricter thresholds.";
            case "Medium Risk":
                return "Monitor closely, with occasional reviews.";
            case "Low Risk":
                return "Routine monitoring is sufficient.";
            default:
                return "No mitigation strategy available.";
        }
    }

    // Get risk details
    public RiskManagement getRiskDetails(long transactionId) {
        return riskManagementRepository.findByTransaction_TransactionId(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Risk details not found for transaction ID: " + transactionId));
    }
}
