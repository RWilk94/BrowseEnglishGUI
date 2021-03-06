package rwilk.browseenglish.release.controller.item;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Data;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.model.etutor.EtutorGrammar;

import java.net.URL;
import java.util.ResourceBundle;

@Data
@Controller
public class DialogueItem implements Initializable {

  private EtutorGrammar etutorGrammar;
  private VBox vBox;
  private VBox vBoxContainer;

  private boolean saved = false;
  public TextField textFieldDialogueLeft;
  public TextField textFieldDialogueRight;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(EtutorGrammar etutorGrammar, VBox vBox, VBox vBoxContainer) {
    this.vBox = vBox;
    this.vBoxContainer = vBoxContainer;
//    this.etutorGrammar = etutorGrammar;
    textFieldDialogueLeft.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setDialogueLeft(newValue));
    textFieldDialogueLeft.setText(etutorGrammar.getDialogueLeft());
    textFieldDialogueRight.textProperty().addListener((observable, oldValue, newValue) -> etutorGrammar.setDialogueRight(newValue));
    textFieldDialogueRight.setText(etutorGrammar.getDialogueRight());
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
