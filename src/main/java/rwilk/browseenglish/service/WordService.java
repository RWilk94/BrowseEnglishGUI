package rwilk.browseenglish.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.WordRepository;

@Service
public class WordService {

  private final WordRepository wordRepository;

  public WordService(WordRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public List<Word> findAll() {
    return wordRepository.findAll();
  }

  public Optional<Word> findById(Long id) {
    return wordRepository.findById(id);
  }

  public void delete(Word word) {
    wordRepository.delete(word);
  }

  public Word save(Word word) {
    return wordRepository.save(word);
  }

  public List<Word> findAll(Lesson lesson) {
    return wordRepository.findAllByLesson(lesson);
  }
}
