package rwilk.browseenglish.controller.word;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.controller.main.MainController;
import rwilk.browseenglish.controller.tool.ToolBarController;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.DikiSentenceRepository;
import rwilk.browseenglish.repository.DikiTranslationRepository;
import rwilk.browseenglish.repository.DikiWordRepository;
import rwilk.browseenglish.repository.SentenceRepository;
import rwilk.browseenglish.repository.WordRepository;
import rwilk.browseenglish.service.DikiSentenceService;
import rwilk.browseenglish.service.DikiTranslationService;
import rwilk.browseenglish.service.DikiWordService;
import rwilk.browseenglish.service.FormWordService;
import rwilk.browseenglish.service.SentenceService;
import rwilk.browseenglish.service.WordService;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@Getter
@Controller
public class WordTableController implements Initializable {

  private final MainController mainController;
  private final ToolBarController toolBarController;
  private final DuplicateTableController duplicateTableController;
  private final DikiWordService dikiWordService;
  private final DikiTranslationService dikiTranslationService;
  private final SentenceService sentenceService;
  private final WordService wordService;
  private final DikiSentenceService dikiSentenceService;
  private final FormWordService formWordService;

  public TableView<Word> tableViewWords;
  public TableColumn<Word, Long> columnId;
  public TableColumn<Word, String> columnEnName;
  public TableColumn<Word, String> columnUsName;
  public TableColumn<Word, String> columnOtherName;
  public TableColumn<Word, String> columnPlName;
  public TableColumn<Word, String> columnLevel;
  public TableColumn<Word, String> columnLesson;
  public TableColumn<Word, String> columnPartOfSpeech;
  public TableColumn<Word, String> columnComparative;
  public TableColumn<Word, String> columnSuperlative;
  public TableColumn<Word, String> columnPastTense;
  public TableColumn<Word, String> columnPastParticiple;
  public TableColumn<Word, String> columnPlural;
  public TableColumn<Word, String> columnSynonym;
  public TableColumn<Word, String> columnGrammar;
  public TextField textFieldFilterByLesson;
  public TextField textFieldFilterByCourse;
  public TextField textFieldFilterByName;
  public TextField textFieldFilterByLevel;

  private List<Word> words;

