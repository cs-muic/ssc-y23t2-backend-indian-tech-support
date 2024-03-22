package io.muzoo.ssc.project.backend.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Transaction findById(long id);
    boolean existsById(long id);
    List<Transaction> findAllByUserId(long id);
    void deleteById(long id);
    //TODO Change the year to current year

    @Query("SELECT SUM(t.value) FROM Transaction t WHERE t.userId = :userId AND MONTH(t.timestamp) = :month AND YEAR(t.timestamp) = 2018 AND t.type = :type")
    Double sumAmountByUserIdAndMonthAndType(long userId, int month, Type type);

    @Query("SELECT t.tagId, SUM(t.value) FROM Transaction t WHERE t.userId = :userId AND MONTH(t.timestamp) = :month AND YEAR(t.timestamp) = 2018 AND t.type = :type GROUP BY t.tagId ORDER BY SUM(t.value) DESC")
    List<Object[]> sumAmountByUserIdAndMonthGroupByTag(long userId, int month, Type type);

    @Query("SELECT CASE :dateFormat WHEN 'Day' THEN DATE_FORMAT(t.timestamp, '%Y-%m-%d') WHEN 'Month' THEN DATE_FORMAT(t.timestamp, '%Y-%m') " +
            "WHEN 'Year' THEN DATE_FORMAT(t.timestamp, '%Y') END, SUM(t.value) FROM Transaction t WHERE t.userId = :userId AND t.timestamp BETWEEN :startDate AND :endDate " +
            "AND t.type = :transactionType GROUP BY CASE :dateFormat WHEN 'Year' THEN DATE_FORMAT(t.timestamp, '%Y') WHEN 'Month' THEN DATE_FORMAT(t.timestamp, '%Y-%m')" +
            " WHEN 'Day' THEN DATE_FORMAT(t.timestamp, '%Y-%m-%d') END")
    List<Object[]> getChartDataNoTag(long userId, Timestamp startDate, Timestamp endDate, Type transactionType, String dateFormat);


    @Query("SELECT CASE :dateFormat WHEN 'Day' THEN DATE_FORMAT(t.timestamp, '%Y-%m-%d') WHEN 'Month' THEN DATE_FORMAT(t.timestamp, '%Y-%m') WHEN 'Year' THEN DATE_FORMAT(t.timestamp, '%Y') END, " +
            "SUM(t.value) FROM Transaction t WHERE t.userId = :userId AND t.timestamp BETWEEN :startDate AND :endDate AND t.type = :transactionType AND " +
            "((t.tagId = :tag1 AND :tag2 = -1) OR (t.tagId2 = :tag2 AND :tag1 = -1) OR (t.tagId = :tag1 AND t.tagId2 = :tag2 AND :tag1 != -1 AND :tag2 != -1)) " +
            "GROUP BY CASE :dateFormat WHEN 'Year' THEN DATE_FORMAT(t.timestamp, '%Y') WHEN 'Month' THEN DATE_FORMAT(t.timestamp, '%Y-%m') WHEN 'Day' THEN DATE_FORMAT(t.timestamp, '%Y-%m-%d') END")
    List<Object[]> getChartData(long userId, Timestamp startDate, Timestamp endDate, Type transactionType, String dateFormat, long tag1, long tag2);



}
