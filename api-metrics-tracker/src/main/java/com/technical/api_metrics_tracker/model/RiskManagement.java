package com.technical.api_metrics_tracker.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "risk_management")
public class RiskManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "risk_id")
    private Long riskId;

    @ManyToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "transaction_id", nullable = false)
    private Transaction transaction;
    @Column(name = "transaction_price_Threshold", precision = 10, scale = 2)
    private BigDecimal transactionPriceThreshold;

    @Column(name = "volume_Threshold")
    private Integer volumeThreshold;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "mitigation_strategy")
    private String mitigationStrategy;

    // Getters and Setters

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public Transaction getTransaction() {
        return transaction;
    }
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BigDecimal getTransactionPriceThreshold() {
        return transactionPriceThreshold;
    }

    public void setTransactionPriceThreshold(BigDecimal transactionPriceThreshold) {
        this.transactionPriceThreshold = transactionPriceThreshold;
    }

    public Integer getVolumeThreshold() {
        return volumeThreshold;
    }

    public void setVolumeThreshold(Integer volumeThreshold) {
        this.volumeThreshold = volumeThreshold;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getMitigationStrategy() {
        return mitigationStrategy;
    }

    public void setMitigationStrategy(String mitigationStrategy) {
        this.mitigationStrategy = mitigationStrategy;
    }
    // Override the toString() method to return meaningful information about the object
    @Override
    public String toString() {
        return "RiskManagement {\n" +
                "  riskId=" + riskId + "\n" +
                "  transactionId=" + (transaction != null ? transaction.getTransactionId() : null) + "\n" + // Optional: Display associated transaction ID
                "  transactionPriceThreshold=" + transactionPriceThreshold + "\n" +
                "  volumeThreshold=" + volumeThreshold + "\n" +
                "  riskLevel='" + riskLevel + "'\n" +
                "  mitigationStrategy='" + mitigationStrategy + "'\n" +
                '}';
    }
}
