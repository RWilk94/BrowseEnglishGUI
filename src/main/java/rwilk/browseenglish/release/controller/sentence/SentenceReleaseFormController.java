package rwilk.browseenglish.release.controller.sentence;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.release.controller.word.InjectFormService;
import rwilk.browseenglish.release.entity.SentenceRelease;
import rwilk.browseenglish.release.service.SentenceReleaseService;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class SentenceReleaseFormController implements Initializable {

  private InjectFormService injectFormService;
  private SentenceReleaseService sentenceReleaseService;

  public TextField textFieldId;
  public TextField textFieldSound;
  public TextField textFieldEnName;
  public TextField textFieldPlName;

  public SentenceReleaseFormController(InjectFormService injectFormService, SentenceReleaseService sentenceReleaseService) {
    this.injectFormService = injectFormService;
    this.sentenceReleaseService = sentenceReleaseService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    injectFormService.setSentenceReleaseFormController(this);
  }

  public void setForm(SentenceRelease sentence) {
    textFieldId.setText(String.valueOf(sentence.getId()));
    textFieldEnName.setText(sentence.getEnSentence().trim());
    textFieldPlName.setText(sentence.getPlSentence().trim());
    textFieldSound.setText(sentence.getSound());

    if (StringUtils.isEmpty(sentence.getSound())) {
      String sentenceName = sentence.getEnSentence().trim();
      List<String> soundNames = getSoundsFromLesson(sentence);
      if (!matchSound(sentenceName, soundNames)) {
        textFieldSound.clear();
      }
    }
  }

  public void buttonSaveWordOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      Optional<SentenceRelease> optionalSentence = sentenceReleaseService.findById(Long.valueOf(textFieldId.getText()));
      optionalSentence.ifPresent(sentence -> {

        sentence.setPlSentence(textFieldPlName.getText() != null ? textFieldPlName.getText().trim() : "");
        sentence.setEnSentence(textFieldEnName.getText() != null ? textFieldEnName.getText().trim() : "");
        sentence.setSound(textFieldSound.getText() != null ? textFieldSound.getText().trim() : "");

        sentence = sentenceReleaseService.save(sentence);
        injectFormService.getSentenceReleaseTableController().update(sentence);
      });
    }
  }

  public void buttonSaveAllWordOnAction(ActionEvent actionEvent) {
    List<SentenceRelease> sentenceReleases = sentenceReleaseService.findAll().stream()
        .filter(sentenceRelease -> StringUtils.isEmpty(sentenceRelease.getSound()))
        .collect(Collectors.toList());

    for (SentenceRelease sentenceRelease : sentenceReleases) {
      setForm(sentenceRelease);
      buttonSaveWordOnAction(null);
    }

  }

  public void buttonMatchSoundOnAction(ActionEvent actionEvent) {
    String sentenceName = textFieldEnName.getText();
    List<String> soundNames = getSoundsFromLesson(sentenceReleaseService.findById(Long.valueOf(textFieldId.getText())).get());

    if (!matchSound(sentenceName, soundNames)) {
      textFieldSound.clear();
    }
  }

  public void buttonSoundOnAction(ActionEvent actionEvent) {
    textFieldSound.clear();

    String sentenceName = textFieldEnName.getText();
    SentenceRelease sentence = sentenceReleaseService.findById(Long.valueOf(textFieldId.getText())).get();
    String audioFolder = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\";

    String courseFolderName = sentence.getWord().getLesson().getCourse().getEnName();
    String lessonFolderName = sentence.getWord().getLesson().getEnName()
        .replace("...", "")
        .replace("?", "")
        .replace(":", "")
        .replaceAll("\"", "")
        .replaceAll("/", "")
        .replaceAll("\\\\", "")
        .replace("!", "").trim();

    File directory = new File(audioFolder + "\\" + courseFolderName + "\\" + lessonFolderName);

    FileChooser fileChooser = new FileChooser();
    fileChooser.setInitialDirectory(directory);
    fileChooser.setTitle(sentenceName);
    File soundFile = fileChooser.showOpenDialog(textFieldId.getScene().getWindow());

    if (soundFile != null) {
      textFieldSound.setText(textFieldSound.getText() + "[" + sentenceName + "="
          + soundFile.getName().replaceAll("en_", "").replaceAll("us_", "")
          + "];");
    }
  }

  private List<String> getSoundsFromLesson(SentenceRelease sentence) {
    String audioFolder = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\";

    String courseFolderName = sentence.getWord().getLesson().getCourse().getEnName();
    String lessonFolderName = sentence.getWord().getLesson().getEnName()
        .replace("...", "")
        .replace("?", "")
        .replace(":", "")
        .replaceAll("\"", "")
        .replaceAll("/", "")
        .replaceAll("\\\\", "")
        .replace("!", "").trim();

    ArrayList<File> files = new ArrayList<>(Arrays.asList(new File(audioFolder + "\\" + courseFolderName + "\\" + lessonFolderName)));
    File file = files.get(0);

    ArrayList<File> fileList = new ArrayList<>(Arrays.asList(file.listFiles()));
    List<String> names = fileList.stream()
        .map(File::getName)
        .map(name -> name.substring(3))
        .distinct()
        .collect(Collectors.toList());

    return names;
  }

  private boolean matchSound(String sentenceName, List<String> soundNames) {

    String expectedSoundName = sentenceName
        .replaceAll("informal", "")
        .replaceAll(Pattern.quote("(British English)"), "")
        .replaceAll(Pattern.quote("(American English)"), "")
        .replaceAll("formal", "").trim()
        .replaceAll(Pattern.quote("%"), "PERCENT")
        .replaceAll(Pattern.quote("I'd"), "id")
        .replaceAll(" ", "_")
        .replaceAll("'", "")
        .replaceAll("\\.", "")
        .replaceAll(",", "")
        .replaceAll(":", "")
        .replaceAll(":", "")
        .replaceAll("!", "")
        .replaceAll("\\?", "")
        .replaceAll("\"", "")
        .replaceAll(Pattern.quote("$"), "S")
        .replaceAll(Pattern.quote("£"), "POUND")
        .replaceAll(Pattern.quote("é"), "_u00e9_")
        .replaceAll(Pattern.quote("°"), "DEG")

        .toLowerCase() + ".mp3";

    List<String> matchedSounds = soundNames.stream()
        .filter(sound -> sound.equalsIgnoreCase(expectedSoundName))
        .collect(Collectors.toList());

    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + sentenceName + "=" + matchedSounds.get(0) + "];");
      return true;
    }
    return false;
  }

}
