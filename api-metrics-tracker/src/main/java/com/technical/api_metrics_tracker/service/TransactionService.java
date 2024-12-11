package com.technical.api_metrics_tracker.service;

import com.technical.api_metrics_tracker.model.DerivativeContract;
import com.technical.api_metrics_tracker.model.Trader;
import com.technical.api_metrics_tracker.model.Transaction;
import com.technical.api_metrics_tracker.model.TransactionMetrics;
import com.technical.api_metrics_tracker.repository.DerivativeContractRepository;
import com.technical.api_metrics_tracker.repository.TransactionMetricsRepository;
import com.technical.api_metrics_tracker.repository.TransactionRepository;
import com.technical.api_metrics_tracker.repository.traderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final traderRepository traderRepository;  // Corrected field type
    private final DerivativeContractRepository derivativeContractRepository;
    private final TransactionMetricsRepository transactionMetricsRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              traderRepository traderRepository,  // Corrected constructor argument
                              DerivativeContractRepository derivativeContractRepository,
                              TransactionMetricsRepository transactionMetricsRepository) {
        this.transactionRepository = transactionRepository;
        this.traderRepository = traderRepository;
        this.derivativeContractRepository = derivativeContractRepository;
        this.transactionMetricsRepository = transactionMetricsRepository;
    }

    // Method to log/store a transaction and its related details (Trader and Derivative Contract)
    public void logTransaction(long transactionId, String transactionType, int quantity, double transactionPrice,
                               long traderId, String traderName, double traderAccountBalance, String traderPhoneNumber,
                               long contractId, String contractType, double contractStrikePrice, String contractExpirationDate,
                               String contractUnderlyingAsset, long startTime, int statusCode) {
        try {
            // Save the Transaction entity
            Transaction transaction = new Transaction();
            transaction.setTransactionId(transactionId);
            transaction.setTransactionType(transactionType);
            transaction.setQuantity(quantity);
            transaction.setTransactionPrice(BigDecimal.valueOf(transactionPrice));
            transaction = transactionRepository.save(transaction);

            // Save the Trader entity
            Trader trader = new Trader();
            trader.setTraderId(traderId);
            trader.setName(traderName);
            trader.setAccountBalance(BigDecimal.valueOf(traderAccountBalance));
            trader.setPhoneNumber(traderPhoneNumber);
            trader.setTransaction(transaction);
            traderRepository.save(trader);

            // Save the DerivativeContract entity
            DerivativeContract derivativeContract = new DerivativeContract();
            derivativeContract.setContractId(contractId);
            derivativeContract.setContractType(contractType);
            derivativeContract.setStrikePrice(BigDecimal.valueOf(contractStrikePrice));
            derivativeContract.setExpirationDate(java.time.LocalDate.parse(contractExpirationDate));
            derivativeContract.setUnderlyingAsset(contractUnderlyingAsset);
            derivativeContract.setTransaction(transaction);
            derivativeContractRepository.save(derivativeContract);

            // Calculate and save TransactionMetrics
            TransactionMetrics transactionMetrics = calculateMetrics(transaction, startTime, statusCode);
            transactionMetricsRepository.save(transactionMetrics);

        } catch (Exception e) {
            throw new RuntimeException("An error occurred while logging the transaction", e);
        }
    }

    /**
     * Calculates metrics for the given transaction.
     *
     * @param transaction the transaction for which metrics are calculated
     * @param startTime   the start time of the transaction
     * @param statusCode  the HTTP status code
     * @return the calculated TransactionMetrics object
     */
    private TransactionMetrics calculateMetrics(Transaction transaction, long startTime, int statusCode) {
        TransactionMetrics metrics = transactionMetricsRepository.findById(transaction.getTransactionId())
                .orElse(new TransactionMetrics());

        long responseTime = System.currentTimeMillis() - startTime;

        // Update min and max response times
        metrics.setMinResponseTime(metrics.getMinResponseTime() == null ? responseTime : Math.min(metrics.getMinResponseTime(), responseTime));
        metrics.setMaxResponseTime(metrics.getMaxResponseTime() == null ? responseTime : Math.max(metrics.getMaxResponseTime(), responseTime));

        // Update average response time
        if (metrics.getTransactionCount() == null) {
            metrics.setTransactionCount(1);
            metrics.setAvgResponseTime(responseTime);
        } else {
            int newCount = metrics.getTransactionCount() + 1;
            long totalResponseTime = metrics.getAvgResponseTime() * metrics.getTransactionCount() + responseTime;
            metrics.setTransactionCount(newCount);
            metrics.setAvgResponseTime(totalResponseTime / newCount);
        }

        // Set response status
        String statusText = HttpStatus.valueOf(statusCode).getReasonPhrase();
        metrics.setResponseStatus(statusCode + " " + statusText);

        // Link metrics to the transaction
        metrics.setTransaction(transaction);
        metrics.setTimestamp(LocalDateTime.now());

        return metrics;
    }

    // Method to get transaction details by transactionId (unchanged)
    public String getTransactionDetailsById(long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> "Transaction ID: " + transaction.getTransactionId() +
                        "\nType: " + transaction.getTransactionType() +
                        "\nQuantity: " + transaction.getQuantity() +
                        "\nPrice: " + transaction.getTransactionPrice())
                .orElse("Transaction not found");
    }
}
