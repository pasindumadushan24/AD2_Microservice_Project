package lk.ijse.ad2.payment.controller;

import lk.ijse.ad2.payment.model.Transaction;
import lk.ijse.ad2.payment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/charge")
    public ResponseEntity<?> chargePayment(@RequestBody Transaction transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setUserId(transactionRequest.getUserId());
        transaction.setReservationId(transactionRequest.getReservationId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setCardNumber(transactionRequest.getCardNumber());
        transaction.setTimestamp(LocalDateTime.now());

        String card = transactionRequest.getCardNumber();
        if (card != null && card.replaceAll("\\s+", "").length() >= 12) {
            transaction.setStatus("SUCCESS");
            String receipt = "RCP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            transaction.setReceiptNumber(receipt);
            Transaction saved = transactionRepository.save(transaction);
            return ResponseEntity.ok(saved);
        } else {
            transaction.setStatus("FAILED");
            transaction.setReceiptNumber("N/A");
            Transaction saved = transactionRepository.save(transaction);
            return ResponseEntity.badRequest().body(saved);
        }
    }

    @GetMapping("/receipt/{transactionId}")
    public ResponseEntity<?> getReceipt(@PathVariable Long transactionId) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isPresent()) {
            Transaction tx = transactionOpt.get();
            if ("SUCCESS".equals(tx.getStatus())) {
                return ResponseEntity.ok(tx);
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No receipt generated. Transaction failed.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found.");
    }
}
