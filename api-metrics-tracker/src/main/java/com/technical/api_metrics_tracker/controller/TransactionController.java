package com.technical.api_metrics_tracker.controller;

import com.technical.api_metrics_tracker.service.RiskManagementService;
import com.technical.api_metrics_tracker.service.TransactionService;
import com.technical.api_metrics_tracker.controller.TransactionDTO;
import com.technical.api_metrics_tracker.model.RiskManagement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")  // Add your frontend domain here for CORS
public class TransactionController {

    private final TransactionService transactionService;
    private final RiskManagementService riskManagementService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    public TransactionController(TransactionService transactionService, RiskManagementService riskManagementService) {
        this.transactionService = transactionService;
        this.riskManagementService = riskManagementService;
    }

    @PostMapping("/log")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<String> logTransaction(@RequestBody TransactionDTO request,
                                                 HttpServletRequest servletRequest) {
        long startTime = (long) servletRequest.getAttribute("startTime");

        try {
            // Extract attributes from all 4 entities and log/store them
            long transactionId = request.getTransactionId();
            String transactionType = request.getTransactionType();
            int quantity = request.getQuantity();
            double transactionPrice = request.getTransactionPrice();

            // Extract Trader details
            long traderId = request.getTraderId();
            String traderName = request.getTraderName();
            double traderAccountBalance = request.getTraderAccountBalance();
            String traderPhoneNumber = request.getTraderPhoneNumber();

            // Extract Derivative Contract details
            long contractId = request.getContractId();
            String contractType = request.getContractType();
            double contractStrikePrice = request.getContractStrikePrice();
            String contractExpirationDate = request.getContractExpirationDate();
            String contractUnderlyingAsset = request.getContractUnderlyingAsset();

            // Call the service to handle logging/storing the transaction data
            transactionService.logTransaction(transactionId, transactionType, quantity, transactionPrice, traderId, traderName,
                    traderAccountBalance, traderPhoneNumber, contractId, contractType,
                    contractStrikePrice, contractExpirationDate, contractUnderlyingAsset, startTime, HttpStatus.CREATED.value());

            // Calculate risk and store it in the RiskManagement table
            riskManagementService.calculateAndSaveRisk(transactionId, contractType, quantity, transactionPrice);

            return new ResponseEntity<>("Transaction and Risk details logged successfully", HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error while logging transaction", e);
            return new ResponseEntity<>("An error occurred while logging the transaction", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/risk/{transactionId}")
    @CrossOrigin(origins = "http://localhost:3000")  // Add CORS here for the specific method if needed
    public ResponseEntity<String> getRiskDetails(@PathVariable long transactionId) {
        try {
            RiskManagement riskManagement = riskManagementService.getRiskDetails(transactionId);
            return new ResponseEntity<>(riskManagement.toString(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Risk details not found for this transaction ID: " + transactionId, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred while retrieving risk details: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
