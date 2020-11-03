package rwilk.browseenglish.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.browseenglish.release.entity.ExerciseRelease;

@Repository
public interface ExerciseReleaseRepository extends JpaRepository<ExerciseRelease, Long> {
}
