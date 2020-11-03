package rwilk.browseenglish.controller.lesson;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.service.CourseService;
import rwilk.browseenglish.service.LessonService;
import rwilk.browseenglish.service.WordService;

@Controller
public class LessonFormController implements Initializable {

  public TextField textFieldURL;
  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;
  public ComboBox<Course> comboBoxCourse;
  private LessonService lessonService;
  private CourseService courseService;
  private WordService wordService;
  private LessonTableController lessonTableController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
  }

  public void init(LessonService lessonService, CourseService courseService,
      LessonTableController lessonTableController, WordService wordService) {
    this.lessonService = lessonService;
    this.courseService = courseService;
    this.lessonTableController = lessonTableController;
    this.wordService = wordService;

    initializeCourseComboBox();
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldEnName.clear();
    textFieldPlName.clear();
    textFieldId.clear();
    comboBoxCourse.getSelectionModel().select(null);
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      lessonService.findById(Long.valueOf(textFieldId.getText())).ifPresent(lesson -> {
        lessonService.delete(lesson);
      });
      buttonClearOnAction(actionEvent);
      refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText()) && StringUtils.isNotEmpty(textFieldEnName.getText())
        && StringUtils.isNotEmpty(textFieldPlName.getText())
        && !comboBoxCourse.getSelectionModel().isEmpty()) {

      Optional<Lesson> lessonOptional = lessonService.findById(Long.valueOf(textFieldId.getText()));
      lessonOptional.ifPresent(lesson -> {
        lesson.setEnName(textFieldEnName.getText().trim());
        lesson.setPlName(textFieldPlName.getText().trim());
        lesson.setCourse(comboBoxCourse.getSelectionModel().getSelectedItem());
        setLessonForm(lessonService.save(lesson));
        refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldEnName.getText()) && StringUtils.isNotEmpty(textFieldPlName.getText())
        && !comboBoxCourse.getSelectionModel().isEmpty()) {
      Lesson lesson = Lesson.builder()
          .enName(textFieldEnName.getText().trim())
          .plName(textFieldPlName.getText().trim())
          .course(courseService.findById((comboBoxCourse.getSelectionModel().getSelectedItem()).getId()).get())
          .build();
      lesson = lessonService.save(lesson);
      setLessonForm(lesson);
      refreshTableView();
    }
  }

  public void buttonSpeakLanguagesOnAction(ActionEvent actionEvent) {
    //    if (StringUtils.isNotEmpty(textFieldURL.getText()) && StringUtils.isNotEmpty(textFieldId.getText())) {
    //      lessonService.findById(Long.valueOf(textFieldId.getText())).ifPresent(lesson -> {
    //
    //        List<Word> words = new SpeaklanguagesScrapper().webScrapLesson(textFieldURL.getText());
    //        for (Word word : words) {
    //          word.setLesson(lesson);
    //          word.setLevel("A1");
    //          word.setUsName("");
    //          word.setOtherNames("");
    //          word = wordService.save(word);
    //        }
    //
    //      });

//    courseService.findById(78L).ifPresent(course -> {
//      List<Lesson> lessons = new SpeaklanguagesScrapper().webScrapWords();
//
//      for (Lesson lesson : lessons) {
//        List<Word> words = lesson.getWords();
//        lesson.setWords(null);
//        lesson.setCourse(course);
//        lesson = lessonService.save(lesson);
//        for (Word word : words) {
//          word.setLesson(lesson);
//          word.setLevel("A1");
//          word.setUsName("");
//          word.setOtherNames("");
//          word = wordService.save(word);
//        }
//      }
//
//    });

  }

  public void setLessonForm(Lesson lesson) {
    textFieldEnName.setText(lesson.getEnName());
    textFieldPlName.setText(lesson.getPlName());
    textFieldId.setText(lesson.getId().toString());
    comboBoxCourse.getSelectionModel().select(lesson.getCourse());
  }

  private void initializeCourseComboBox() {
    List<Course> courses = courseService.findAllByIsCustomIsTrue();
    comboBoxCourse.setItems(FXCollections.observableArrayList(courses));
  }

  private void refreshTableView() {
    lessonTableController.fillInTableView();
  }

}