  @Autowired
  public WordTableController(WordRepository wordRepository, SentenceRepository sentenceRepository, DikiWordRepository dikiWordRepository,
      DikiTranslationRepository dikiTranslationRepository, DikiSentenceRepository dikiSentenceRepository, MainController mainController,
      ToolBarController toolBarController, DuplicateTableController duplicateTableController, DikiWordService dikiWordService,
      DikiTranslationService dikiTranslationService, SentenceService sentenceService, WordService wordService,
      DikiSentenceService dikiSentenceService, FormWordService formWordService) {
    this.mainController = mainController;
    this.toolBarController = toolBarController;
    this.duplicateTableController = duplicateTableController;
    this.dikiWordService = dikiWordService;
    this.dikiTranslationService = dikiTranslationService;
    this.sentenceService = sentenceService;
    this.wordService = wordService;
    this.dikiSentenceService = dikiSentenceService;
    this.formWordService = formWordService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    formWordService.setWordTableController(this);

    initializeTableView();
    fillInTableView();

    textFieldFilterByLesson.textProperty().addListener((observable, oldValue, newValue) -> filterTableByLesson(newValue));
    textFieldFilterByLesson.setOnMouseClicked(view -> filterTableByLesson(textFieldFilterByLesson.getText()));
    textFieldFilterByCourse.textProperty().addListener((observable, oldValue, newValue) -> filterTableByCourse(newValue));
    textFieldFilterByCourse.setOnMouseClicked(view -> filterTableByCourse(textFieldFilterByCourse.getText()));
    textFieldFilterByName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldFilterByName.setOnMouseClicked(view -> filterTableByName(textFieldFilterByName.getText()));
    textFieldFilterByLevel.textProperty().addListener((observable, oldValue, newValue) -> filterTableByLevel(newValue));
    textFieldFilterByLevel.setOnMouseClicked(view -> filterTableByLevel(textFieldFilterByLevel.getText()));

    tableViewWords.setRowFactory(row -> new TableRow<Word>() {
      @Override
      protected void updateItem(Word item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          if (item.isWordGrouped() && item.isWordReady()) {
            if (item.getLesson().getEnName().contains("SNAPSHOT")) {
              this.setStyle("-fx-background-color: #07cded");
            } else {
              this.setStyle("-fx-background-color: #18a90a");
            }
          } else if (item.isWordGrouped() || item.getLesson().getCourse().getEnName().contains("Speak Languages")) {
            this.setStyle("-fx-background-color: #32ff20");
          } else {
            setStyle("");
          }

//          if (item.getFrequency() != null && item.isWordReady() && item.isWordWithSentence()) {
//            this.setStyle("-fx-background-color: #0A84B2");
//          } else if (item.getFrequency() != null) {
//            this.setStyle("-fx-background-color: #0AC4FA");
//          } else if (item.isWordReady() && item.isWordWithSentence()) {
//            this.setStyle("-fx-background-color: #18a90a");
//          } else if (item.isWordReady() || item.isWordWithSentence()) {
//            this.setStyle("-fx-background-color: #32ff20");
//          }
        } else {
          setStyle("");
        }
      }
    });

  }

  public void tableViewWordsOnMouseClicked(MouseEvent mouseEvent) {
    formWordService.getWordFormController().removeAllTabs();
    tableViewWordsChangeFocus(null, mouseEvent);
  }

  public void tableViewWordsOnKeyReleased(KeyEvent keyEvent) {
    formWordService.getWordFormController().removeAllTabs();
    tableViewWordsChangeFocus(null, keyEvent);
  }

  public void refreshTable(Word word) {
    words.set(findWordById(word.getId()), word);
    if (!textFieldFilterByCourse.getText().isEmpty()) {
      filterTableByCourse(textFieldFilterByCourse.getText());
    }
    if (!textFieldFilterByLesson.getText().isEmpty()) {
      filterTableByLesson(textFieldFilterByLesson.getText());
    }
    if (!textFieldFilterByName.getText().isEmpty()) {
      filterTableByName(textFieldFilterByName.getText());
    }
    if (!textFieldFilterByLevel.getText().isEmpty()) {
      filterTableByLevel(textFieldFilterByLevel.getText());
    }
  }

  public void removeWord(Word word) {
    words.remove(findWordById(word.getId()));
    if (!textFieldFilterByCourse.getText().isEmpty()) {
      filterTableByCourse(textFieldFilterByCourse.getText());
    }
    if (!textFieldFilterByLesson.getText().isEmpty()) {
      filterTableByLesson(textFieldFilterByLesson.getText());
    }
    if (!textFieldFilterByName.getText().isEmpty()) {
      filterTableByName(textFieldFilterByName.getText());
    }

    List<Word> filteredWords = words.stream()
        .filter(w -> w.getSound() == null).collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));

  }

  private int findWordById(Long id) {
    List<Long> ids = words.stream().map(Word::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnEnName, columnPlName), 0.16);
    initializeColumns(Arrays.asList(columnUsName, columnOtherName, columnPartOfSpeech), 0.14);
    initializeColumns(Arrays.asList(
        columnLesson, columnComparative,
        columnSuperlative, columnPastTense, columnPastParticiple, columnPlural, columnSynonym, columnGrammar), 0.2);
    initializeColumns(Arrays.asList(columnId, columnLevel), 0.035);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableViewWords.widthProperty().multiply(other)));
  }

  private void fillInTableView() {

//    words = wordService.findAll();
//    words = words.stream()
//        .filter(word -> word.getLesson().getCourse().getId().compareTo(77L) == 0 || word.getLesson().getCourse().getId().compareTo(78L) == 0)
//        .collect(Collectors.toList());
//    words.forEach(word -> {
//      if (!(word.isWordReady() && word.isWordGrouped())) {
//        word.setWordReady(true);
//        word.setWordGrouped(true);
//        wordService.save(word);
//      }
//    });

    words = wordService.findAll()
        .stream()
        // TODO clear after fixing audio
        .filter(word -> word.getSound() == null)
        // .filter(word -> !word.isWordSkipped())
        // FIXME clear below filters
        // .filter(word -> word.getLevel().equals("A1") || word.getLevel().equals("A2") || word.getLevel().equals("B1"))
        // .filter(word -> !word.isWordGrouped())
        .collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(words));
    setTextStatus(words);
//    setTextStatus(
//        words.stream().filter(Word::isWordWithSentence).count() + "/" + words.stream().filter(Word::isWordGrouped).count() + "/" + words
//            .size());
  }

  private void filterTableByCourse(String value) {
    List<Word> filteredWords = words.stream()
        .filter(word -> word.getLesson().getCourse().getEnName().toLowerCase().contains(value.toLowerCase()))
        // FIXME clear below filter
//        .filter(word -> !word.isWordGrouped())
        // .filter(word -> word.getLevel().equals("A1") || word.getLevel().equals("A2") || word.getLevel().equals("B1"))
        .collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));
    setTextStatus(words);
//    setTextStatus(
//        words.stream().filter(Word::isWordWithSentence).count() + "/" + words.stream().filter(Word::isWordGrouped).count() + "/" + words
//            .size());
  }

  private void filterTableByLesson(String value) {
    List<Word> filteredWords = words.stream()
        .filter(word -> word.getLesson().getEnName().toLowerCase().contains(value.toLowerCase()))
        // FIXME clear below filter
//        .filter(word -> !word.isWordGrouped())
        // .filter(word -> word.getLevel().equals("A1") || word.getLevel().equals("A2") || word.getLevel().equals("B1"))
        .collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));
    tableViewWords.getSortOrder().add(columnLevel);
    setTextStatus(words);
