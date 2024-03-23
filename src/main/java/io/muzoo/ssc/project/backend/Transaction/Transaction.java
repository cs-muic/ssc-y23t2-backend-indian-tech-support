package io.muzoo.ssc.project.backend.Transaction;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
//@Builder
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    private long tagId;

    private long tagId2;

    @Enumerated(EnumType.ORDINAL)
    private Type type;

    private String notes;

    private BigDecimal value;

    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp timestamp;

    public Transaction() {}
}
