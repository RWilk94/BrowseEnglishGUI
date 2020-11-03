package rwilk.browseenglish.controller.lesson;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import rwilk.browseenglish.service.CourseService;
import rwilk.browseenglish.service.LessonService;
import rwilk.browseenglish.service.WordService;

@Controller
public class LessonController implements Initializable {
  public AnchorPane anchorPaneLessonForm;
  public AnchorPane anchorPaneLessonTable;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(LessonService lessonService, CourseService courseService, WordService wordService) {
    try {
      FXMLLoader fxmlLoaderLessonForm = new FXMLLoader();
      fxmlLoaderLessonForm.setLocation(getClass().getResource("/scene/lesson/lesson_form.fxml"));
      VBox form = fxmlLoaderLessonForm.load();
      LessonFormController lessonFormController = fxmlLoaderLessonForm.getController();

      FXMLLoader fxmlLoaderLessonTable = new FXMLLoader();
      fxmlLoaderLessonTable.setLocation(getClass().getResource("/scene/lesson/lesson_table.fxml"));
      VBox table = fxmlLoaderLessonTable.load();
      LessonTableController lessonTableController = fxmlLoaderLessonTable.getController();

      lessonFormController.init(lessonService, courseService, lessonTableController, wordService);
      lessonTableController.init(lessonService, courseService, lessonFormController);

      anchorPaneLessonForm.getChildren().add(form);
      anchorPaneLessonTable.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
