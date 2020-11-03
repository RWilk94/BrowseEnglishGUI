package rwilk.browseenglish.release.controller.word;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.controller.scrapper.ScrapperController;
import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.release.entity.WordRelease;
import rwilk.browseenglish.release.service.WordReleaseService;
import rwilk.browseenglish.scrapper.diki.BabScrapper;
import rwilk.browseenglish.scrapper.diki.DikiScrapper;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class WordReleaseFormController implements Initializable {

  private InjectFormService injectFormService;
  private WordReleaseService wordReleaseService;

  public TextField textFieldId;
  public TextField textFieldSound;
  public TextField textFieldEnName;
  public TextField textFieldUsName;
  public TextField textFieldPlName;
  public TextField textFieldOtherNames;
  public ToggleGroup toggleGroupPartOfSpeech;
  public ToggleButton toggleButtonNoun;
  public ToggleButton toggleButtonVerb;
  public ToggleButton toggleButtonAdjective;
  public ToggleButton toggleButtonAdverb;
  public ToggleButton toggleButtonPhrasalVerb;
  public ToggleButton toggleButtonSentence;
  public ToggleButton toggleButtonIdiom;
  public ToggleButton toggleButtonOther;
  private ToggleGroup toggleGroupGrammarType;
  public ToggleButton toggleButtonCountable;
  public ToggleButton toggleButtonUncountable;
  public ToggleButton toggleButtonCountableUncountable;
  public ToggleButton toggleButtonSingular;
  public ToggleButton toggleButtonPlural;
  public ToggleButton toggleButtonTransitive;
  public ToggleButton toggleButtonIntransitive;
  private ToggleGroup toggleGroupArticle;
  public ToggleButton toggleButtonEmpty;
  public ToggleButton toggleButtonA;
  public ToggleButton toggleButtonAn;
  public ToggleButton toggleButtonNone;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldPlural;
  public TextField textFieldSynonym;

  public WordReleaseFormController(InjectFormService injectFormService, WordReleaseService wordReleaseService) {
    this.injectFormService = injectFormService;
    this.wordReleaseService = wordReleaseService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    injectFormService.setWordReleaseFormController(this);
    setToggleGroups();
  }

  public void setForm(WordRelease word) {
    textFieldId.setText(String.valueOf(word.getId()));
    textFieldEnName.setText(word.getEnName() != null
        ? word.getEnName()
        .replaceAll("British English", "(British English)")
        .replaceAll("American English", "(American English)")
        .replaceAll(Pattern.quote("(("), "(")
        .replaceAll(Pattern.quote("))"), ")")
        : "");
    textFieldUsName.setText(word.getUsName() != null
        ? word.getUsName()
        .replaceAll("British English", "(British English)")
        .replaceAll("American English", "(American English)")
        .replaceAll(Pattern.quote("(("), "(")
        .replaceAll(Pattern.quote("))"), ")")
        : "");
    textFieldPlName.setText(word.getPlName());
    textFieldOtherNames.setText(word.getOtherNames());
    // TODO set partOfSpeech, grammar and article
    textFieldComparative.setText(word.getComparative());
    textFieldSuperlative.setText(word.getSuperlative());
    textFieldPastTense.setText(word.getPastTense());
    textFieldPastParticiple.setText(word.getPastParticiple());
    textFieldPlural.setText(word.getPlural());
    textFieldSynonym.setText(word.getSynonym());
    textFieldSound.setText(word.getSound());

    String enText = textFieldEnName.getText();
    if (enText.contains("plural:")) {
      String plural = enText.substring(enText.indexOf(":") + 1);
      enText = enText.substring(0, enText.indexOf("plural:"));

      textFieldPlural.setText(
          (textFieldPlural.getText() != null ? textFieldPlural.getText() : "") + plural
      );
      textFieldEnName.setText(enText);
    }
    if (enText.contains("stopień wyższy")) {
      if (enText.indexOf("stopień wyższy") > enText.indexOf(",")) {
        enText = enText.replaceFirst(",", "");
      }
      String comparative = enText.substring(enText.indexOf("stopień wyższy"), enText.indexOf(","))
          .replaceAll("stopień wyższy", "").trim();
      String superlative = enText.substring(enText.indexOf("stopień najwyższy"))
          .replaceAll("stopień najwyższy", "").trim();
      enText = enText.substring(0, enText.indexOf("stopień")).trim();
      textFieldComparative.setText(comparative);
      textFieldSuperlative.setText(superlative);
      textFieldEnName.setText(enText);
    }
    if (enText.contains("past tense")) {
      if (enText.indexOf("past tense") > enText.indexOf(",")) {
        enText = enText.replaceFirst(",", "");
      }
      String pastTense = enText.substring(enText.indexOf("past tense"), enText.indexOf(","))
          .replaceAll("past tense", "").trim();
      String pastParticiple = enText.substring(enText.indexOf("past participle"))
          .replaceAll("past participle", "").trim();
      enText = enText.substring(0, enText.indexOf("past tense")).trim();
      textFieldPastTense.setText(pastTense);
      textFieldPastParticiple.setText(pastParticiple);
      textFieldEnName.setText(enText);
    }
    replaceText(textFieldEnName);
    replaceText(textFieldUsName);
    replaceText(textFieldOtherNames);
//    if (enText.length() > 0 && Character.isUpperCase(enText.substring(0, 1).toCharArray()[0])
//        && enText.contains(", ") && !enText.contains(".") && !enText.contains("?") && !enText.contains("!")) {
//      textFieldEnName.clear();
//      String[] split = enText.split(", ");
//      for (String s : split) {
//        if (StringUtils.isEmpty(textFieldEnName.getText())) {
//          textFieldEnName.setText(s.trim());
//        } else if (StringUtils.isEmpty(textFieldUsName.getText()) && s.contains("American English")) {
//          textFieldUsName.setText(s.trim());
//        } else {
//          String otherText = StringUtils.isNotEmpty(textFieldOtherNames.getText()) ? textFieldOtherNames.getText() : "";
//          if (StringUtils.isNotEmpty(otherText)) {
//            otherText = otherText.concat("; ").concat(s);
//          } else {
//            otherText = otherText.concat(s);
//          }
//          textFieldOtherNames.setText(otherText.trim());
//        }
//      }
//    } else if (enText.contains(". ") && !enText.contains("?")) {
//      textFieldEnName.clear();
//      String[] split = enText.split(Pattern.quote(". "));
//      textFieldEnName.setText(split[0].trim());
//      textFieldOtherNames.setText(split[1].trim());
//    }
//    if (enText.endsWith(",")) {
//      enText = enText.substring(0, enText.length() - 1);
//      textFieldEnName.setText(enText);
//    }
//    if (enText.contains("? ")) {
//      textFieldEnName.clear();
//      String[] split = enText.split(Pattern.quote("? "));
//      textFieldEnName.setText(split[0].trim() + "?");
//      textFieldOtherNames.setText(split[1].trim());
//    }
//    if (StringUtils.isNotEmpty(textFieldEnName.getText()) && StringUtils.isNotEmpty(textFieldOtherNames.getText())
//        && Character.isUpperCase(textFieldEnName.getText().substring(0, 1).toCharArray()[0])
//        && Character.isUpperCase(textFieldOtherNames.getText().substring(0, 1).toCharArray()[0])) {
//      textFieldEnName.setText(textFieldEnName.getText() + ". " + textFieldOtherNames.getText());
//      textFieldOtherNames.clear();
//    }

    if (StringUtils.isEmpty(word.getSound())) {
      List<String> wordNames = getWordNames();
      List<String> soundNames = getSoundsFromLesson(word);

      for (String wordName : wordNames) {
        if (!matchSound(wordName, soundNames)) {
          textFieldSound.clear();
          break;
        }
      }
    }

    // set shortcut
    textFieldId.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      final KeyCombination keyComb = new KeyCodeCombination(KeyCode.S,
          KeyCombination.CONTROL_DOWN);

      public void handle(KeyEvent ke) {
        if (keyComb.match(ke)) {
          buttonSaveWordOnAction(null);
          ke.consume(); // <-- stops passing the event to next node
        }
      }
    });
  }

  private void replaceText(TextField textField) {
    String text = textField.getText();
    if (StringUtils.isEmpty(text)) {
      return;
    }
    if (text.contains("informal") && !text.contains("(informal)")) {
      textField.setText(text.replaceAll("informal", "(informal)"));
    }
    if (text.contains("formal") && !text.contains("(formal)") && !text.contains("informal")) {
      textField.setText(text.replaceAll("formal", "(formal)"));
    }
    if (text.contains("literary") && !text.contains("(literary)")) {
      textField.setText(text.replaceAll("literary", "(literary)"));
    }
    if (text.contains("slang") && !text.contains("(slang)")) {
      textField.setText(text.replaceAll("slang", "(slang)"));
    }
  }

  public void buttonSaveAllWordOnAction(ActionEvent actionEvent) {

    List<WordRelease> wordReleases = wordReleaseService.findAll().stream()
        .filter(wordRelease -> StringUtils.isEmpty(wordRelease.getSound()))
        .collect(Collectors.toList());

    for (WordRelease wordRelease : wordReleases) {
      log.info("Set word {} {}", wordRelease.getId(), wordRelease.getEnName());
      setForm(wordRelease);
      buttonExtractOnAction(null);
      if (StringUtils.isNotEmpty(textFieldSound.getText())) {
        buttonSaveWordOnAction(null);
      }
    }

  }

  public void buttonRemoveWordOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      Optional<WordRelease> optionalWord = wordReleaseService.findById(Long.valueOf(textFieldId.getText()));
      optionalWord.ifPresent(word -> {
        wordReleaseService.delete(word);
        textFieldId.clear();
      });
    }
  }

  public void buttonSaveWordOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      Optional<WordRelease> optionalWord = wordReleaseService.findById(Long.valueOf(textFieldId.getText()));
      optionalWord.ifPresent(word -> {

        word.setPlName(textFieldPlName.getText() != null ? textFieldPlName.getText().trim() : "");
        word.setEnName(textFieldEnName.getText() != null ? textFieldEnName.getText().trim() : "");
        word.setUsName(textFieldUsName.getText() != null ? textFieldUsName.getText().trim() : "");
        word.setOtherNames(textFieldOtherNames.getText() != null ? textFieldOtherNames.getText().trim() : "");
        word.setComparative(textFieldComparative.getText() != null ? textFieldComparative.getText().trim() : "");
        word.setSuperlative(textFieldSuperlative.getText() != null ? textFieldSuperlative.getText().trim() : "");
        word.setPastTense(textFieldPastTense.getText() != null ? textFieldPastTense.getText().trim() : "");
        word.setPastParticiple(textFieldPastParticiple.getText() != null ? textFieldPastParticiple.getText().trim() : "");
        word.setPlural(textFieldPlural.getText() != null ? textFieldPlural.getText().trim() : "");
        word.setSynonym(textFieldSynonym.getText() != null ? textFieldSynonym.getText().trim() : "");
        word.setSound(textFieldSound.getText() != null ? textFieldSound.getText().trim() : "");
        // TODO set partOfSpeech, grammar and article

        word = wordReleaseService.save(word);
        injectFormService.getWordReleaseTableController().update(word);
      });
    }
  }

  public void buttonTranslateOnAction(ActionEvent actionEvent) {
    injectFormService.getScrapperFormController().tabPaneScrapper.getTabs().clear();
    if (StringUtils.isNotEmpty(textFieldEnName.getText()) && StringUtils.isNotEmpty(textFieldId.getText())) {

      wordReleaseService.findById(Long.valueOf(textFieldId.getText())).ifPresent(wordRelease -> {

        String text = textFieldEnName.getText();
        List<DikiWord> dikiWords = new DikiScrapper().webScrap(text);
        dikiWords.addAll(new BabScrapper().webScrap(text));
        wordRelease.setDikiWords(dikiWords);

        updateViewAfterChangeFocus(wordRelease);
      });
    }

  }

  public void updateViewAfterChangeFocus(WordRelease word) {
    if (!word.getDikiWords().isEmpty()) {
      updateViewAfterWebScrap(word.getDikiWords(), word.getPlName());
    } else {
      createEmptyTab(word);
    }
  }

  private void updateViewAfterWebScrap(List<DikiWord> dikiWords, String plName) {
    dikiWords.forEach(translation -> {
      try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        VBox vBox = fxmlLoader.load(getClass().getResource("/scene/items/scrapper_item.fxml").openStream());
        ScrapperController item = fxmlLoader.getController();
        item.setWordReleaseFormController(this);
        item.setPlTranslation(plName);
        item.initialize(translation);

        Tab tab = new Tab(translation.getEnName() + " |" + translation.getSource() + "|");
        tab.setContent(vBox);
        injectFormService.getScrapperFormController().tabPaneScrapper.getTabs().add(tab);
      } catch (Exception e) {
        log.error("[updateViewAfterWebScrap]", e);
      }
    });
  }

  private void createEmptyTab(WordRelease word) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      HBox hBox = fxmlLoader.load(getClass().getResource("/scene/items/empty_tab.fxml").openStream());
      Tab tab = new Tab(word.getEnName());
      tab.setContent(hBox);
      injectFormService.getScrapperFormController().tabPaneScrapper.getTabs().add(tab);
    } catch (Exception e) {
      log.error("[createEmptyTab]", e);
    }
  }

  public void buttonMatchSoundOnAction(ActionEvent actionEvent) {
    textFieldSound.clear();
    List<String> wordNames = getWordNames();
    List<String> soundNames = getSoundsFromLesson(wordReleaseService.findById(Long.valueOf(textFieldId.getText())).get());

    for (String wordName : wordNames) {
      if (!matchSound(wordName, soundNames)) {
        textFieldSound.clear();
        break;
      }
    }
  }

  public void buttonSoundOnAction(ActionEvent actionEvent) {
    textFieldSound.clear();

    List<String> names = getWordNames();
    for (String name : names) {
      WordRelease word = wordReleaseService.findById(Long.valueOf(textFieldId.getText())).get();
      String audioFolder = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\";

      String courseFolderName = word.getLesson().getCourse().getEnName();
      String lessonFolderName = word.getLesson().getEnName()
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
      fileChooser.setTitle(name);
      File soundFile = fileChooser.showOpenDialog(textFieldId.getScene().getWindow());

      if (soundFile != null) {
        textFieldSound.setText(textFieldSound.getText() + "[" + name + "="
            + soundFile.getName().replaceAll("en_", "").replaceAll("us_", "")
            + "];");
      }
    }
  }

  private void setToggleGroups() {
    toggleGroupPartOfSpeech = new ToggleGroup();
    setToggleGroup(Arrays.asList(toggleButtonNoun, toggleButtonVerb, toggleButtonAdjective, toggleButtonAdverb, toggleButtonPhrasalVerb,
        toggleButtonSentence, toggleButtonIdiom, toggleButtonOther), toggleGroupPartOfSpeech);

    toggleGroupGrammarType = new ToggleGroup();
    setToggleGroup(Arrays.asList(toggleButtonCountable, toggleButtonUncountable, toggleButtonSingular, toggleButtonPlural,
        toggleButtonTransitive, toggleButtonIntransitive, toggleButtonCountableUncountable, toggleButtonEmpty), toggleGroupGrammarType);

    toggleGroupArticle = new ToggleGroup();
    setToggleGroup(Arrays.asList(toggleButtonA, toggleButtonAn, toggleButtonNone), toggleGroupArticle);
  }

  private void setToggleGroup(List<ToggleButton> toggleButtons, ToggleGroup toggleGroup) {
    toggleButtons.forEach(toggleButton -> toggleButton.setToggleGroup(toggleGroup));
  }

  private List<String> getWordNames() {

    List<String> names = new ArrayList<>();

    if (StringUtils.isNotEmpty(textFieldEnName.getText())) {
      names.add(textFieldEnName.getText());
    }
    if (StringUtils.isNotEmpty(textFieldUsName.getText())) {
      names.add(textFieldUsName.getText());
    }
    if (StringUtils.isNotEmpty(textFieldOtherNames.getText())) {
      names.addAll(Arrays.asList(textFieldOtherNames.getText().split(";")));
    }
    if (StringUtils.isNotEmpty(textFieldComparative.getText())) {
      names.addAll(Arrays.asList(textFieldComparative.getText().split(";")));
    }
    if (StringUtils.isNotEmpty(textFieldSuperlative.getText())) {
      names.addAll(Arrays.asList(textFieldSuperlative.getText().split(";")));
    }
    if (StringUtils.isNotEmpty(textFieldPastTense.getText())) {
      names.addAll(Arrays.asList(textFieldPastTense.getText().split(";")));
    }
    if (StringUtils.isNotEmpty(textFieldPastParticiple.getText())) {
      names.addAll(Arrays.asList(textFieldPastParticiple.getText().split(";")));
    }
//    if (StringUtils.isNotEmpty(textFieldPlural.getText())) {
//      names.addAll(Arrays.asList(textFieldPlural.getText().split(";")));
//    }
    if (StringUtils.isNotEmpty(textFieldSynonym.getText())) {
      names.addAll(Arrays.asList(textFieldSynonym.getText().split(";")));
    }
    names = names.stream().map(String::trim).collect(Collectors.toList());
    return names;
  }

  private List<String> getSoundsFromLesson(WordRelease word) {
    String audioFolder = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\";

    String courseFolderName = word.getLesson().getCourse().getEnName();
    String lessonFolderName = word.getLesson().getEnName()
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

  private boolean matchSound(String wordName, List<String> soundNames) {

    String expectedSoundName = wordName
        .replaceAll(Pattern.quote("(informal)"), "")
        .replaceAll(Pattern.quote("(British English)"), "")
        .replaceAll(Pattern.quote("(American English)"), "")
        .replaceAll(Pattern.quote("(literary)"), "")
        .replaceAll(Pattern.quote("(slang)"), "")
        .replaceAll(Pattern.quote("(formal)"), "").trim()
        .replaceAll(" ", "_")
        .replaceAll("'", "")
        .replaceAll("\\.", "")
        .replaceAll(",", "")
        .replaceAll(":", "")
        .replaceAll(":", "")
        .replaceAll("!", "")
        .replaceAll("\\?", "")
        .toLowerCase() + ".mp3";

    List<String> matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(expectedSoundName))
        .collect(Collectors.toList());

    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName1 = expectedSoundName.replace(".mp3", "-v.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName1))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName2 = expectedSoundName.replace(".mp3", "-n.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName2))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName3 = expectedSoundName.replace(".mp3", "-a.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName3))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName4 = expectedSoundName.replace(".mp3", "-1.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName4))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName5 = expectedSoundName.replace(".mp3", "-2.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName5))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName6 = expectedSoundName.replace(".mp3", "-exclamation.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName6))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    String soundName7 = expectedSoundName.replace(".mp3", "-sf.mp3");
    matchedSounds = soundNames.stream()
        .filter(sound -> sound.equals(soundName7))
        .collect(Collectors.toList());
    if (matchedSounds.size() == 1) {
      String currentText = textFieldSound.getText() != null ? textFieldSound.getText() : "";
      textFieldSound.setText(currentText + "[" + wordName + "=" + matchedSounds.get(0) + "];");
      return true;
    }

    return false;
  }


  public void buttonDotOnAction(ActionEvent actionEvent) {
    if (!textFieldEnName.getText().endsWith(".")) {
      textFieldEnName.setText(textFieldEnName.getText().concat("."));
    }
    if (StringUtils.isNotEmpty(textFieldOtherNames.getText()) && !textFieldOtherNames.getText().endsWith(".")) {
      textFieldOtherNames.setText(textFieldOtherNames.getText().concat("."));
    }
    buttonMatchSoundOnAction(null);
  }

  public void buttonExtractOnAction(ActionEvent actionEvent) {
    String[] textArray = textFieldEnName.getText().split(", ");
    List<String> texts = new ArrayList<>(Arrays.asList(textArray));
    texts = texts.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());

    if (texts.size() == 2 && texts.get(0).contains("British") && texts.get(1).contains("American")) {
      textFieldEnName.setText(texts.get(0).trim());
      textFieldUsName.setText(texts.get(1).trim());
    } else if (texts.size() > 1) {
      textFieldEnName.setText(texts.get(0));

      textFieldOtherNames.setText("");
      texts
          .subList(1, texts.size())
          .forEach(text -> {
            if (textFieldOtherNames.getText().isEmpty()) {
              textFieldOtherNames.setText(textFieldOtherNames.getText().concat(text).trim());
            } else {
              textFieldOtherNames.setText(textFieldOtherNames.getText().concat("; ").concat(text).trim());
            }
          });
    }
    buttonMatchSoundOnAction(null);
  }

}