//    setTextStatus(
//        words.stream().filter(Word::isWordWithSentence).count() + "/" + words.stream().filter(Word::isWordGrouped).count() + "/" + words
//            .size());
  }

  private void filterTableByName(String value) {
    List<Word> filteredWords = words.stream()
        .filter(word ->
            word.getEnName().toLowerCase().contains(value.toLowerCase())
                || word.getPlName().toLowerCase().contains(value.toLowerCase())
                || word.getUsName().toLowerCase().contains(value.toLowerCase())
                || word.getOtherNames().toLowerCase().contains(value.toLowerCase()))
        // FIXME clear below filter
//        .filter(word -> !word.isWordGrouped())
        // .filter(word -> word.getLevel().equals("A1") || word.getLevel().equals("A2") || word.getLevel().equals("B1"))
        .collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));

    setTextStatus(words);
//    setTextStatus(
//        words.stream().filter(Word::isWordWithSentence).count() + "/" + words.stream().filter(Word::isWordGrouped).count() + "/" + words
//            .size());
  }

  private void filterTableByLevel(String value) {
    if (StringUtils.isNotEmpty(value)) {
      List<Word> filteredWords = words.stream()
          .filter(word -> word.getLevel().equals(value))
          // FIXME clear below filter
//          .filter(word -> !word.isWordGrouped())
          // .filter(word -> word.getLevel().equals("A1") || word.getLevel().equals("A2") || word.getLevel().equals("B1"))
          .collect(Collectors.toList());
      tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));

      setTextStatus(words);
//      setTextStatus(
//          words.stream().filter(Word::isWordWithSentence).count() + "/" + words.stream().filter(Word::isWordGrouped).count() + "/" + words
//              .size());
    }
  }

  public void changeFocus(Word word) {
    tableViewWordsChangeFocus(word, null);
  }

  private void tableViewWordsChangeFocus(Word w, Event event) {
    Word selectedWord;
    if (w != null) {
      selectedWord = w;
    } else {
      selectedWord = tableViewWords.getSelectionModel().getSelectedItem();
    }
    if (event != null) {
      ((TableView) event.getSource()).getParent().requestFocus();
    }
    Optional.ofNullable(selectedWord).ifPresent(word -> {
      formWordService.getWordFormController().setFormFields(word);
      // set filter in duplicateTable
      duplicateTableController.setPlTranslation(word.getPlName());
      duplicateTableController.textFieldFilterByName.setText(word.getEnName());

/*      // find translations in database
      new Thread(() -> {
        List<DikiWord> dikiWords = dikiWordService.findAll(word.getEnName());
        if (dikiWords.stream().noneMatch(dikiWord -> dikiWord.getSource().equals("diki"))) {
          List<DikiWord> dikiTranslations = new DikiScrapper().webScrap(word.getEnName());
          dikiWordService.saveDikiWords(dikiTranslations, word);
          dikiWords.addAll(dikiTranslations);
        }
        if (dikiWords.stream().noneMatch(dikiWord -> dikiWord.getSource().equals("bab"))) {
          List<DikiWord> babTranslations = new BabScrapper().webScrap(word.getEnName());
          dikiWordService.saveDikiWords(babTranslations, word);
          dikiWords.addAll(babTranslations);
        }
        for (DikiWord dikiWord : dikiWords) {
          dikiWord.setDikiTranslations(dikiTranslationService.findAll(dikiWord));
          for (DikiTranslation dikiTranslation : dikiWord.getDikiTranslations()) {
            dikiTranslation.setDikiSentences(dikiSentenceService.findAll(dikiTranslation));
          }
        }
        word.setDikiWords(dikiWords);

        // find sentences in database
        word.setSentences(sentenceService.findAll(word.getEnName(), word.getPlName()));

        // call new Runnable object and call UI update inside
        Runnable runnable = () -> {
          // update form and recreate tabs with translations
          formWordService.getWordFormController().updateViewAfterChangeFocus(word);
        };
        // run UI update on main thread
        Platform.runLater(runnable);
      }).start();*/
    });
  }

  private void setTextStatus(List<Word> words) {
    Optional.ofNullable(toolBarController.getTextStatus()).ifPresent(textStatus -> textStatus.setText(
        words.stream().filter(word -> word.getLesson().getEnName().contains("SNAPSHOT")).count() + "/" + words.size())
    );
  }

  private void setTextStatus(String text) {
    Optional.ofNullable(toolBarController.getTextStatus()).ifPresent(textStatus -> textStatus.setText(text));
  }

}
