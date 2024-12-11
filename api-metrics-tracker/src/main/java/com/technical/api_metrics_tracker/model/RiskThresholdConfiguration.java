package com.technical.api_metrics_tracker.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Entity
@Table(name = "risk_threshold_configuration")
public class RiskThresholdConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "contract_type")
    private String contractType;  // "Option" or "Future"

    @Column(name = "transaction_price_threshold", precision = 10, scale = 2)
    private BigDecimal transactionPriceThreshold;

    @Column(name = "volume_threshold")
    private Integer volumeThreshold;

    @Column(name = "market_factor", precision = 5, scale = 2)
    private BigDecimal marketFactor;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
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

    public BigDecimal getMarketFactor() {
        return marketFactor;
    }

    public void setMarketFactor(BigDecimal marketFactor) {
        this.marketFactor = marketFactor;
    }
}
