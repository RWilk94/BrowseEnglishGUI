package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.entity.SentenceRelease;
import rwilk.browseenglish.release.repository.SentenceReleaseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SentenceReleaseService {

  private final SentenceReleaseRepository sentenceRepository;

  @Autowired
  public SentenceReleaseService(SentenceReleaseRepository sentenceRepository) {
    this.sentenceRepository = sentenceRepository;
  }

  public List<SentenceRelease> findAll() {
    return sentenceRepository.findAll();
  }

  public Optional<SentenceRelease> findById(Long id) {
    return sentenceRepository.findById(id);
  }

  public SentenceRelease save(SentenceRelease sentence) {
    return sentenceRepository.save(sentence);
  }


}
