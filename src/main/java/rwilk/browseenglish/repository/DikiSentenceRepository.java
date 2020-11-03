package rwilk.browseenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.diki.DikiSentence;
import rwilk.browseenglish.model.diki.DikiTranslation;

@Repository
public interface DikiSentenceRepository extends JpaRepository<DikiSentence, Long> {

  List<DikiSentence> findAllByDikiTranslation(DikiTranslation dikiTranslation);

}
