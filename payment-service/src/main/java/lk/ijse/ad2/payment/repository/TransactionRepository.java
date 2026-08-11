package lk.ijse.ad2.payment.repository;

import lk.ijse.ad2.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
