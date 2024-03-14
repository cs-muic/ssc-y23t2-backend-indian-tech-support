package io.muzoo.ssc.project.backend.Transaction;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long userId;

    private long tagId;

    @Enumerated(EnumType.ORDINAL)
    private Type type;

    private String notes;

    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date timestamp;

}
