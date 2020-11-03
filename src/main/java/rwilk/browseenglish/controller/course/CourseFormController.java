package rwilk.browseenglish.controller.course;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.service.CourseService;

@Slf4j
@Controller
public class CourseFormController implements Initializable {

  private CourseService courseService;
  private CourseTableController courseTableController;
  public TextField textFieldId;
  public TextField textFieldEnName;
  public TextField textFieldPlName;
  public ComboBox<Integer> comboBoxLevel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeLevelComboBox();
  }

  public void init(CourseService courseService, CourseTableController courseTableController) {
    this.courseService = courseService;
    this.courseTableController = courseTableController;
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldId.clear();
    textFieldEnName.clear();
    textFieldPlName.clear();
    comboBoxLevel.getSelectionModel().select(null);
  }

  public void buttonDeleteOnAction(ActionEvent actionEvent) {
    if (!textFieldId.getText().isEmpty()) {
      courseService.findById(Long.valueOf(textFieldId.getText())).ifPresent(course -> courseService.delete(course));
      buttonClearOnAction(actionEvent);
      refreshTableView();
    }
  }

  public void buttonEditOnAction(ActionEvent actionEvent) {
    if (!textFieldId.getText().isEmpty() && !textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty() && !comboBoxLevel
        .getSelectionModel().isEmpty()) {
      Optional<Course> courseOptional = courseService.findById(Long.valueOf(textFieldId.getText()));
      courseOptional.ifPresent(course -> {
        course.setEnName(textFieldEnName.getText().trim());
        course.setPlName(textFieldPlName.getText().trim());
        course.setLevel(comboBoxLevel.getSelectionModel().getSelectedItem().toString());
        setCourseForm(courseService.save(course));
        refreshTableView();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (!textFieldEnName.getText().isEmpty() && !textFieldPlName.getText().isEmpty() && !comboBoxLevel.getSelectionModel().isEmpty()) {
      Course course = Course.builder()
          .enName(textFieldEnName.getText().trim())
          .plName(textFieldPlName.getText().trim())
          .isCustom(true)
          .level(comboBoxLevel.getSelectionModel().getSelectedItem().toString())
          .build();
      course = courseService.save(course);
      setCourseForm(course);
      refreshTableView();
    }
  }

  public void setCourseForm(Course course) {
    textFieldEnName.setText(course.getEnName());
    textFieldPlName.setText(course.getPlName());
    textFieldId.setText(course.getId().toString());
    comboBoxLevel.getSelectionModel().select(Integer.valueOf(course.getLevel()));
  }

  private void initializeLevelComboBox() {
    comboBoxLevel.setItems(FXCollections.observableArrayList(Arrays.asList(1, 2, 3, 4, 5)));
  }

  private void refreshTableView() {
    courseTableController.fillInTableView();
  }

}
