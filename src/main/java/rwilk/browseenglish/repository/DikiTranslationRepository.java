package rwilk.browseenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.model.diki.DikiWord;

@Repository
public interface DikiTranslationRepository extends JpaRepository<DikiTranslation, Long> {

  List<DikiTranslation> findAllByDikiWord(DikiWord dikiWord);
}
