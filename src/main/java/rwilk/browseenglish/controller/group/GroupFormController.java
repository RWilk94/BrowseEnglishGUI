package rwilk.browseenglish.controller.group;

import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.service.CourseService;
import rwilk.browseenglish.service.FormWordService;
import rwilk.browseenglish.service.LessonService;
import rwilk.browseenglish.service.WordService;

@SuppressWarnings("unused")
@Controller
public class GroupFormController implements Initializable {

  private final FormWordService formWordService;
  private final WordService wordService;
  private final LessonService lessonService;
  private final CourseService courseService;
  private HBox pinnedHBox;
  public ListView<Word> listViewWordsInLesson;
  public Accordion accordionCourses;
  public TextField textFieldSelectedLesson;
  public Button buttonPin;
  public Button buttonRemove;
  public Button buttonAdd;
  public Label labelCount;

  private Map<Button, Lesson> lessonButtonMap = new HashMap<>();
  private Map<Button, Node> pinnedButtonMap = new HashMap<>();
  private Lesson selectedLesson;

  public GroupFormController(FormWordService formWordService, WordService wordService,
      LessonService lessonService, CourseService courseService) {
    this.formWordService = formWordService;
    this.wordService = wordService;
    this.lessonService = lessonService;
    this.courseService = courseService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    createAccordionCoursesView();
  }

  public void buttonBlockOnAction(ActionEvent actionEvent) {
    if (selectedLesson != null) {
      selectedLesson.setReady(!selectedLesson.isReady());
      selectedLesson = lessonService.save(selectedLesson);
      buttonAdd.setDisable(selectedLesson.isReady());
      buttonRemove.setDisable(selectedLesson.isReady());
      fillListViewWordsInLesson();
      refreshButtonText();

      lessonButtonMap.forEach((button, lesson) -> {
        if (lesson.getId().compareTo(selectedLesson.getId()) == 0) {
          lesson.setReady(selectedLesson.isReady());
          if (lesson.isReady()) {
            button.getStyleClass().clear();
            button.getStyleClass().add("lessonIsReady");
          } else {
            button.getStyleClass().clear();
            button.getStyleClass().add("activeButton");
          }
        }
      });

    }
  }

  public void buttonRemoveOnAction(ActionEvent actionEvent) {
    Word selectedWord = listViewWordsInLesson.getSelectionModel().getSelectedItem();
    if (selectedWord != null) {
      Optional<Lesson> lessonOptional = lessonService.findById(1764L);
      lessonOptional.ifPresent(lesson -> {
        selectedWord.setLesson(lesson);
        selectedWord.setWordGrouped(false);
        Word word = wordService.save(selectedWord);
        formWordService.getWordTableController().refreshTable(word);
        formWordService.getWordTableController().getDuplicateTableController().updateWord(word);
        fillListViewWordsInLesson();
        // refreshButtonText();
      });
    }
  }

