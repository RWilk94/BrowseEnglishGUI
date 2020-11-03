package rwilk.browseenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rwilk.browseenglish.model.diki.DikiSentence;
import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.repository.DikiSentenceRepository;

@Service
public class DikiSentenceService {

  private final DikiSentenceRepository dikiSentenceRepository;

  @Autowired
  public DikiSentenceService(DikiSentenceRepository dikiSentenceRepository) {
    this.dikiSentenceRepository = dikiSentenceRepository;
  }

  public void saveDikiSentences(List<DikiSentence> dikiSentences, DikiTranslation dikiTranslation) {
    for (DikiSentence dikiSentence : dikiSentences) {
      dikiSentence.setDikiTranslation(dikiTranslation);
      dikiSentenceRepository.save(dikiSentence);
    }
  }

  public List<DikiSentence> findAll(DikiTranslation dikiTranslation) {
    return dikiSentenceRepository.findAllByDikiTranslation(dikiTranslation);
  }

  public void deleteAll(List<DikiSentence> dikiSentences) {
    dikiSentenceRepository.deleteAll(dikiSentences);
  }
}
