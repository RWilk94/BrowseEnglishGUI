package rwilk.browseenglish.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.browseenglish.release.entity.ExerciseRowRelease;

@Repository
public interface ExerciseRowReleaseRepository extends JpaRepository<ExerciseRowRelease, Long> {
}
