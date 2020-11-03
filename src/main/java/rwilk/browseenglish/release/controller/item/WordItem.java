package rwilk.browseenglish.release.controller.item;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Data;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.model.etutor.EtutorGrammar;
import rwilk.browseenglish.service.WordService;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

@Data
@Controller
public class WordItem implements Initializable {

  private EtutorGrammar etutorGrammar;
  private boolean saved = false;
  private VBox vBox;
  private VBox vBoxContainer;

  private WordService wordService;
  public TextField textFieldWordId;
  public TextField textFieldEnName;
  public TextField textFieldUsName;
  public TextField textFieldOtherNames;
  public TextField textFieldPlName;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(EtutorGrammar etutorGrammar, WordService wordService, VBox vBox, VBox vBoxContainer) {
//    this.etutorGrammar = etutorGrammar;
    this.wordService = wordService;
    this.vBox = vBox;
    this.vBoxContainer = vBoxContainer;

    textFieldEnName.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setWordListLeft(newValue));
    textFieldEnName.setText(etutorGrammar.getWordListLeft().replaceAll(Pattern.quote("(]"), ""));
    textFieldPlName.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setWordListRight(newValue));
    textFieldPlName.setText(etutorGrammar.getWordListRight().replaceAll(Pattern.quote("(]"), ""));
  }

  public void loadWord(ActionEvent actionEvent) {
    if (!textFieldWordId.getText().isEmpty()) {
      wordService.findById(Long.valueOf(textFieldWordId.getText())).ifPresent(wordRelease -> {
        textFieldEnName.setText(wordRelease.getEnName());
        textFieldPlName.setText(wordRelease.getPlName());
        textFieldUsName.setText(wordRelease.getUsName());
        textFieldOtherNames.setText(wordRelease.getOtherNames());
      });
    }
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
