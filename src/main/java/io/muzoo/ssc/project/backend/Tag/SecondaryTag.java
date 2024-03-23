package io.muzoo.ssc.project.backend.Tag;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//@Builder
@Table(name = "secondary_tag")
public class SecondaryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long tagId;

    private String secondaryTagName;

    private boolean isDeleted;
}
