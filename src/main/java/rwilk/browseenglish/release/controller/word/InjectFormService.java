package rwilk.browseenglish.release.controller.word;

import lombok.Data;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.controller.scrapper.ScrapperFormController;
import rwilk.browseenglish.release.controller.sentence.SentenceReleaseFormController;
import rwilk.browseenglish.release.controller.sentence.SentenceReleaseTableController;

@Data
@Service
public class InjectFormService {

  private WordReleaseFormController wordReleaseFormController;
  private WordReleaseTableController wordReleaseTableController;

  private SentenceReleaseFormController sentenceReleaseFormController;
  private SentenceReleaseTableController sentenceReleaseTableController;

  private final ScrapperFormController scrapperFormController;

}
