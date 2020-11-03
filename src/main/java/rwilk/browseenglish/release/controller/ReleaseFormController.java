package rwilk.browseenglish.release.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.model.etutor.EtutorGrammar;
import rwilk.browseenglish.release.controller.item.ChoiceItem;
import rwilk.browseenglish.release.controller.item.DialogueItem;
import rwilk.browseenglish.release.controller.item.NoteItem;
import rwilk.browseenglish.release.controller.item.WordItem;
import rwilk.browseenglish.release.entity.ExerciseRelease;
import rwilk.browseenglish.release.entity.ExerciseRowRelease;
import rwilk.browseenglish.release.entity.LessonRelease;
import rwilk.browseenglish.release.entity.NoteRelease;
import rwilk.browseenglish.release.entity.SentenceRelease;
import rwilk.browseenglish.release.entity.WordRelease;
import rwilk.browseenglish.release.repository.ExerciseReleaseRepository;
import rwilk.browseenglish.release.repository.ExerciseRowReleaseRepository;
import rwilk.browseenglish.release.repository.NoteReleaseRepository;
import rwilk.browseenglish.release.repository.SentenceReleaseRepository;
import rwilk.browseenglish.release.repository.WordReleaseRepository;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Slf4j
@Controller
public class ReleaseFormController implements Initializable {

  private Map<Node, EtutorGrammar> objectMap;

  public VBox vBoxContainer;
  private ReleaseContextController releaseContextController;
  public TextField textFieldEtutorGrammarLessonId;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(ReleaseContextController releaseContextController) {
    this.releaseContextController = releaseContextController;
  }

  public void buttonAutoLoadEtutorGrammarLesson(ActionEvent actionEvent) throws IOException {
    buttonLoadEtutorGrammarLesson(null);
    while (true) {
      buttonSaveEtutorGrammarReleaseLesson(null);
    }
  }

  public void buttonLoadEtutorGrammarLesson(ActionEvent actionEvent) throws IOException {
    if (!textFieldEtutorGrammarLessonId.getText().isEmpty()) {
      log.info("START PROCESSING LESSON {}", textFieldEtutorGrammarLessonId.getText());
      vBoxContainer.getChildren().clear();

      List<EtutorGrammar> etutorGrammars = releaseContextController.getEtutorGrammarService()
          .findAllByLessonId(Long.valueOf(textFieldEtutorGrammarLessonId.getText()));

      objectMap = new HashMap<>();

      for (EtutorGrammar etutorGrammar : etutorGrammars) {

        if (etutorGrammar.getGrammarType().equals("NOTE")) {

          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/scene/release/items/note_item.fxml"));
          VBox vBox = fxmlLoader.load();
          NoteItem noteItem = fxmlLoader.getController();

          noteItem.init(etutorGrammar, vBox, vBoxContainer);
          objectMap.put(vBox, etutorGrammar);

          vBoxContainer.getChildren().add(vBox);

        } else if (etutorGrammar.getGrammarType().equals("WORD")) {

          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/scene/release/items/word_item.fxml"));
          VBox vBox = fxmlLoader.load();
          WordItem wordItem = fxmlLoader.getController();

          wordItem.init(etutorGrammar, releaseContextController.getWordService(), vBox, vBoxContainer);
          objectMap.put(vBox, etutorGrammar);

          vBoxContainer.getChildren().add(vBox);
        } else if (etutorGrammar.getGrammarType().equals("CHOICE") && StringUtils.isNotEmpty(etutorGrammar.getQuestion())) {

          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/scene/release/items/choice_item.fxml"));
          VBox vBox = fxmlLoader.load();
          ChoiceItem choiceItem = fxmlLoader.getController();

          choiceItem.init(etutorGrammar, vBox, vBoxContainer);
          objectMap.put(vBox, etutorGrammar);

          vBoxContainer.getChildren().add(vBox);


        } else if (etutorGrammar.getGrammarType().equals("DIALOGUE")) {

          FXMLLoader fxmlLoader = new FXMLLoader();
          fxmlLoader.setLocation(getClass().getResource("/scene/release/items/dialogue_item.fxml"));
          VBox vBox = fxmlLoader.load();
          DialogueItem dialogueItem = fxmlLoader.getController();

          dialogueItem.init(etutorGrammar, vBox, vBoxContainer);
          objectMap.put(vBox, etutorGrammar);

          vBoxContainer.getChildren().add(vBox);

        }
      }
    }

  }

