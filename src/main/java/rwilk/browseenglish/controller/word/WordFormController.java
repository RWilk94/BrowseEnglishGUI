package rwilk.browseenglish.controller.word;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.controller.scrapper.ScrapperController;
import rwilk.browseenglish.controller.scrapper.ScrapperFormController;
import rwilk.browseenglish.controller.scrapper.SentenceTabController;
import rwilk.browseenglish.model.SentenceView;
import rwilk.browseenglish.model.diki.DikiWord;
import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.release.service.ExportReleaseDatabaseService;
import rwilk.browseenglish.scrapper.ang.AngScrapper;
import rwilk.browseenglish.scrapper.diki.BabScrapper;
import rwilk.browseenglish.scrapper.diki.DikiScrapper;
import rwilk.browseenglish.scrapper.etutor.EtutorDictionaryScrapper;
import rwilk.browseenglish.scrapper.etutor.EtutorLessonScrapper;
import rwilk.browseenglish.scrapper.etutor.EtutorScrapper;
import rwilk.browseenglish.scrapper.tatoeba.TatoebaScrapper;
import rwilk.browseenglish.scrapper.wiki.WikiScrapper;
import rwilk.browseenglish.service.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Slf4j
@Controller
public class WordFormController implements Initializable {

  private static final String regex = "[^a-zA-Z0-9 ']";
  private final FormWordService formWordService;
  private final ScrapperFormController scrapperFormController;
  private final WikiScrapper wikiScrapper;
  private final EtutorScrapper etutorScrapper;
  private final CourseService courseService;
  private final LessonService lessonService;
  private final WordService wordService;
  private final SentenceService sentenceService;
  private final DikiWordService dikiWordService;
  private final EtutorLessonScrapper etutorLessonScrapper;
  private final ExportDatabaseService exportDatabaseService;
  private final EtutorDictionaryScrapper etutorDictionaryScrapper;

  private final ExportReleaseDatabaseService exportReleaseDatabaseService;

  public ListView<String> listViewSound;
  public TextField textFieldSound;
  public ListView<String> listViewSoundWord;

  @Autowired
  private AngScrapper angScrapper;

  public TextField textFieldId;
  public TextField textFieldEnWord;
  public TextField textFieldUsWord;
  public TextField textFieldPlWord;
  public TextArea textAreaOtherVersion;
  public ToggleGroup toggleGroupPartOfSpeech;
  public ToggleButton toggleButtonNoun;
  public ToggleButton toggleButtonVerb;
  public ToggleButton toggleButtonAdjective;
  public ToggleButton toggleButtonAdverb;
  public ToggleButton toggleButtonPhrasalVerb;
  public ToggleButton toggleButtonSentence;
  public ToggleButton toggleButtonIdiom;
  public ToggleButton toggleButtonOther;
  public ToggleButton toggleButtonCountable;
  public ToggleButton toggleButtonUncountable;
  public ToggleButton toggleButtonSingular;
  public ToggleButton toggleButtonPlural;
  public ToggleButton toggleButtonTransitive;
  public ToggleButton toggleButtonIntransitive;
  public ToggleButton toggleButtonCountableUncountable;
  public ToggleButton toggleButtonEmpty;
  public ToggleButton toggleButtonA;
  public ToggleButton toggleButtonAn;
  public ToggleButton toggleButtonNone;
  public TextField textFieldComparative;
  public TextField textFieldSuperlative;
  public TextField textFieldPastTense;
  public TextField textFieldPastParticiple;
  public TextField textFieldPlural;
  // public TabPane tabPaneScrapper;
  public TextField textFieldSynonym;
  public ListView<SentenceView> listViewSentences;
  public TextField textFieldSentenceEn;
  public TextField textFieldSentencePl;
  public TextField textFieldSentenceId;
  public TextField textFieldTatoeba;

  public TextField textFieldAngUrl;
  public TextField textFieldAngLessonId;

  private ToggleGroup toggleGroupGrammarType;
  private ToggleGroup toggleGroupArticle;

