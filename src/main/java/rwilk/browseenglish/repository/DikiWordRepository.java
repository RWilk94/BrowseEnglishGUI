package rwilk.browseenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.model.entity.Word;

@Repository
public interface DikiWordRepository extends JpaRepository<DikiWord, Long> {

  List<DikiWord> findAllByWord(Word word);

  List<DikiWord> findAllByWord_EnNameAndWord_PlName(String enName, String plName);

  List<DikiWord> findAllByWord_EnName(String enName);

}
