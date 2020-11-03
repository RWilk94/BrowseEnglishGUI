package rwilk.browseenglish.controller.course;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import rwilk.browseenglish.service.CourseService;

@Slf4j
@Controller
public class CourseController implements Initializable {

  public AnchorPane anchorPaneCourseForm;
  public AnchorPane anchorPaneCourseTable;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(CourseService courseService) {
    try {
      FXMLLoader fxmlLoaderCourseForm = new FXMLLoader();
      fxmlLoaderCourseForm.setLocation(getClass().getResource("/scene/course/course_form.fxml"));
      VBox form = fxmlLoaderCourseForm.load();
      CourseFormController courseFormController = fxmlLoaderCourseForm.getController();

      FXMLLoader fxmlLoaderCourseTable = new FXMLLoader();
      fxmlLoaderCourseTable.setLocation(getClass().getResource("/scene/course/course_table.fxml"));
      VBox table = fxmlLoaderCourseTable.load();
      CourseTableController courseTableController = fxmlLoaderCourseTable.getController();

      courseFormController.init(courseService, courseTableController);
      courseTableController.init(courseService, courseFormController);

      anchorPaneCourseForm.getChildren().add(form);
      anchorPaneCourseTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
