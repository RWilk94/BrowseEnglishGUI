package rwilk.browseenglish.controller.tool;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.controller.course.CourseController;
import rwilk.browseenglish.controller.group.GroupFormController;
import rwilk.browseenglish.controller.lesson.LessonController;
import rwilk.browseenglish.controller.view.LeftSideController;
import rwilk.browseenglish.release.controller.MainReleaseController;
import rwilk.browseenglish.release.controller.ReleaseContextController;
import rwilk.browseenglish.service.CourseService;
import rwilk.browseenglish.service.FormWordService;
import rwilk.browseenglish.service.LessonService;
import rwilk.browseenglish.service.WordService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@SuppressWarnings("unused")
@Getter
@Controller
public class ToolBarController implements Initializable {

  private final CourseService courseService;
  private final LessonService lessonService;
  private final WordService wordService;
  private final FormWordService formWordService;
  private final LeftSideController leftSideController;
  private final ReleaseContextController releaseContextController;
  private GroupFormController groupFormController;

  public ToggleButton toggleButtonTranslations;
  public ToggleButton toggleButtonLessons;
  public HBox hBoxPinnedLesson;
  public Text textStatus;
  public CheckBox checkBoxShowColumnUsName;
  public CheckBox checkBoxShowColumnOtherName;
  public CheckBox checkBoxShowColumnLevel;
  public CheckBox checkBoxShowColumnLesson;
  public CheckBox checkBoxShowColumnPartOfSpeech;
  public CheckBox checkBoxShowColumnComparative;
  public CheckBox checkBoxShowColumnSuperlative;
  public CheckBox checkBoxShowColumnPastTense;
  public CheckBox checkBoxShowColumnPastParticiple;
  public CheckBox checkBoxShowColumnPlural;
  public CheckBox checkBoxShowColumnSynonym;
  public CheckBox checkBoxShowColumnGrammar;

  private EventHandler<WindowEvent> closeCoursesWindowEvent = event ->
      Optional.ofNullable(groupFormController).ifPresent(GroupFormController::refreshAccordionCoursesView);

  private EventHandler<WindowEvent> closeLessonsWindowEvent = event ->
      Optional.ofNullable(groupFormController).ifPresent(GroupFormController::refreshAccordionCoursesView);

  public ToolBarController(CourseService courseService, LessonService lessonService,
                           WordService wordService, FormWordService formWordService, GroupFormController groupFormController,
                           LeftSideController leftSideController, ReleaseContextController releaseContextController) {
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.formWordService = formWordService;
    this.groupFormController = groupFormController;
    this.leftSideController = leftSideController;
    this.releaseContextController = releaseContextController;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    ToggleGroup toggleGroup = new ToggleGroup();
    toggleButtonTranslations.setToggleGroup(toggleGroup);
    toggleButtonTranslations.setSelected(true);
    toggleButtonLessons.setToggleGroup(toggleGroup);

    groupFormController.setPinnedHbox(hBoxPinnedLesson);
  }

  public void checkBoxShowColumnUsNameOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnUsName().setVisible(checkBoxShowColumnUsName.isSelected());
  }

  public void checkBoxShowColumnOtherNameOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnOtherName().setVisible(checkBoxShowColumnOtherName.isSelected());
  }

  public void checkBoxShowColumnLevelOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnLevel().setVisible(checkBoxShowColumnLevel.isSelected());
  }

  public void checkBoxShowColumnLessonOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnLesson().setVisible(checkBoxShowColumnLesson.isSelected());
  }

  public void checkBoxShowColumnPartOfSpeechOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnPartOfSpeech().setVisible(checkBoxShowColumnPartOfSpeech.isSelected());
  }

  public void checkBoxShowColumnComparativeOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnComparative().setVisible(checkBoxShowColumnComparative.isSelected());
  }

  public void checkBoxShowColumnSuperlativeOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnSuperlative().setVisible(checkBoxShowColumnSuperlative.isSelected());
  }

  public void checkBoxShowColumnPastTenseOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnPastTense().setVisible(checkBoxShowColumnPastTense.isSelected());
  }

  public void checkBoxShowColumnPastParticipleOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnPastParticiple().setVisible(checkBoxShowColumnPastParticiple.isSelected());
  }

  public void checkBoxShowColumnPluralOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnPlural().setVisible(checkBoxShowColumnPlural.isSelected());
  }

  public void checkBoxShowColumnSynonymOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnSynonym().setVisible(checkBoxShowColumnSynonym.isSelected());
  }

  public void checkBoxShowColumnGrammarOnAction(ActionEvent actionEvent) {
    formWordService.getWordTableController().getColumnGrammar().setVisible(checkBoxShowColumnGrammar.isSelected());
  }

  public void toggleButtonTranslationsOnAction(ActionEvent actionEvent) {
    leftSideController.scrapperForm.setVisible(true);
    leftSideController.groupForm.setVisible(false);
  }

  public void toggleButtonLessonsOnAction(ActionEvent actionEvent) {
    leftSideController.scrapperForm.setVisible(false);
    leftSideController.groupForm.setVisible(true);
  }

  public void openReleasePanelOnAction(ActionEvent actionEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/scene/release/main_release.fxml"));
    BorderPane borderPane = fxmlLoader.load();
    MainReleaseController item = fxmlLoader.getController();
    item.init(releaseContextController);
    createScene(borderPane, "Release", null);
  }

  public void openCoursePanelOnAction(ActionEvent actionEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/scene/course/course.fxml"));
    BorderPane borderPane = fxmlLoader.load();
    CourseController item = fxmlLoader.getController();
    item.init(courseService);
    createScene(borderPane, "Courses", closeCoursesWindowEvent);
  }

  public void openLessonPanelOnAction(ActionEvent actionEvent) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("/scene/lesson/lesson.fxml"));
    BorderPane borderPane = fxmlLoader.load();
    LessonController item = fxmlLoader.getController();
    item.init(lessonService, courseService, wordService);
    createScene(borderPane, "Lessons", closeLessonsWindowEvent);
  }

  public void toggleButtonUnpinAllOnAction(ActionEvent actionEvent) {
    this.hBoxPinnedLesson.getChildren().clear();
  }

  private void createScene(BorderPane borderPane, String title, EventHandler<WindowEvent> eventHandler) {
    Scene scene = new Scene(borderPane, 1366, 766);
    Stage stage = new Stage();
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
    if (eventHandler != null) {
      scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, eventHandler);
    }
  }

}
