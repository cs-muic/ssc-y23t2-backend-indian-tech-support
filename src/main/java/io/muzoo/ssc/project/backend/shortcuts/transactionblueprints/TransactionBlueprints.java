package io.muzoo.ssc.project.backend.shortcuts.transactionblueprints;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table
public class TransactionBlueprints {
    

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        private long userId;

        private long tagId;

        private long tagId2;

        @Enumerated(EnumType.ORDINAL)
        private io.muzoo.ssc.project.backend.Transaction.Type transactionType;

        @Enumerated(EnumType.STRING)
        private io.muzoo.ssc.project.backend.shortcuts.transactionblueprints.Type shortcutType;

        private String notes;

        private BigDecimal value;



}