  public WordFormController(FormWordService formWordService, ScrapperFormController scrapperFormController,
                            WikiScrapper wikiScrapper, EtutorScrapper etutorScrapper, CourseService courseService, LessonService lessonService,
                            WordService wordService,
                            SentenceService sentenceService, DikiWordService dikiWordService, EtutorLessonScrapper etutorLessonScrapper, ExportDatabaseService exportDatabaseService, EtutorDictionaryScrapper etutorDictionaryScrapper, ExportReleaseDatabaseService exportReleaseDatabaseService) {
    this.formWordService = formWordService;
    this.scrapperFormController = scrapperFormController;
    this.wikiScrapper = wikiScrapper;
    this.etutorScrapper = etutorScrapper;
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.sentenceService = sentenceService;
    this.dikiWordService = dikiWordService;
    this.etutorLessonScrapper = etutorLessonScrapper;
    this.exportDatabaseService = exportDatabaseService;
    this.etutorDictionaryScrapper = etutorDictionaryScrapper;
    this.exportReleaseDatabaseService = exportReleaseDatabaseService;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    formWordService.setWordFormController(this);

    setToggleGroups();

    listViewSentences.setCellFactory(lv -> new ListCell<SentenceView>() {
      @Override
      protected void updateItem(SentenceView item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          this.setText(item.toString());
        } else {
          this.setText(null);
        }
        if (item != null && item.toString().contains("[") && item.toString().contains("]")) {
          this.setStyle("-fx-background-color: #6BFF59");
        } else {
          setStyle("");
        }
      }
    });

  }

  public void buttonAngOnAction(ActionEvent actionEvent) {
    lessonService.findById(Long.valueOf(textFieldAngLessonId.getText())).ifPresent(lesson -> {
      List<Word> words = angScrapper.webScrapWithImages(textFieldAngUrl.getText());
      for (Word word : words) {
        word.setLesson(lesson);
        wordService.save(word);
      }
    });


  }

  public void buttonTranslateOnAction(ActionEvent event) {
    if (StringUtils.isNotEmpty(textFieldEnWord.getText()) && StringUtils.isNotEmpty(textFieldId.getText())) {
      Optional<Word> optionalWord = wordService.findById(Long.valueOf(textFieldId.getText()));
      optionalWord.ifPresent(word -> {
        String text = textFieldEnWord.getText();
        List<DikiWord> dikiWords = new DikiScrapper().webScrap(text);
        dikiWords.addAll(new BabScrapper().webScrap(text));

        dikiWordService.saveDikiWords(dikiWords, word);
        word.setDikiWords(dikiWords);

        // find sentences in database
        word.setSentences(sentenceService.findAll(word.getEnName(), word.getPlName()));

        updateViewAfterChangeFocus(word);
      });
    }
  }

  public void buttonTatoebaOnAction(ActionEvent actionEvent) {
    Optional<Word> wordOptional = wordService.findById(Long.valueOf(textFieldId.getText()));
    wordOptional.ifPresent(word -> {
      TatoebaScrapper tatoebaScrapper = new TatoebaScrapper();
      List<Sentence> sentences = tatoebaScrapper.webScrapSelenium(word, "pol", textFieldTatoeba.getText());
      if (sentences.isEmpty()) {
        sentences = tatoebaScrapper.webScrapSelenium(word, "und", textFieldTatoeba.getText());
      }
      sentences = sentenceService.saveAll(sentences);
      word.setSentenceScrapped(true);
      word = wordService.save(word);

      word.setSentences(sentences);
      createSentenceTab(word);
    });
  }

  public void buttonEditWordOnAction(ActionEvent actionEvent) {
    if (StringUtils.isNotEmpty(textFieldId.getText())) {
      Optional<Word> optionalWord = wordService.findById(Long.valueOf(textFieldId.getText()));
      optionalWord.ifPresent(word -> {

        String plName = word.getPlName();
        Long wordId = word.getId();
        // List<Word> duplicateItems = wordTableController.getDuplicateTableController().tableViewWords.getItems();
        List<Word> duplicateItems = formWordService.getWordTableController().getDuplicateTableController().tableViewWords.getItems();
        duplicateItems = duplicateItems.stream()
            .filter(duplicateItem -> duplicateItem.getPlName().equals(plName))
            .filter(duplicateItem -> duplicateItem.getId().longValue() != wordId.longValue())
            .collect(Collectors.toList());

        word.setEnName(textFieldEnWord.getText().trim());
        word.setUsName(textFieldUsWord.getText().trim());
        word.setPlName(textFieldPlWord.getText().trim());
        if (!textAreaOtherVersion.getText().isEmpty()) {
          word.setOtherNames(textAreaOtherVersion.getText().trim());
        } else {
          word.setOtherNames("");
        }

        word.setPartOfSpeech(((ToggleButton) toggleGroupPartOfSpeech.getSelectedToggle()).getText().trim());
        word.setGrammarType(((ToggleButton) toggleGroupGrammarType.getSelectedToggle()).getText().trim());
        // word.setArticle(((ToggleButton)toggleGroupArticle.getSelectedToggle()).getText());
        word.setComparative(textFieldComparative.getText().trim());
        word.setSuperlative(textFieldSuperlative.getText().trim());
        word.setPastTense(textFieldPastTense.getText().trim());
        word.setPastParticiple(textFieldPastParticiple.getText().trim());
        word.setPlural(textFieldPlural.getText().trim());
        word.setSynonym(textFieldSynonym.getText().trim());
        word.setWordReady(true);

        word = wordService.save(word);
        setFormFields(word);

        for (Word duplicatedWord : duplicateItems) {
          formWordService.getWordTableController().getDuplicateTableController().deleteWord(duplicatedWord.getId());
        }

        formWordService.getWordTableController().refreshTable(word);
        formWordService.getWordTableController().getDuplicateTableController().updateWord(word);

        formWordService.getWordTableController().getDuplicateTableController()
            .filterTableByName(formWordService.getWordTableController().getDuplicateTableController().textFieldFilterByName.getText());
      });
    }
  }

  public void buttonAddWordOnAction(ActionEvent actionEvent) {
    // TODO
    Word word = Word.builder()
        .enName(textFieldEnWord.getText().trim())
        .usName(textFieldUsWord.getText().trim())
        .otherNames(textAreaOtherVersion.getText().trim())
        .plName(textFieldPlWord.getText().trim())
        .partOfSpeech(toggleButtonOther.getText())
        .grammarType(toggleButtonEmpty.getText())
        .comparative("")
        .superlative("")
        .pastTense("")
        .pastParticiple("")
        .plural("")
        .synonym("")
        .lesson(lessonService.findById(1945L).get())
        .build();
    word = wordService.save(word);
    setFormFields(word);
  }

  public void updateViewAfterChangeFocus(Word word) {
    if (!word.getDikiWords().isEmpty()) {
      createSentenceTab(word);
      updateViewAfterWebScrap(word.getDikiWords(), word.getPlName());
    } else {
      createEmptyTab(word);
    }
  }

  public void setPartOfSpeech(String partOfSpeech) {
    switch (partOfSpeech != null ? partOfSpeech : "") {
      case "rzecz.":
        partOfSpeech = "rzeczownik";
        break;
      case "czas.":
        partOfSpeech = "czasownik";
        break;
      case "przym.":
        partOfSpeech = "przymiotnik";
        break;
      case "przysł.":
        partOfSpeech = "przysłówek";
        break;
    }

    List<ToggleButton> partOfSpeechButtons = Arrays.asList(toggleButtonNoun, toggleButtonVerb, toggleButtonAdjective, toggleButtonAdverb,
        toggleButtonPhrasalVerb, toggleButtonSentence, toggleButtonIdiom, toggleButtonOther);

    String partOfSpeechTemp = partOfSpeech;
    ToggleButton toggleButton = partOfSpeechButtons
        .stream()
        .filter(button -> button.getText().equalsIgnoreCase(partOfSpeechTemp))
        .findFirst()
        .orElse(toggleButtonOther);

    toggleButton.setSelected(true);
  }

  public void setGrammarType(String grammarType) {
    List<ToggleButton> grammarTypeButtons = Arrays.asList(toggleButtonCountable, toggleButtonUncountable, toggleButtonSingular,
        toggleButtonPlural, toggleButtonTransitive, toggleButtonIntransitive, toggleButtonCountableUncountable, toggleButtonEmpty);

    ToggleButton toggleButton = grammarTypeButtons
        .stream()
        .filter(button -> button.getText().equalsIgnoreCase(grammarType))
        .findFirst()
        .orElse(toggleButtonEmpty);

    toggleButton.setSelected(true);
  }

  public void setArticle(String article) {
    // implements method
  }

  public void removeAllTabs() {
    scrapperFormController.tabPaneScrapper.getTabs().clear();
  }

  public void buttonClearOnAction(ActionEvent actionEvent) {
    textFieldPlWord.clear();
  }

  public void buttonBrEOnAction(ActionEvent actionEvent) {
    buttonAmEOnAction(actionEvent);
  }

  public void buttonAmEOnAction(ActionEvent actionEvent) {
    textFieldEnWord.setText(textFieldEnWord.getText()
        .replaceAll("British English", "(British English)")
        .replaceAll("American English", "(American English)")
        .replaceAll("\\(\\(", "(").replaceAll("\\)\\)", ")"));
    textFieldUsWord.setText(textFieldUsWord.getText()
        .replaceAll("American English", "(American English)")
        .replaceAll("British English", "(British English)")
        .replaceAll("\\(\\(", "(").replaceAll("\\)\\)", ")"));
    textAreaOtherVersion.setText(textAreaOtherVersion.getText()
        .replaceAll("British English", "(British English)")
        .replaceAll("American English", "(American English)")
        .replaceAll("\\(\\(", "(").replaceAll("\\)\\)", ")"));
  }

  public void setFormFields(Word word) {
    textFieldId.setText(String.valueOf(word.getId()));
    textFieldEnWord.setText(word.getEnName());
    textFieldUsWord.setText(word.getUsName());
    textFieldPlWord.setText(word.getPlName());
    if (!StringUtils.isEmpty(word.getOtherNames())) {
      textAreaOtherVersion.setText(word.getOtherNames().replaceAll(" ,  ", "\n"));
    } else {
      textAreaOtherVersion.setText("");
    }
    setPartOfSpeech(word.getPartOfSpeech());
    setGrammarType(word.getGrammarType());
    setArticle(word.getArticle());
    textFieldComparative.setText(word.getComparative());
    textFieldSuperlative.setText(word.getSuperlative());
    textFieldPastTense.setText(word.getPastTense());
    textFieldPastParticiple.setText(word.getPastParticiple());
    textFieldPlural.setText(word.getPlural());
    textFieldSynonym.setText(word.getSynonym());

    buttonAmEOnAction(null);

    textFieldSentenceId.clear();
    textFieldSentenceEn.clear();
    textFieldSentencePl.clear();
    listViewSentences.getItems().clear();

    listViewSentences.setItems(FXCollections.observableArrayList(
        sentenceService.findAllByWordAndIsSentenceMatched(word, true)
            .stream()
            .map(SentenceView::new)
            .collect(Collectors.toList())
        )
    );
  }

  public void listViewSentencesOnMouseClicked(MouseEvent mouseEvent) {
    SentenceView sentenceView = listViewSentences.getSelectionModel().getSelectedItem();
    if (sentenceView != null) {
      textFieldSentenceId.setText(sentenceView.getId().toString());
      textFieldSentenceEn.setText(sentenceView.getEnSentence().trim());
      textFieldSentencePl.setText(sentenceView.getPlSentence().trim());
    }
  }

  public void buttonAddSentenceOnAction(ActionEvent actionEvent) {
    String sentenceEn = textFieldSentenceEn.getText(); // .replaceAll(regex, "");
    String textEn = textFieldEnWord.getText()
        .replaceAll("\\(British English\\)", "")
        .trim()
        .replaceAll("\\(American English\\)", "")
        .trim();
    if (!sentenceEn.contains("[") && !sentenceEn.contains("]")
        && sentenceEn.replaceAll(regex, "").toLowerCase().contains(textEn.replaceAll(regex, "").toLowerCase())) {

      int startIndex = sentenceEn.replaceAll(regex, "").toLowerCase().indexOf(textEn.replaceAll(regex, "").toLowerCase());
      int endIndex = startIndex + textEn.replaceAll(regex, "").length();

      while (sentenceEn.substring(endIndex, endIndex + 1).matches("[a-zA-z]")) {
        endIndex++;
      }

      sentenceEn =
          sentenceEn.substring(0, startIndex) + "[" + sentenceEn.substring(startIndex, endIndex) + "]" + sentenceEn.substring(endIndex);
    }

    Sentence sentence = Sentence.builder()
        .enSentence(sentenceEn.trim())
        .plSentence(textFieldSentencePl.getText().trim())
        .word(wordService.findById(Long.valueOf(textFieldId.getText())).get())
        .isSentenceMatched(true)
        .build();
    sentence = sentenceService.save(sentence);

    SentenceView sentenceView = new SentenceView(sentence);
    listViewSentences.getItems().add(sentenceView);

    // Update words table
    Optional<Word> wordOptional = wordService.findById(Long.valueOf(textFieldId.getText()));
    wordOptional.ifPresent(word -> {
      word.setWordWithSentence(true);
      word = wordService.save(word);
      // wordTableController.refreshTable(word);
      formWordService.getWordTableController().refreshTable(word);
    });

  }

  public void buttonEditSentenceOnAction(ActionEvent actionEvent) {
    ObservableList<SentenceView> sentenceItems = listViewSentences.getItems();
    sentenceItems.stream()
        .filter(sentenceView -> sentenceView.getId().longValue() == Long.valueOf(textFieldSentenceId.getText()).longValue())
        .findFirst()
        .ifPresent(sentenceView -> {

          Sentence sentence = sentenceService.findById(sentenceView.getId()).get();
          sentence.setEnSentence(textFieldSentenceEn.getText().trim());
          sentence.setPlSentence(textFieldSentencePl.getText().trim());
          sentence = sentenceService.save(sentence);

          sentenceView.setEnSentence(sentence.getEnSentence());
          sentenceView.setPlSentence(sentence.getPlSentence());
        });
    listViewSentences.setItems(null);
    listViewSentences.setItems(sentenceItems);
  }

  public void buttonDeleteSentenceOnAction(ActionEvent actionEvent) {
    ObservableList<SentenceView> sentenceItems = listViewSentences.getItems();
    sentenceItems.stream()
        .filter(sentenceView -> sentenceView.getId().longValue() == Long.valueOf(textFieldSentenceId.getText()).longValue())
        .findFirst()
        .ifPresent(sentenceView -> {
          sentenceService.deleteById(sentenceView.getId());
          listViewSentences.getItems().remove(sentenceView);
        });

    if (listViewSentences.getItems().isEmpty()) {
      // Update words table
      Optional<Word> wordOptional = wordService.findById(Long.valueOf(textFieldId.getText()));
      wordOptional.ifPresent(word -> {
        word.setWordWithSentence(false);
        word = wordService.save(word);
        // wordTableController.refreshTable(word);
        formWordService.getWordTableController().refreshTable(word);
      });
    }

  }

  public void buttonOkSentenceOnAction(ActionEvent actionEvent) {
    Optional<Word> wordOptional = wordService.findById(Long.valueOf(textFieldId.getText()));
    wordOptional.ifPresent(word -> {
      word.setWordWithSentence(true);
      word = wordService.save(word);
      // wordTableController.refreshTable(word);
      formWordService.getWordTableController().refreshTable(word);
    });
  }

  public void buttonWikiOnAction(ActionEvent actionEvent) {
    //    wikiScrapper.webScrap1000BaseWord();
    wikiScrapper.webScrap1_2000MostFrequentWords();
  }

  //  public void addCourseOnAction(ActionEvent actionEvent) {
