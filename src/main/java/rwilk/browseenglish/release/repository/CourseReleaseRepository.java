package rwilk.browseenglish.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.browseenglish.release.entity.CourseRelease;

@Repository
public interface CourseReleaseRepository extends JpaRepository<CourseRelease, Long> {
}
