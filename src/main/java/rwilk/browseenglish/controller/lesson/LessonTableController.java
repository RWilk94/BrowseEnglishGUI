package rwilk.browseenglish.controller.lesson;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.service.CourseService;
import rwilk.browseenglish.service.LessonService;

@Controller
public class LessonTableController implements Initializable {

  private LessonService lessonService;
  private CourseService courseService;
  private LessonFormController lessonFormController;
  private List<Lesson> lessons;
  public TextField textFieldSearch;
  public TableView<Lesson> tableLessons;
  public TableColumn<Lesson, Long> columnId;
  public TableColumn<Lesson, String> columnEnName;
  public TableColumn<Lesson, String> columnPlName;
  public TableColumn<Lesson, String> columnCourse;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeTableView();

    // TODO add table filtering
  }

  public void init(LessonService lessonService, CourseService courseService,
      LessonFormController lessonFormController) {
    this.lessonService = lessonService;
    this.courseService = courseService;
    this.lessonFormController = lessonFormController;
    fillInTableView();
  }

  public void fillInTableView() {
    lessons = lessonService.findAll();
    tableLessons.setItems(FXCollections.observableArrayList(lessons));
  }

  private void initializeTableView() {
    columnId.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.05));
    columnEnName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnPlName.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.30));
    columnCourse.prefWidthProperty().bind(tableLessons.widthProperty().multiply(0.35));
  }

  public void tableViewOnMouseClicked(MouseEvent mouseEvent) {
    if (!tableLessons.getSelectionModel().isEmpty()) {
      Lesson selectedLesson = tableLessons.getSelectionModel().getSelectedItem();
      lessonFormController.setLessonForm(selectedLesson);
    }
  }

}
