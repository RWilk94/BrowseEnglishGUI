package rwilk.browseenglish.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.browseenglish.release.entity.LessonRelease;

@Repository
public interface LessonReleaseRepository extends JpaRepository<LessonRelease, Long> {
}
