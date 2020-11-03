package rwilk.browseenglish.controller.course;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.service.CourseService;

@Controller
public class CourseTableController implements Initializable {

  private CourseService courseService;
  private CourseFormController courseFormController;

  public TextField textFieldSearch;
  public TableView<Course> tableCourses;
  public TableColumn<Course, Long> columnId;
  public TableColumn<Course, String> columnEnName;
  public TableColumn<Course, String> columnPlName;
  public TableColumn<Course, String> columnLevel;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    // TODO add filtering
  }

  public void init(CourseService courseService, CourseFormController courseFormController) {
    this.courseService = courseService;
    this.courseFormController = courseFormController;
    fillInTableView();
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.05));
    columnEnName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.425));
    columnPlName.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.425));
    columnLevel.prefWidthProperty().bind(tableCourses.widthProperty().multiply(0.1));
  }

  public void fillInTableView() {
    tableCourses.setItems(FXCollections.observableArrayList(courseService.findAllByIsCustomIsTrue()));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableCourses.getSelectionModel().isEmpty()) {
      Course selectedCourse = tableCourses.getSelectionModel().getSelectedItem();
      courseFormController.setCourseForm(selectedCourse);
    }
  }

}
