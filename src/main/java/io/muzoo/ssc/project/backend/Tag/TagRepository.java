package io.muzoo.ssc.project.backend.Tag;

import io.muzoo.ssc.project.backend.Transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllById(long id);
    List<Tag> findAllByUserId(long id);
}
