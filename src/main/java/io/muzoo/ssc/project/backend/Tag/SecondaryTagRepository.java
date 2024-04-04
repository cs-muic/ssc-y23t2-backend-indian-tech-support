package io.muzoo.ssc.project.backend.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondaryTagRepository extends JpaRepository<SecondaryTag, Long> {
    List<SecondaryTag> findAllByTagId(long id);
    List<Tag> findAllById(long id);

    @Query("SELECT st FROM SecondaryTag st\n" +
            "INNER JOIN Tag t ON st.tagId = t.id\n" +
            "WHERE st.isDeleted = false AND (t.userId = :userId OR t.userId = 1)\n" +
            "GROUP BY st.id\n")
    List<SecondaryTag> findAllByUserId(Long userId);
}
