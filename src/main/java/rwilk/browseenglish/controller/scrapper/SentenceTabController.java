package rwilk.browseenglish.controller.scrapper;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.Setter;
import rwilk.browseenglish.controller.word.WordFormController;
import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.util.Tool;

@Controller
public class SentenceTabController implements Initializable {

  private List<Sentence> sentences;
  public TableView<Sentence> tableViewSentences;
  public TableColumn<Sentence, String> columnId;
  public TableColumn<Sentence, String> columnEnSentence;
  public TableColumn<Sentence, String> columnPlSentence;
  public TableColumn<Sentence, String> columnSource;
  public TextField textFieldFilter;

  @Setter
  private WordFormController wordFormController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    textFieldFilter.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
  }

  public void initialize(List<Sentence> sentences) {
    this.sentences = sentences;
    initializeTableView();
    fillInTableView(sentences);
  }

  public void tableViewSentencesOnMouseClicked(MouseEvent mouseEvent) {
    Sentence selectedSentence = tableViewSentences.getSelectionModel().getSelectedItem();
    if (selectedSentence != null) {
      wordFormController.textFieldSentenceEn.setText(selectedSentence.getEnSentence());
      wordFormController.textFieldSentencePl.setText(selectedSentence.getPlSentence());
      wordFormController.textFieldSentenceId.setText(String.valueOf(selectedSentence.getId()));
    }
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnId, columnSource), 0.09);
    initializeColumns(Arrays.asList(columnEnSentence, columnPlSentence), 0.4);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableViewSentences.widthProperty().multiply(other)));
  }

  private void fillInTableView(List<Sentence> sentences) {
    sentences = sentences.stream().filter(Tool.distinctByKey(sentence -> sentence.getEnSentence() + " " + sentence.getPlSentence())).collect(Collectors.toList());
    tableViewSentences.setItems(FXCollections.observableArrayList(sentences));
  }

  private void filterTable(String value) {
    List<Sentence> filteredSentences = sentences.stream()
        .filter(sentence -> sentence.getEnSentence().toLowerCase().contains(value.toLowerCase()) || sentence.getPlSentence().toLowerCase().contains(value.toLowerCase()))
        .collect(Collectors.toList());
    tableViewSentences.setItems(FXCollections.observableArrayList(filteredSentences));
  }
}