//    try {
//      FXMLLoader fxmlLoader = new FXMLLoader();
//      fxmlLoader.setLocation(getClass().getResource("/scene/course/course.fxml"));
//      BorderPane borderPane = fxmlLoader.load();
//      CourseController item = fxmlLoader.getController();
//      item.init(courseService);
//      Scene scene = new Scene(borderPane, 800, 600);
//      Stage stage = new Stage();
//      stage.setTitle("Courses");
//      stage.setScene(scene);
//      stage.show();
//      scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
//        @Override
//        public void handle(WindowEvent event) {
//          // TODO refresh courses
//        }
//      });
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

//  public void addLessonOnAction(ActionEvent actionEvent) {
//    try {
//      FXMLLoader fxmlLoader = new FXMLLoader();
//      fxmlLoader.setLocation(getClass().getResource("/scene/lesson/lesson.fxml"));
//      BorderPane borderPane = fxmlLoader.load();
//      LessonController item = fxmlLoader.getController();
//      item.init(lessonService, courseService);
//      Scene scene = new Scene(borderPane, 800, 600);
//      Stage stage = new Stage();
//      stage.setTitle("Lessons");
//      stage.setScene(scene);
//      stage.show();
//      scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
//        @Override
//        public void handle(WindowEvent event) {
//          // TODO refresh lessons
//        }
//      });
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }

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

  private void updateViewAfterWebScrap(List<DikiWord> dikiWords, String plName) {
    dikiWords.forEach(translation -> {
      try {
        FXMLLoader fxmlLoader = new FXMLLoader();
        VBox vBox = fxmlLoader.load(getClass().getResource("/scene/items/scrapper_item.fxml").openStream());
        ScrapperController item = fxmlLoader.getController();
        item.setWordFormController(this);
        item.setPlTranslation(plName);
        item.initialize(translation);

        Tab tab = new Tab(translation.getEnName() + " |" + translation.getSource() + "|");
        tab.setContent(vBox);
        scrapperFormController.tabPaneScrapper.getTabs().add(tab);
      } catch (Exception e) {
        log.error("[updateViewAfterWebScrap]", e);
      }
    });
  }

  private void createEmptyTab(Word word) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader();
      HBox hBox = fxmlLoader.load(getClass().getResource("/scene/items/empty_tab.fxml").openStream());
      Tab tab = new Tab(word.getEnName());
      tab.setContent(hBox);
      scrapperFormController.tabPaneScrapper.getTabs().add(tab);
    } catch (Exception e) {
      log.error("[createEmptyTab]", e);
    }
  }

  private void createSentenceTab(Word word) {
    try {
      if (StringUtils.isNotEmpty(word.getEnName())) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        VBox vBox = fxmlLoader.load(getClass().getResource("/scene/items/sentence_tab.fxml").openStream());
        SentenceTabController item = fxmlLoader.getController();
        item.setWordFormController(this);
        item.initialize(sentenceService.findAllByEnSentenceContainsOrWord(word.getEnName(), word));

        Tab tab = new Tab(word.getEnName() + " |sentence|");
        tab.setContent(vBox);
        scrapperFormController.tabPaneScrapper.getTabs().add(tab);
      }
    } catch (Exception e) {
      log.error("[createSentenceTab]", e);
    }
  }

  public void buttonEtutorOnAction(ActionEvent actionEvent) throws IOException {
//     etutorScrapper.webScrap();

    // etutorDictionaryScrapper.webScrapDictionaryNames();
    //etutorDictionaryScrapper.webScrapDictionary();

    // TODO export database here
    exportReleaseDatabaseService.exportDatabase();


    // exportReleaseDatabaseService.prepareSounds();

//    etutorLessonScrapper.webScrapLessonAudioAndGrammar();

//    etutorScrapper.webScrapAudio();

//    exportDatabaseService.exportDatabase();


//    etutorLessonScrapper.insertCourses();
//    etutorLessonScrapper.insertLessons();

//    etutorLessonScrapper.webScrapLessons();

  }


  public void buttonSoundOnAction(ActionEvent actionEvent) {
//    List<Word> words = wordService.findAll().stream()
//        .filter(word -> word.getSound() == null)
//        .collect(Collectors.toList());

    Word word = wordService.findById(Long.valueOf(textFieldId.getText())).get();

//    for (Word word : words) {
//      log.info("START {}", word.getEnName());

    Path path = Paths.get("C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\"
        + word.getLesson().getCourse().getEnName().replace("ETUTOR", "").trim()
        .replace("Lekcje: ", "").trim()
        + "\\"
        + word.getLesson().getEnName().substring(word.getLesson().getEnName().indexOf(" ")).trim()
        .replace("...", "")
        .replace("?", "")
        .replace(":", "")
        .replaceAll("\"", "")
        .replaceAll("/", "")
        .replaceAll("\\\\", "")
        .replace("!", "").trim());

    if (!Files.exists(path)) {
      path = Paths.get("C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\"
          + word.getLesson().getCourse().getEnName().replace("ETUTOR", "").trim()
          .replace("Lekcje: ", "").trim()
          + "\\ "
          + word.getLesson().getEnName().substring(word.getLesson().getEnName().indexOf(" ")).trim()
          .replace("...", "")
          .replace("?", "")
          .replace(":", "")
          .replaceAll("\"", "")
          .replaceAll("/", "")
          .replaceAll("\\\\", "")
          .replace("!", "").trim());
    }

    try (Stream<Path> walk = Files.walk(path)) {

      List<String> result = walk.filter(Files::isRegularFile)
          .map(Path::toString)
          .map(p -> p.substring(p.lastIndexOf("\\") + 1))
          .map(name -> name.substring(3))
          .collect(Collectors.toList());

      String name = word.getEnName()
          .replaceAll("informal", "")
          .replaceAll("British English", "")
          .replaceAll("American English", "")
          .replaceAll("formal", "").trim()
          .replaceAll(" ", "_")
          .replaceAll("'", "")
          .replaceAll("\\.", "")
          .replaceAll(",", "")
          .replaceAll(":", "")
          .replaceAll(":", "")
          .replaceAll("!", "")
          .replaceAll("\\?", "")
          .toLowerCase();

//        result = result.stream().filter(n -> n.contains(name)).collect(Collectors.toList());

      // name += "-exclamation";

      result = result.stream().filter(audio -> audio.startsWith(name.substring(0, 1))).collect(Collectors.toList());
      listViewSound.setItems(null);
      listViewSound.setItems(FXCollections.observableArrayList(result));

//        if (result.size() == 1) {
//          word.setSound(result.get(0));
//          wordService.save(word);
//        }

//        if (result.contains(name + ".mp3")) {
////          System.out.println(name + ".mp3");
//          word.setSound(name + ".mp3");
//          wordService.save(word);
//        } else {
//          System.err.println(name);
//        }

      // result.forEach(System.out::println);

    } catch (IOException e) {
      e.printStackTrace();
    }

//    }


  }

  public void listViewSoundOnMouseClicked(MouseEvent mouseEvent) {

  }

  public void buttonEditSoundOnAction(ActionEvent actionEvent) throws IOException {

//    String soundName = listViewSound.getSelectionModel().getSelectedItem();
//    if (soundName != null && textFieldId.getText() != null) {
//
//      Word word = wordService.findById(Long.valueOf(textFieldId.getText())).get();
//      word.setSound(soundName);
//
//      log.info("SAVE {} {}", word.getEnName(), soundName);
//
//      wordService.save(word);
//      formWordService.getWordTableController().removeWord(word);
//
//    }

    copyAudioToFolder();

  }

  public void listViewSoundWordOnMouseClicked(MouseEvent mouseEvent) {
  }


  public void copyAudioToFolder() throws IOException {

    String desc = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio-all\\";
//    String desc = "Ten komputer\\Honor 91\\Karta pamięci Samsung\\angielski\\";
    ArrayList<File> files = new ArrayList<>(Arrays.asList(new File("C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\")));

    File file = files.get(0);

    // file.listFiles();

    for (int i = 0; i < file.listFiles().length; i++) {

      log.info("START {} / {}", i, file.listFiles().length);
      File courseFolder = file.listFiles()[i];

      for (int j = 0; j < courseFolder.listFiles().length; j++) {

        File lessonFolder = courseFolder.listFiles()[j];

        for (int k = 0; k < lessonFolder.listFiles().length; k++) {

          File sound = lessonFolder.listFiles()[k];
          copyFileUsingStream(sound, new File(desc + sound.getName()));
        }

      }
      log.info("FINISH {} / {}", i, file.listFiles().length);
    }

  }


  // C:\Users\R.Wilk\IdeaProjects\BrowseEnglishGUI\audio

//    Path path = Paths.get("C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\");
////    Path path2 = Paths.get("C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio-all\\");
//
//    String desc = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio-all\\";
//
//    try (Stream<Path> walk = Files.walk(path)) {
//
//      walk.forEach(folder -> {
//        try {
//          Stream<Path> walk1 = Files.walk(folder);
//          walk1.forEach(p -> {
//            // log.info(p.toString());
//            try {
//
//              p.
//
//
//              copyFileUsingStream(p.toFile(), new File(desc + p.getFileName()));
//            } catch (IOException e) {
//              e.printStackTrace();
//            }
//          });
//
//
//
//        } catch (Exception e) {
//          log.error("", e);
//        }
//      });
//
//    }

  // List<Word> words = wordService.findAll().stream().filter(word -> word.getSound() != null).collect(Collectors.toList());
//
//    for (Word word : words) {
//
//    }

//  }

  private static void copyFileUsingStream(File source, File dest) throws IOException {
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(source);
      outputStream = new FileOutputStream(dest);
      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

}
