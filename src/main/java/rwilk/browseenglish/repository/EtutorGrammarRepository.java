package rwilk.browseenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.etutor.EtutorGrammar;

import java.util.List;

@Repository
public interface EtutorGrammarRepository extends JpaRepository<EtutorGrammar, Long> {

  List<EtutorGrammar> findAllByLesson_Id(Long lessonId);

}
