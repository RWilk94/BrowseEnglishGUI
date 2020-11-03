package rwilk.browseenglish.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.SentenceRepository;

@Service
public class SentenceService {

  private final SentenceRepository sentenceRepository;

  public SentenceService(SentenceRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

  public List<Sentence> findAll(String wordEnName, String wordPlName) {
    return sentenceRepository.findAllByWord_EnNameAndWord_PlName(wordEnName, wordPlName);
  }

  public List<Sentence> findAll(Word word) {
    return sentenceRepository.findAllByWord(word);
  }

  public void deleteAll(List<Sentence> sentences) {
    sentenceRepository.deleteAll(sentences);
  }

  public List<Sentence> saveAll(List<Sentence> sentences) {
    return sentenceRepository.saveAll(sentences);
  }

  public List<Sentence> findAllByWordAndIsSentenceMatched(Word word, boolean isSentenceMatched) {
    return sentenceRepository.findAllByWordAndIsSentenceMatched(word, isSentenceMatched);
  }

  public Sentence save(Sentence sentence) {
    return sentenceRepository.save(sentence);
  }

  public Optional<Sentence> findById(Long id) {
    return sentenceRepository.findById(id);
  }

  public void deleteById(Long id) {
    sentenceRepository.deleteById(id);
  }

  public List<Sentence> findAllByEnSentenceContainsOrWord(String enName, Word word) {
    return sentenceRepository.findAllByEnSentenceContainsOrWord(enName, word);
  }
}
