package rwilk.browseenglish.release.controller.sentence;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.release.controller.word.InjectFormService;
import rwilk.browseenglish.release.entity.SentenceRelease;
import rwilk.browseenglish.release.service.SentenceReleaseService;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class SentenceReleaseTableController implements Initializable {

  private final SentenceReleaseService sentenceReleaseService;
  private final InjectFormService injectFormService;

  private List<SentenceRelease> sentences;

  public TableView<SentenceRelease> tableViewSentences;
  public TableColumn<SentenceRelease, Long> columnId;
  public TableColumn<SentenceRelease, String> columnEnSentence;
  public TableColumn<SentenceRelease, String> columnPlSentence;
  public TableColumn<SentenceRelease, String> columnSound;
  public TableColumn<SentenceRelease, String> columnWord;

  public SentenceReleaseTableController(SentenceReleaseService sentenceReleaseService, InjectFormService injectFormService) {
    this.sentenceReleaseService = sentenceReleaseService;
    this.injectFormService = injectFormService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    injectFormService.setSentenceReleaseTableController(this);

    initializeTableView();
    fillInTableView();

    tableViewSentences.setRowFactory(row -> new TableRow<SentenceRelease>() {
      @Override
      protected void updateItem(SentenceRelease item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && StringUtils.isNotEmpty(item.getSound())) {
          this.setStyle("-fx-background-color: #32ff20");
        } else {
          setStyle("");
        }

      }
    });
  }

  public void update(SentenceRelease sentence) {
    sentences.set(findSentenceById(sentence.getId()), sentence);
    tableViewSentences.setItems(null);
    tableViewSentences.setItems(FXCollections.observableArrayList(sentences));
  }

  public void tableViewSentencesOnMouseClicked(MouseEvent mouseEvent) {
    Optional.ofNullable(tableViewSentences.getSelectionModel().getSelectedItem()).
        ifPresent(sentenceRelease -> injectFormService.getSentenceReleaseFormController().setForm(sentenceRelease));
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnEnSentence, columnPlSentence, columnSound, columnWord), 0.2);
    initializeColumns(Collections.singletonList(columnId), 0.035);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableViewSentences.widthProperty().multiply(other)));
  }

  private void fillInTableView() {
    sentences = sentenceReleaseService.findAll()
        .stream()
        .filter(sentence -> StringUtils.isEmpty(sentence.getSound()))
    .collect(Collectors.toList());
    tableViewSentences.setItems(FXCollections.observableArrayList(sentences));
  }

  private int findSentenceById(Long id) {
    List<Long> ids = sentences.stream().map(SentenceRelease::getId).collect(Collectors.toList());
    return ids.indexOf(id);
  }

}
