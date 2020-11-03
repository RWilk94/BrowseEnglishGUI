package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.entity.WordRelease;
import rwilk.browseenglish.release.repository.WordReleaseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WordReleaseService {

  private final WordReleaseRepository wordRepository;

  @Autowired
  public WordReleaseService(WordReleaseRepository wordRepository) {
    this.wordRepository = wordRepository;
  }

  public Optional<WordRelease> findById(Long id) {
    return wordRepository.findById(id);
  }

  public List<WordRelease> findAll() {
    return wordRepository.findAll();
  }

  public WordRelease save(WordRelease word) {
    return wordRepository.save(word);
  }

  public void delete(WordRelease word) {
    wordRepository.delete(word);
  }
}
