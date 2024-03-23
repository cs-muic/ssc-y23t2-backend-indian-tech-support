package io.muzoo.ssc.project.backend.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondaryTagRepository extends JpaRepository<Tag, Long> {
}
