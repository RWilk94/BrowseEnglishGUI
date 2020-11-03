package rwilk.browseenglish.release.controller.word;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.release.entity.WordRelease;
import rwilk.browseenglish.release.service.WordReleaseService;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class WordReleaseTableController implements Initializable {

  private final WordReleaseService wordReleaseService;
  private final InjectFormService injectFormService;

  private List<WordRelease> words;

  public TableView<WordRelease> tableViewWords;
  public TableColumn<WordRelease, Long> columnId;
  public TableColumn<WordRelease, String> columnEnName;
  public TableColumn<WordRelease, String> columnUsName;
  public TableColumn<WordRelease, String> columnOtherName;
  public TableColumn<WordRelease, String> columnPlName;
  public TableColumn<WordRelease, String> columnLevel;
  public TableColumn<WordRelease, String> columnLesson;
  public TableColumn<WordRelease, String> columnPartOfSpeech;
  public TableColumn<WordRelease, String> columnComparative;
  public TableColumn<WordRelease, String> columnSuperlative;
  public TableColumn<WordRelease, String> columnPastTense;
  public TableColumn<WordRelease, String> columnPastParticiple;
  public TableColumn<WordRelease, String> columnPlural;
  public TableColumn<WordRelease, String> columnSynonym;
  public TableColumn<WordRelease, String> columnGrammar;

  public WordReleaseTableController(WordReleaseService wordReleaseService, InjectFormService injectFormService) {
    this.wordReleaseService = wordReleaseService;
    this.injectFormService = injectFormService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    injectFormService.setWordReleaseTableController(this);

    initializeTableView();
    fillInTableView();

    tableViewWords.setRowFactory(row -> new TableRow<WordRelease>() {
      @Override
      protected void updateItem(WordRelease item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && StringUtils.isNotEmpty(item.getSound())) {
          this.setStyle("-fx-background-color: #32ff20");
        } else {
          setStyle("");
        }

      }
    });
  }

  public void update(WordRelease word) {
    words.set(findWordById(word.getId()), word);
    tableViewWords.setItems(null);
    tableViewWords.setItems(FXCollections.observableArrayList(words));
  }

  public void tableViewWordsOnMouseClicked(MouseEvent mouseEvent) {
    Optional.ofNullable(tableViewWords.getSelectionModel().getSelectedItem()).
        ifPresent(wordRelease -> injectFormService.getWordReleaseFormController().setForm(wordRelease));
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
    words = wordReleaseService.findAll().stream()
         // .filter(wordRelease -> StringUtils.isEmpty(wordRelease.getSound()))
         // .filter(wordRelease -> wordRelease.getId() >= 23000L)
        .collect(Collectors.toList());
    tableViewWords.setItems(FXCollections.observableArrayList(words));
  }

  private int findWordById(Long id) {
    List<Long> ids = words.stream().map(WordRelease::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }

}
