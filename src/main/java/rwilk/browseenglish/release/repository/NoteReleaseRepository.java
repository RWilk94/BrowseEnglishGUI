package rwilk.browseenglish.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.browseenglish.release.entity.NoteRelease;

@Repository
public interface NoteReleaseRepository extends JpaRepository<NoteRelease, Long> {
}
