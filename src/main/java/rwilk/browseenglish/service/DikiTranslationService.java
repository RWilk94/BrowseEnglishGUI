package rwilk.browseenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.repository.DikiTranslationRepository;

@Service
public class DikiTranslationService {

  private final DikiTranslationRepository dikiTranslationRepository;
  private final DikiSentenceService dikiSentenceService;

  @Autowired
  public DikiTranslationService(DikiTranslationRepository dikiTranslationRepository, DikiSentenceService dikiSentenceService) {
    this.dikiTranslationRepository = dikiTranslationRepository;
    this.dikiSentenceService = dikiSentenceService;
  }

  public void saveDikiTranslations(List<DikiTranslation> dikiTranslations, DikiWord dikiWord) {
    for (DikiTranslation dikiTranslation : dikiTranslations) {
      dikiTranslation.setDikiWord(dikiWord);
      DikiTranslation addedDikiTranslation = dikiTranslationRepository.save(dikiTranslation);
      dikiSentenceService.saveDikiSentences(dikiTranslation.getDikiSentences(), addedDikiTranslation);
    }
  }

  public List<DikiTranslation> findAll(DikiWord dikiWord) {
    return dikiTranslationRepository.findAllByDikiWord(dikiWord);
  }

  public void deleteAll(List<DikiTranslation> dikiTranslations) {
    dikiTranslationRepository.deleteAll(dikiTranslations);
  }
}
