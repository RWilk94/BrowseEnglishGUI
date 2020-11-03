package rwilk.browseenglish.service;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import rwilk.browseenglish.controller.word.DuplicateTableController;
import rwilk.browseenglish.controller.word.WordFormController;
import rwilk.browseenglish.controller.word.WordTableController;

@Service
@Getter
@Setter
public class FormWordService {

  private WordFormController wordFormController;
  private WordTableController wordTableController;
  private DuplicateTableController duplicateTableController;

}
