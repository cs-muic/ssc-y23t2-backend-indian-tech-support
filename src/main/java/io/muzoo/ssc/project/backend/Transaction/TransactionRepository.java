package io.muzoo.ssc.project.backend.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Transaction findById(long id);
    boolean existsById(long id);
    List<Transaction> findAllByUserId(long id);
    void deleteById(long id);

    @Query("SELECT SUM(t.value) FROM Transaction t WHERE t.userId = :userId AND MONTH(t.timestamp) = :month AND YEAR(t.timestamp) = 2018 AND t.type = :type")
    Double sumAmountByUserIdAndMonthAndType(long userId, int month, Type type);


}
