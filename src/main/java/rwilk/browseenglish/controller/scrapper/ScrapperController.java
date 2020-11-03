package rwilk.browseenglish.controller.scrapper;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.controller.word.WordFormController;
import rwilk.browseenglish.model.Translation;
import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.release.controller.word.WordReleaseFormController;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Data
@Controller
public class ScrapperController implements Initializable {

  public TextField textFieldEnWord;
  public TextField textFieldUsWord;
  public TextArea textAreaOtherVersion;
  public ListView<String> listViewMeaning;
  public ListView<String> listViewSentence;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPlural;
  public TextField textFieldSynonym;
  @Getter
  private WordFormController wordFormController;
  @Getter
  private WordReleaseFormController wordReleaseFormController;
  private Translation translation;
  private boolean firstClick = true;
  @Getter
  @Setter
  private String plTranslation;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

    listViewMeaning.setCellFactory(lv -> new ListCell<String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        this.setText(item);
        if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(plTranslation) && (item.contains(plTranslation) || plTranslation.contains(item.substring(0, item.indexOf("(")).trim()))) {
          setStyle("-fx-background-color: #FFF200");
        } else {
          setStyle("");
        }
      }
    });
  }

  public void initialize(DikiWord dikiWord) {
    textFieldEnWord.setText(dikiWord.getEnName());
    textFieldUsWord.setText(dikiWord.getUsName());
    if (StringUtils.isNotEmpty(dikiWord.getOtherNames())) {
      textAreaOtherVersion.setText(dikiWord.getOtherNames());
    }
    textFieldPastTense.setText(dikiWord.getPastTense());
    textFieldPastParticiple.setText(dikiWord.getPastParticiple());
    textFieldComparative.setText(dikiWord.getComparative());
    textFieldSuperlative.setText(dikiWord.getSuperlative());
    textFieldPlural.setText(dikiWord.getPlural());

    listViewMeaning.setItems(FXCollections.observableArrayList(dikiWord.getDikiTranslations().stream().map(dikiTranslation ->
        dikiTranslation.getPlName() + " (" + dikiTranslation.getPartOfSpeech() + ") " + dikiTranslation.getGrammarType())
        .collect(Collectors.toList()))
    );

    listViewSentence.setItems(FXCollections.observableArrayList(
        dikiWord.getDikiTranslations().stream()
            .map(DikiTranslation::getDikiSentences)
            .flatMap(List::stream)
            .map(dikiSentence -> dikiSentence.getEnName() + "(" + dikiSentence.getPlName() + ")")
            .collect(Collectors.toList()))
    );
  }

  public void buttonLoadDataOnAction(ActionEvent actionEvent) {
    if (wordReleaseFormController != null) {
      setField(textFieldPastTense.getText(), wordReleaseFormController.textFieldPastTense);
      setField(textFieldPastParticiple.getText(), wordReleaseFormController.textFieldPastParticiple);
      setField(textFieldComparative.getText(), wordReleaseFormController.textFieldComparative);
      setField(textFieldSuperlative.getText(), wordReleaseFormController.textFieldSuperlative);
      setField(textFieldPlural.getText(), wordReleaseFormController.textFieldPlural);
      setField(textFieldSynonym.getText(), wordReleaseFormController.textFieldSynonym);
    }
  }

  public void listViewMeaningOnMouseClicked(MouseEvent mouseEvent) {
    String meaningItem = listViewMeaning.getSelectionModel().getSelectedItem();
    Optional.ofNullable(meaningItem).ifPresent(meaningText -> {
      String grammarType = meaningText.contains("[")
          ? meaningText.substring(meaningText.indexOf("[") + 1, meaningText.indexOf("]")).trim()
          : "";
      String partOfSpeech = meaningText.contains("(")
          ? meaningText.substring(meaningText.lastIndexOf("(") + 1, meaningText.lastIndexOf(")")).trim()
          : "";
      String meaning = meaningText.contains("(")
          ? meaningText.substring(0, meaningText.lastIndexOf("(")).trim()
          : meaningText;
      // FIXME some day
      // wordReleaseFormController.setGrammarType(grammarType);
      // wordReleaseFormController.setPartOfSpeech(partOfSpeech);
      if (!firstClick) {
        if (StringUtils.isNotEmpty(wordReleaseFormController.textFieldPlName.getText())) {
          wordReleaseFormController.textFieldPlName.setText(wordReleaseFormController.textFieldPlName.getText() + ", " + meaning);
        } else {
          wordReleaseFormController.textFieldPlName.setText(meaning);
        }
      }
      firstClick = false;
    });
  }

  private void setField(String text, TextField textField) {
    Optional.ofNullable(text).ifPresent(textField::setText);
  }
}
