package rwilk.browseenglish.release.controller;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.model.etutor.EtutorGrammar;
import rwilk.browseenglish.release.entity.LessonRelease;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Controller
public class ReleaseTableController implements Initializable {

  private ReleaseContextController releaseContextController;

  public TableView<EtutorGrammar> tableViewWordReleases;
  public TableColumn<EtutorGrammar, Long> columnId;
  public TableColumn<EtutorGrammar, String> columnLesson;
  public TableColumn<EtutorGrammar, String> columnGrammarType;
  public TableColumn<EtutorGrammar, String> columnNote;
  public TableColumn<EtutorGrammar, String> columnQuestion;
  public TableColumn<EtutorGrammar, String> columnCorrectAnswer;
  public TableColumn<EtutorGrammar, String> columnCorrectAnswerAfterChoice;
  public TableColumn<EtutorGrammar, String> columnFirstPossibleAnswer;
  public TableColumn<EtutorGrammar, String> columnSecondPossibleAnswer;
  public TableColumn<EtutorGrammar, String> columnThirdPossibleAnswer;
  public TableColumn<EtutorGrammar, String> columnForthPossibleAnswer;
  public TableColumn<EtutorGrammar, String> columnDialogueLeft;
  public TableColumn<EtutorGrammar, String> columnDialogueRight;
  public TableColumn<EtutorGrammar, String> columnWordListLeft;
  public TableColumn<EtutorGrammar, String> columnWordListRight;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    // fillInTableView();
  }

  public void init(ReleaseContextController releaseContextController) {
    this.releaseContextController = releaseContextController;
    initializeTableView();
    fillInTableView();

    this.releaseContextController.setReleaseTableController(this);
  }

  private void initializeTableView() {
    initializeColumns(Arrays.asList(columnId, columnLesson), 0.08);
    initializeColumns(Arrays.asList(
        columnGrammarType, columnNote, columnQuestion, columnCorrectAnswer, columnCorrectAnswerAfterChoice,
        columnFirstPossibleAnswer, columnSecondPossibleAnswer, columnThirdPossibleAnswer, columnForthPossibleAnswer,
        columnDialogueLeft, columnDialogueRight, columnWordListLeft, columnWordListRight), 0.15);
  }

  private void initializeColumns(List<TableColumn<?, ?>> tableColumns, double other) {
    tableColumns.forEach(tableColumn -> tableColumn.prefWidthProperty().bind(tableViewWordReleases.widthProperty().multiply(other)));
  }

  public void fillInTableView() {
    tableViewWordReleases.setItems(null);

    List<LessonRelease> lessons = this.releaseContextController.getLessonReleaseRepository().findAll();

    List<EtutorGrammar> etutorGrammars = this.releaseContextController.getEtutorGrammarService().findAll()
        .stream()
        .filter(etutorGrammar -> lessons.stream().filter(lessonRelease -> lessonRelease.getId().compareTo(etutorGrammar.getLesson().getId()) == 0)
            .findFirst()
            .orElse(LessonRelease.builder().isReady(false).build())
            .getIsReady())
        .sorted(Comparator.comparing(etutorGrammar -> etutorGrammar.getLesson().getId()))
        .collect(Collectors.toList());

    tableViewWordReleases.setItems(FXCollections.observableArrayList(etutorGrammars));
  }

}