  public void buttonSaveEtutorGrammarReleaseLesson(ActionEvent actionEvent) throws IOException {

//    List<EtutorCourse> courses = releaseContextController.getEtutorCourseRepository().findAll();
//    CourseReleaseRepository courseReleaseRepository = releaseContextController.getCourseReleaseRepository();
//
//    for (EtutorCourse course : courses) {
//
//      CourseRelease courseRelease = CourseRelease.builder()
//          .enName(course.getName().substring(1))
//          .build();
//
//      courseReleaseRepository.save(courseRelease);
//
//    }

//    List<EtutorLesson> lessons = releaseContextController.getEtutorLessonRepository().findAll();
//    LessonReleaseRepository lessonReleaseRepository = releaseContextController.getLessonReleaseRepository();
//
//    for (EtutorLesson lesson : lessons) {
//
//      LessonRelease lessonRelease = LessonRelease.builder()
//          .course(courseReleaseRepository.findById(lesson.getCourse().getId()).get())
//          .enName(lesson.getLessonName())
//          .plName(lesson.getLessonName())
//          .href(lesson.getLessonHref())
//          .build();
//
//      lessonReleaseRepository.save(lessonRelease);
//
//    }

    NoteReleaseRepository noteReleaseRepository = releaseContextController.getNoteReleaseRepository();
    WordReleaseRepository wordReleaseRepository = releaseContextController.getWordReleaseRepository();
    ExerciseReleaseRepository exerciseReleaseRepository = releaseContextController.getExerciseReleaseRepository();
    ExerciseRowReleaseRepository exerciseRowReleaseRepository = releaseContextController.getExerciseRowReleaseRepository();
    SentenceReleaseRepository sentenceReleaseRepository = releaseContextController.getSentenceReleaseRepository();

    LessonRelease lessonRelease = releaseContextController.getLessonReleaseRepository()
        .findById(Long.valueOf(textFieldEtutorGrammarLessonId.getText()))
        .get();

    List<EtutorGrammar> etutorGrammars = new ArrayList<>();

    for (Node node : vBoxContainer.getChildren()) {
      etutorGrammars.add(objectMap.get(node));
    }

    ExerciseRelease exerciseRelease = null;

    for (EtutorGrammar etutorGrammar : etutorGrammars) {
      // log.info("START PROCESSING {}", etutorGrammar.toString());

      if (etutorGrammar.getGrammarType().equals("NOTE")) {
        exerciseRelease = null;

        NoteRelease noteRelease = NoteRelease.builder()
            .lesson(lessonRelease)
            .note(etutorGrammar.getNote())
            .position((long) etutorGrammars.indexOf(etutorGrammar))
            .build();

        // noteReleaseRepository.save(noteRelease);

      } else if (etutorGrammar.getGrammarType().equals("WORD")) {
        exerciseRelease = null;

        // TODO build word mapper later

        String enName = etutorGrammar.getWordListLeft();
        String plName = etutorGrammar.getWordListRight();
        List<SentenceRelease> sentenceReleases = new ArrayList<>();
        if (enName.contains("[") && plName.contains("[")) {
          String sentenceEn = enName.substring(enName.indexOf("[")).replaceAll(Pattern.quote("(]"), "");
          String sentencePl = plName.substring(plName.indexOf("[")).replaceAll(Pattern.quote("(]"), "");

          String[] splitEn = sentenceEn.split(Pattern.quote("["));
          String[] splitPl = sentencePl.split(Pattern.quote("["));

          List<String> sentencesEn = extractSentences(splitEn);
          List<String> sentencesPl = extractSentences(splitPl);

          enName = enName.substring(0, enName.indexOf("[")).trim();
          plName = plName.substring(0, plName.indexOf("[")).trim();

          for (int i = 0; i < sentencesEn.size(); i++) {
            sentenceReleases.add(
                SentenceRelease.builder()
                    .enSentence(sentencesEn.get(i))
                    .plSentence(sentencesPl.get(i))
                    .build()
            );
          }
        }


        WordRelease wordRelease = WordRelease.builder()
            .lesson(lessonRelease)
            .enName(enName)
            .plName(plName)
            .position((long) etutorGrammars.indexOf(etutorGrammar))
            .build();

        // wordRelease = wordReleaseRepository.save(wordRelease);
        for (SentenceRelease sentenceRelease : sentenceReleases) {
          sentenceRelease.setWord(wordRelease);
          // sentenceReleaseRepository.save(sentenceRelease);
        }

      } else if (etutorGrammar.getGrammarType().equals("CHOICE")) {

        if (exerciseRelease == null || exerciseRelease.getType().equals("DIALOGUE")) {
          exerciseRelease = ExerciseRelease.builder()
              .lesson(lessonRelease)
              .type("CHOICE")
              .name(etutorGrammar.getName())
              .position((long) etutorGrammars.indexOf(etutorGrammar))
              .build();

          exerciseRelease = exerciseReleaseRepository.save(exerciseRelease);
        }

        ExerciseRowRelease exerciseRowRelease = ExerciseRowRelease.builder()
            .exercise(exerciseRelease)
            .question(etutorGrammar.getQuestion())
            .correctAnswer(etutorGrammar.getCorrectAnswer())
            .correctAnswerAfterChoice(etutorGrammar.getCorrectAnswerAfterChoice())
            .firstPossibleAnswer(etutorGrammar.getFirstPossibleAnswer())
            .secondPossibleAnswer(etutorGrammar.getSecondPossibleAnswer())
            .thirdPossibleAnswer(etutorGrammar.getThirdPossibleAnswer())
            .forthPossibleAnswer(etutorGrammar.getForthPossibleAnswer())
            .description(etutorGrammar.getTranslation())
            .build();

        exerciseRowReleaseRepository.save(exerciseRowRelease);

      } else if (etutorGrammar.getGrammarType().equals("DIALOGUE")) {

        if (exerciseRelease == null || exerciseRelease.getType().equals("CHOICE")) {
          exerciseRelease = ExerciseRelease.builder()
              .lesson(lessonRelease)
              .type("DIALOGUE")
              .position((long) etutorGrammars.indexOf(etutorGrammar))
              .build();

          exerciseRelease = exerciseReleaseRepository.save(exerciseRelease);
        }

        ExerciseRowRelease exerciseRowRelease = ExerciseRowRelease.builder()
            .exercise(exerciseRelease)
            .dialogueLeft(etutorGrammar.getDialogueLeft())
            .dialogueRight(etutorGrammar.getDialogueRight())
            .build();

        exerciseRowReleaseRepository.save(exerciseRowRelease);
      }
    }

    lessonRelease.setIsReady(false);
    releaseContextController.getLessonReleaseRepository().save(lessonRelease);
    releaseContextController.getReleaseTableController().fillInTableView();
    vBoxContainer.getChildren().clear();

    // load next lesson
    Long id = Long.parseLong(textFieldEtutorGrammarLessonId.getText()) + 1L;
    textFieldEtutorGrammarLessonId.setText(id.toString());
    buttonLoadEtutorGrammarLesson(null);
  }

  private List<String> extractSentences(String[] split) {
    List<String> sentences = new ArrayList<>();
    for (String s : split) {
      String text = s.replace("]", "").trim();
      if (StringUtils.isNotEmpty(text)) {
        sentences.add(text);
      }
    }
    return sentences;
  }

}
