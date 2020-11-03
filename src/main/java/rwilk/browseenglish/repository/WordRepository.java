package rwilk.browseenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Word;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  List<Word> findAllByEnNameAndIsSentenceScrapped(String enName, Boolean sentenceScrapped);

  List<Word> findAllByEnNameAndIsWordScrapped(String enName, Boolean dikiScrapped);

  List<Word> findAllByEnNameOrEnNameOrUsNameOrUsName(String enName, String enNameBre, String usName, String usNameAme);

  List<Word> findAllByLesson(Lesson lesson);
}
