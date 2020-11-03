package rwilk.browseenglish.controller.word;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import lombok.Setter;
import rwilk.browseenglish.model.diki.DikiSentence;
import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.service.DikiSentenceService;
import rwilk.browseenglish.service.DikiTranslationService;
import rwilk.browseenglish.service.DikiWordService;
import rwilk.browseenglish.service.FormWordService;
import rwilk.browseenglish.service.SentenceService;
import rwilk.browseenglish.service.WordService;

@SuppressWarnings("unused")
@Controller
public class DuplicateTableController implements Initializable {

  private final FormWordService formWordService;
  private final WordService wordService;
  private final SentenceService sentenceService;
  private final DikiWordService dikiWordService;
  private final DikiTranslationService dikiTranslationService;
  private final DikiSentenceService dikiSentenceService;
  public TextField textFieldFilterByName;
  public CheckBox checkBoxWholeWord;
  public TextField textFieldId;
  public TableView<Word> tableViewWords;
  public TableColumn<Word, Long> columnId;
  public TableColumn<Word, String> columnEnName;
  public TableColumn<Word, String> columnUsName;
  public TableColumn<Word, String> columnOtherName;
  public TableColumn<Word, String> columnPlName;
  public TableColumn<Word, String> columnLevel;
  public TableColumn<Word, String> columnLesson;
  public TableColumn<Word, String> columnPartOfSpeech;
  public CheckBox checkBoxIsReady;
  private List<Word> words;
  @Getter
  @Setter
  private String plTranslation;

  public DuplicateTableController(FormWordService formWordService, WordService wordService, SentenceService sentenceService,
      DikiWordService dikiWordService, DikiTranslationService dikiTranslationService, DikiSentenceService dikiSentenceService) {
    this.formWordService = formWordService;
    this.wordService = wordService;
    this.sentenceService = sentenceService;
    this.dikiWordService = dikiWordService;
    this.dikiTranslationService = dikiTranslationService;
    this.dikiSentenceService = dikiSentenceService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    formWordService.setDuplicateTableController(this);

    initializeTableView();
    fillInTableView();

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
          } else if (StringUtils.isNotEmpty(plTranslation) && plTranslation.equals(item.getPlName())) {
            this.setStyle("-fx-background-color: #FFF200");
          } else {
            setStyle("");
          }

          //          if (item.getFrequency() != null && item.isWordReady() && item.isWordWithSentence()) {
          //            this.setStyle("-fx-background-color: #0A84B2");
          //          } else if (item.getFrequency() != null) {
          //            this.setStyle("-fx-background-color: #0AC4FA");
          //          } else if (StringUtils.isNotEmpty(plTranslation) && plTranslation.equals(item.getPlName())) {
          //            this.setStyle("-fx-background-color: #FFF200");
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

    textFieldId.textProperty().addListener((observable, oldValue, newValue) -> {
      if (StringUtils.isNotEmpty(newValue)) {
        wordService.findById(Long.valueOf(newValue)).ifPresent(word -> checkBoxIsReady.setSelected(word.isWordReady()));
      }
    });

    textFieldFilterByName.textProperty().addListener((observable, oldValue, newValue) -> filterTableByName(newValue));
    textFieldFilterByName.setOnMouseClicked(view -> filterTableByName(textFieldFilterByName.getText()));
  }

  public void checkBoxWholeWordOnAction(ActionEvent actionEvent) {
    filterTableByName(textFieldFilterByName.getText());
  }

  public void checkBoxIsReadyOnAction(ActionEvent actionEvent) {
    //if (!checkBoxIsReady.isSelected()) {
      wordService.findById(Long.valueOf(textFieldId.getText())).ifPresent(word -> {
        word.setWordReady(checkBoxIsReady.isSelected());
        word = wordService.save(word);
        updateWord(word);
        formWordService.getWordTableController().refreshTable(word);
      });
    //}
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      deleteWord(Long.valueOf(textFieldId.getText()));
    }
  }

  public void buttonDeleteAllOnAction(ActionEvent actionEvent) {
    ObservableList<Word> words = tableViewWords.getItems();
    words.stream().filter(word -> !word.isWordReady()).forEach(word -> deleteWord(word.getId()));
  }

  public void deleteWord(Long wordId) {
    Optional<Word> optionalWord = wordService.findById(wordId);
    optionalWord.ifPresent(word -> {
      if (!word.isWordReady()) {
        List<Sentence> sentences = sentenceService.findAll(word);
        sentenceService.deleteAll(sentences);

        List<DikiWord> dikiWords = dikiWordService.findAll(word);
        for (DikiWord dikiWord : dikiWords) {
          List<DikiTranslation> dikiTranslations = dikiTranslationService.findAll(dikiWord);
          for (DikiTranslation dikiTranslation : dikiTranslations) {
            List<DikiSentence> dikiSentences = dikiSentenceService.findAll(dikiTranslation);
            dikiSentenceService.deleteAll(dikiSentences);
          }
          dikiTranslationService.deleteAll(dikiTranslations);
        }
        dikiWordService.deleteAll(dikiWords);
        wordService.delete(word);

        removeWord(word);
        filterTableByName(textFieldFilterByName.getText());
        formWordService.getWordTableController().removeWord(word);
      }
    });

    filterTableByName(textFieldFilterByName.getText());
  }

  public void tableViewWordsOnMouseClicked(MouseEvent mouseEvent) {
    Word selectedWord = tableViewWords.getSelectionModel().getSelectedItem();
    if (selectedWord != null) {
      textFieldId.setText(String.valueOf(selectedWord.getId()));
       formWordService.getWordTableController().changeFocus(selectedWord);
    }
  }

  public void tableViewWordsOnKeyReleased(KeyEvent keyEvent) {
  }

  public void updateWord(Word word) {
    words.set(findWordById(word.getId()), word);
    filterTableByName(textFieldFilterByName.getText());
  }

  public void removeWord(Word word) {
    words.remove(findWordById(word.getId()));
    filterTableByName(textFieldFilterByName.getText());
  }

  public void filterTableByName(String value) {
    if (StringUtils.isNotEmpty(value)) {
      if (checkBoxWholeWord.isSelected()) {
        List<Word> filteredWords = words.stream()
            .filter(word -> word.getEnName().replaceAll("\\(British English\\)", "").replaceAll("British English", "").trim().toLowerCase()
                .equals(value.replaceAll("\\(British English\\)", "").replaceAll("British English", "").trim().toLowerCase()))
            .collect(Collectors.toList());
        tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));
      } else {
        List<Word> filteredWords = words.stream()
            .filter(word -> word.getEnName().toLowerCase().contains(value.toLowerCase().trim()))
            .collect(Collectors.toList());
        tableViewWords.setItems(FXCollections.observableArrayList(filteredWords));
      }
    }
    this.textFieldId.clear();
  }

  private int findWordById(Long id) {
    List<Long> ids = words.stream().map(Word::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(
        columnEnName, columnUsName, columnOtherName, columnPlName, columnLevel, columnLesson, columnPartOfSpeech), 0.15);
    initializeColumns(Arrays.asList(columnId, columnLevel), 0.05);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableViewWords.widthProperty().multiply(other)));
  }

  private void fillInTableView() {
    words = wordService.findAll()
        .stream()
        // .filter(word -> !word.isWordSkipped())
        .collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(words));
  }

}
