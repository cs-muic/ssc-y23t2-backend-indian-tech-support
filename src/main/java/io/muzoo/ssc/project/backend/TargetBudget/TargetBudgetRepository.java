package io.muzoo.ssc.project.backend.TargetBudget;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetBudgetRepository extends JpaRepository<TargetBudget, Long>{

    TargetBudget findByUserId(long userId);
}