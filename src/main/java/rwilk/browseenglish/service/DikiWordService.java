package rwilk.browseenglish.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.DikiWordRepository;

@Service
public class DikiWordService {

  private final DikiWordRepository dikiWordRepository;
  private final DikiTranslationService dikiTranslationService;

  @Autowired
  public DikiWordService(DikiWordRepository dikiWordRepository, DikiTranslationService dikiTranslationService) {
    this.dikiWordRepository = dikiWordRepository;
    this.dikiTranslationService = dikiTranslationService;
  }

  public void saveDikiWords(List<DikiWord> dikiWords, Word word) {
    for (DikiWord dikiWord : dikiWords) {
      dikiWord.setWord(word);
      DikiWord addedDikiWord = dikiWordRepository.save(dikiWord);
      dikiTranslationService.saveDikiTranslations(dikiWord.getDikiTranslations(), addedDikiWord);
    }
  }

  public List<DikiWord> findAll(String wordEnName) {
    return dikiWordRepository.findAllByWord_EnName(wordEnName);
  }

  public List<DikiWord> findAll(Word word) {
    return dikiWordRepository.findAllByWord(word);
  }

  public void deleteAll(List<DikiWord> dikiWords) {
    dikiWordRepository.deleteAll(dikiWords);
  }

}
