package rwilk.browseenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.etutor.EtutorLesson;

@Repository
public interface EtutorLessonRepository extends JpaRepository<EtutorLesson, Long> {
}
