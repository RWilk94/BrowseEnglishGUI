package rwilk.browseenglish.release.controller.item;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.model.etutor.EtutorGrammar;

import java.net.URL;
import java.util.ResourceBundle;

@Data
@Controller
public class ChoiceItem implements Initializable {

  private static final String SEPARATOR = "[...] ";
  private EtutorGrammar etutorGrammar;
  private boolean saved = false;
  private VBox vBox;
  private VBox vBoxContainer;

  public TextField textFieldQuestion;
  public TextField textFieldCorrectAnswer;
  public TextField textFieldCorrectAnswerAfterChoice;
  public TextField textFieldFirstAnswer;
  public TextField textFieldSecondAnswer;
  public TextField textFieldThirdAnswer;
  public TextField textFieldFourthAnswer;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(EtutorGrammar etutorGrammar, VBox vBox, VBox vBoxContainer) {
    this.vBox = vBox;
    this.vBoxContainer = vBoxContainer;
    // this.etutorGrammar = etutorGrammar;

    String question = etutorGrammar.getQuestion();
    String correctAnswerAfter = etutorGrammar.getCorrectAnswerAfterChoice();

    // "Ile kosztuje znaczek na pocztówkę? - How much is a for a postcard?";
    // "Ile kosztuje znaczek na pocztówkę? - How much is a stamp for a postcard?";

    if (!etutorGrammar.getCorrectAnswer().equals(etutorGrammar.getCorrectAnswerAfterChoice()) && StringUtils.isNotEmpty(etutorGrammar.getCorrectAnswerAfterChoice())) {

      for (int i = 0; i < correctAnswerAfter.length() - 1; i++) {
        if (i + 1 == question.length()) {
          question = (question + " " + SEPARATOR).trim();
          break;
        }
        if (!question.substring(i, i + 1).equals(correctAnswerAfter.substring(i, i + 1))) {
          question = question.substring(0, i) + SEPARATOR + question.substring(i);
          break;
        }
      }

    }

    textFieldQuestion.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setQuestion(newValue));
    textFieldQuestion.setText(question);
    textFieldCorrectAnswer.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setCorrectAnswer(newValue));
    textFieldCorrectAnswer.setText(etutorGrammar.getCorrectAnswer());
    textFieldCorrectAnswerAfterChoice.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setCorrectAnswerAfterChoice(newValue));
    textFieldCorrectAnswerAfterChoice.setText(etutorGrammar.getCorrectAnswerAfterChoice());
    textFieldFirstAnswer.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setFirstPossibleAnswer(newValue));
    textFieldFirstAnswer.setText(etutorGrammar.getFirstPossibleAnswer());
    textFieldSecondAnswer.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setSecondPossibleAnswer(newValue));
    textFieldSecondAnswer.setText(etutorGrammar.getSecondPossibleAnswer());
    textFieldThirdAnswer.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setThirdPossibleAnswer(newValue));
    textFieldThirdAnswer.setText(etutorGrammar.getThirdPossibleAnswer());
    textFieldFourthAnswer.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setForthPossibleAnswer(newValue));
    textFieldFourthAnswer.setText(etutorGrammar.getForthPossibleAnswer());

  }

  public void buttonUpOnAction(ActionEvent actionEvent) {
    int index = vBoxContainer.getChildren().indexOf(vBox);
    if (index > 0) {
      vBoxContainer.getChildren().remove(vBox);
      vBoxContainer.getChildren().add(index - 1, vBox);
    }
  }

  public void buttonDownOnAction(ActionEvent actionEvent) {
    int index = vBoxContainer.getChildren().indexOf(vBox);
    if (index < vBoxContainer.getChildren().size() - 1) {
      vBoxContainer.getChildren().remove(vBox);
      vBoxContainer.getChildren().add(index + 1, vBox);
    }
  }

  public void buttonRemoveOnAction(ActionEvent actionEvent) {
    vBoxContainer.getChildren().remove(vBox);
  }
}
