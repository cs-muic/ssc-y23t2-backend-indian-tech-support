package io.muzoo.ssc.project.backend.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Transaction findById(long id);
    List<Transaction> findAllByUserId(long id);
}