  public void buttonAddOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(formWordService.getWordFormController().textFieldId.getText())) {
      Optional<Word> wordOptional = wordService.findById(Long.valueOf(formWordService.getWordFormController().textFieldId.getText()));
      wordOptional.ifPresent(word -> {
        if (word.getLesson().getId().compareTo(selectedLesson.getId()) != 0) {
          word.setLesson(selectedLesson);
        }
        if (!word.isWordGrouped()) {
          word.setWordGrouped(true);
        }
        if (!word.isWordReady()) {
          word.setWordReady(true);
        }
        word = wordService.save(word);
        fillListViewWordsInLesson();
        // refreshButtonText();
        formWordService.getWordTableController().refreshTable(word);
        formWordService.getWordTableController().getDuplicateTableController().updateWord(word);
      });
    }
  }

  public void listViewWordsInLessonOnMouseClicked(MouseEvent mouseEvent) {
    Word selectedWord = listViewWordsInLesson.getSelectionModel().getSelectedItem();
    if (selectedWord != null) {
      formWordService.getWordFormController().setFormFields(selectedWord);
      // filter duplicateTable
      formWordService.getWordTableController().getDuplicateTableController().setPlTranslation(selectedWord.getPlName());
      formWordService.getWordTableController().getDuplicateTableController().textFieldFilterByName.setText(selectedWord.getEnName());
    }
  }

  public void refreshAccordionCoursesView() {
    createAccordionCoursesView();
  }

  public void buttonPinOnAction(ActionEvent actionEvent) {
    Button pinnedButton = new Button();

    lessonButtonMap.forEach((button, lesson) -> {
      if (selectedLesson != null && lesson.getId().compareTo(selectedLesson.getId()) == 0) {
        if (!pinnedHBox.getChildren().isEmpty() && pinnedHBox.getChildren().stream().map(Node::getId).filter(id -> id.equals(button.getId())).count() > 0) {
          List<Node> toRemove =
              this.pinnedHBox.getChildren().stream().filter(node -> node.getId().equals(button.getId())).collect(Collectors.toList());
          this.pinnedHBox.getChildren().removeAll(toRemove);
          buttonPin.setText("Pin");
        } else {
          pinnedButton.setText(button.getText());
          pinnedButton.setStyle("-fx-text-fill: #bbbbbb;");
          pinnedButton.setOnAction(button.getOnAction());
          pinnedButton.setId(button.getId());
          pinnedButton.getStyleClass().clear();
          pinnedButton.getStyleClass().add("inactiveButton");
          // pinnedButtonMap.put(pinnedButton, pinnedButton.getParent());
          this.pinnedHBox.getChildren().add(pinnedButton);
          buttonPin.setText("Unpin");
        }
      }
    });

//    if (StringUtils.isNotEmpty(pinnedButton.getText())) {
//      this.lessonButtonMap.put(pinnedButton, selectedLesson);
//    }

  }

  public void setPinnedHbox(HBox hBoxPinnedLesson) {
    this.pinnedHBox = hBoxPinnedLesson;
  }

  private void createAccordionCoursesView() {
    accordionCourses.getPanes().clear();

    List<Course> courses = courseService.findAllByIsCustomIsTrue();

    for (Course course : courses) {
      course.setLessons(lessonService.findAllByCourse(course));

      ScrollPane scrollPane = new ScrollPane();
      VBox vBox = new VBox();

      int wordsCount = 0;

      for (Lesson lesson : course.getLessons()) {
        HBox hBox = new HBox();
        Button button = new Button(lesson.getEnName() + " [" + lesson.getWords().size() + "]");
        wordsCount += lesson.getWords().size();
        button.setId(UUID.randomUUID().toString());
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().clear();
        button.getStyleClass().add("inactiveButton");
        if (lesson.isReady()) {
          button.getStyleClass().add("lessonIsReady");
        }
        HBox.setMargin(button, new Insets(2, 0, 2, 0));
        HBox.setHgrow(button, Priority.ALWAYS);

        hBox.getChildren().add(button);
        vBox.getChildren().add(hBox);
        scrollPane.setContent(vBox);

        button.setOnAction(event -> {
          List<Map.Entry<Button, Lesson>> collect =
              lessonButtonMap.entrySet().stream().filter(o -> o.getKey().getId().equals(((Button) event.getSource()).getId()))
                  .collect(Collectors.toList());
          selectedLesson = collect.get(0).getValue();

          // selectedLesson = lessonButtonMap.get(((Button) event.getSource()));

          textFieldSelectedLesson.setText(selectedLesson.toString() + " (" + selectedLesson.getCourse().toString() + ")");
          fillListViewWordsInLesson();
          setActiveButtonBackground((Button) event.getSource());

          buttonAdd.setDisable(selectedLesson.isReady());
          buttonRemove.setDisable(selectedLesson.isReady());

          if (pinnedHBox.getChildren().stream().map(Node::getId).anyMatch(s -> s.equals(collect.get(0).getKey().getId()))) {
            buttonPin.setText("Unpin");
          } else {
            buttonPin.setText("Pin");
          }

        });
        lessonButtonMap.put(button, lesson);
      }
      TitledPane titledPane = new TitledPane(course.getEnName() + " [" + course.getLessons().size() + "][" + wordsCount + "]", scrollPane);
      titledPane.setAnimated(false);
      accordionCourses.getPanes().add(titledPane);
    }
  }

  private void fillListViewWordsInLesson() {
    List<Word> words = wordService.findAll(selectedLesson).stream().sorted(Comparator.comparing(Word::getEnName)).collect(Collectors.toList());
    listViewWordsInLesson.setItems(null);
    listViewWordsInLesson.setItems(FXCollections.observableArrayList(words));
    labelCount.setText(String.valueOf(listViewWordsInLesson.getItems().size()));
  }

  private void refreshButtonText() {
    lessonButtonMap.forEach((button, lesson) -> {
      lessonService.findById(lesson.getId()).ifPresent(l -> {
        button.setText(l.getEnName() + " [" + l.getWords().size() + "]");
      });
    });
  }

  private void setActiveButtonBackground(Button activeButton) {
    lessonButtonMap.forEach((button, lesson) -> {
      // button.getStyleClass().clear();
      button.getStyleClass().remove("activeButton");
    });
    pinnedHBox.getChildren().forEach(button -> {
      button.getStyleClass().remove("activeButton");
    });
    activeButton.getStyleClass().add("activeButton");
  }
}
