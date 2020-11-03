package rwilk.browseenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

  List<Sentence> findAllByWord(Word word);

  List<Sentence> findAllByWord_EnNameAndWord_PlName(String enName, String plName);

  List<Sentence> findAllByEnSentenceContainsOrWord(String enName, Word word);

  List<Sentence> findAllByWordAndIsSentenceMatched(Word word, boolean isSentenceMatched);

}
