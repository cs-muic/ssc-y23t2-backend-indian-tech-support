package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.repositories;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.TransactionBlueprints;

@Repository
public interface TransactionBlueprintsRepositories extends JpaRepository<TransactionBlueprints,Long> {
    
    Optional<TransactionBlueprints> findById(Long id);
    boolean existsById(Long id);
    List<TransactionBlueprints> findAllByUserId(Long id);
    void deleteById(Long id);
    
    
    
    

}
