package rwilk.browseenglish.scrapper.etutor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rwilk.browseenglish.model.etutor.EtutorCourse;
import rwilk.browseenglish.model.etutor.EtutorGrammar;
import rwilk.browseenglish.model.etutor.EtutorLesson;
import rwilk.browseenglish.release.entity.LessonRelease;
import rwilk.browseenglish.release.repository.LessonReleaseRepository;
import rwilk.browseenglish.repository.EtutorCourseRepository;
import rwilk.browseenglish.repository.EtutorGrammarRepository;
import rwilk.browseenglish.repository.EtutorLessonRepository;
import rwilk.browseenglish.scrapper.audio.DikiAudio;
import rwilk.browseenglish.scrapper.etutor.model.EtutorChoiceItem;
import rwilk.browseenglish.scrapper.etutor.model.EtutorGrammarContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EtutorLessonScrapper {

  private static final String AUDIO_FOLDER = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\";
  private static final int MAX_COLUMN_SIZE = 2000;
  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorLessonRepository etutorLessonRepository;
  private final EtutorGrammarRepository etutorGrammarRepository;
  private final LessonReleaseRepository lessonReleaseRepository;
  private final DikiAudio dikiAudio;


  private static List<String> courseLinks = Arrays.asList(
      "https://www.etutor.pl/lessons/en/a1",
      "https://www.etutor.pl/lessons/en/a2",
      "https://www.etutor.pl/lessons/en/b1",
      "https://www.etutor.pl/lessons/en/b2",
      "https://www.etutor.pl/lessons/en/c1",
      "https://www.etutor.pl/lessons/en/c2",
      "https://www.etutor.pl/lessons/en/travel",
      "https://www.etutor.pl/lessons/en/business-b1",
      "https://www.etutor.pl/lessons/en/business-b2",
      "https://www.etutor.pl/lessons/en/business-c",
      "https://www.etutor.pl/lessons/en/business-ext",
      "https://www.etutor.pl/lessons/en/blended"
  );

  @Autowired
  public EtutorLessonScrapper(EtutorCourseRepository etutorCourseRepository, EtutorLessonRepository etutorLessonRepository, EtutorGrammarRepository etutorGrammarRepository, LessonReleaseRepository lessonReleaseRepository, DikiAudio dikiAudio) {
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorLessonRepository = etutorLessonRepository;
    this.etutorGrammarRepository = etutorGrammarRepository;
    this.lessonReleaseRepository = lessonReleaseRepository;
    this.dikiAudio = dikiAudio;
  }

  public void insertCourses() {
    for (String courseLink : courseLinks) {
      EtutorCourse etutorCourse = EtutorCourse.builder()
          .name(courseLink.substring(courseLink.lastIndexOf("/")))
          .href(courseLink)
          .build();
      etutorCourseRepository.save(etutorCourse);
    }
  }

  public void insertLessons() {

    List<EtutorCourse> courses = etutorCourseRepository.findAll();

    System.setProperty("webdriver.chrome.driver", "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver_83.exe");
    WebDriver driver = new ChromeDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    Cookie cookie = new Cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej");

    driver.get("https://www.etutor.pl/");
    driver.manage().addCookie(cookie);

    for (EtutorCourse etutorCourse : courses) {
      driver.get(etutorCourse.getHref());
      wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonsList")));
      WebElement lessonsListWebElement = driver.findElement(By.className("lessonsList"));
      List<WebElement> lessonWebElements = lessonsListWebElement.findElements(By.tagName("li"));

      List<String> lessonHrefs = lessonWebElements.stream()
          .map(lessonWebElement -> lessonWebElement.findElement(By.tagName("div")).getAttribute("href"))
          .collect(Collectors.toList());

      for (String lessonHref : lessonHrefs) {
        driver.get("https://www.etutor.pl" + lessonHref);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonColumnLeft")));

        List<WebElement> singleLessonWebElements = driver.findElements(By.className("singleLessonFrame"));
        for (WebElement singleLessonWebElement : singleLessonWebElements) {

          WebElement singleLesson = singleLessonWebElement.findElement(By.className("singleLessonTitle"));

          EtutorLesson etutorLesson = EtutorLesson.builder()
              .lessonHref(singleLesson.getAttribute("href"))
              .level(etutorCourse.getHref())
              .isReady(false)
              .lessonName(singleLesson.getText())
              .isExercise(false)
              .course(etutorCourse)
              .build();

          etutorLessonRepository.save(etutorLesson);
          log.info(etutorLesson.toString());
        }
      }

    }
  }

  public void webScrapLessons() {

    List<EtutorLesson> etutorLessons = etutorLessonRepository.findAll();
    etutorLessons = etutorLessons.stream()
        .filter(etutorLesson -> !etutorLesson.isReady())
        .collect(Collectors.toList());

    System.setProperty("webdriver.chrome.driver", "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver_83.exe");
    WebDriver driver = new ChromeDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    Cookie cookie = new Cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej");

    driver.get("https://www.etutor.pl/");
    driver.manage().addCookie(cookie);

    for (EtutorLesson etutorLesson : etutorLessons) {
      try {
        log.info("Start scrapping lesson {}", etutorLesson.getLessonHref());
        List<EtutorGrammarContainer> grammarContainers = new ArrayList<>();

        driver.get(etutorLesson.getLessonHref());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonPagesList")));

        WebElement lessonPagesList = driver.findElement(By.className("lessonPagesList"));
        List<WebElement> exercises = lessonPagesList.findElements(By.tagName("li"));

        List<String> hrefExercises = exercises.stream()
            .map(webElement -> webElement.findElement(By.tagName("a")).getAttribute("href"))
            .collect(Collectors.toList());

        for (String hrefExercise : hrefExercises) {
          // log.info("Start scrapping exercise {}", hrefExercise);
          driver.get(hrefExercise);
//          driver.get("https://www.etutor.pl/lessons/en/a1/1/2/37205");

          try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
          } catch (Exception e) {
            try {
              if (driver.findElement(By.id("comicBookContainer")).isDisplayed()) {
                continue;
              }
            } catch (Exception ex) {
            }
            try {
              if (driver.findElement(By.className("lessonhtml")).isDisplayed() &&
                  driver.findElement(By.className("lessonhtml")).getAttribute("data-exercise-title").contains("ćwiczenie wymowy")) {
                continue;
              }
            } catch (Exception exc) {
            }
          }

          WebElement exercise = driver.findElement(By.className("exercise"));

          switch (exercise.getAttribute("data-exercise")) {
            case "dialogue":
              grammarContainers.add(webScrapDialogue(driver));
              break;
            case "word-list":
              driver.get(hrefExercise + "?forceBrowsingMode=1");
              grammarContainers.add(webScrapWordList(hrefExercise));
              break;
            case "note":
            case "writing":
              grammarContainers.add(webScrapNote(driver));
              break;
            case "pictures-listening":
            case "pictures-choice":
            case "pictures-masked-writing":
            case "masked-writing":
            case "video":
              // I guess it contains only words showed earlier
              break;
            case "choice":
              grammarContainers.add(webScrapChoice(hrefExercise));
              break;
            case "reading":
              grammarContainers.add(webScrapReading(driver));
          }
          sleep(2000);
        }

        for (EtutorGrammarContainer etutorGrammarContainer : grammarContainers) {

          if (!etutorGrammarContainer.getDialogue().isEmpty()) {
            for (Pair<String, String> dialogueRow : etutorGrammarContainer.getDialogue()) {
              EtutorGrammar etutorGrammar = EtutorGrammar.builder()
                  .grammarType("DIALOGUE")
                  .dialogueLeft(dialogueRow.getLeft())
                  .dialogueRight(dialogueRow.getRight())
                  .lesson(etutorLesson)
                  .build();
              etutorGrammarRepository.save(etutorGrammar);
            }
          } else if (!etutorGrammarContainer.getChoices().isEmpty()) {
            for (EtutorChoiceItem choiceItem : etutorGrammarContainer.getChoices()) {
              EtutorGrammar etutorGrammar = EtutorGrammar.builder()
                  .grammarType("CHOICE")
                  .question(choiceItem.getQuestion())
                  .correctAnswer(choiceItem.getCorrectAnswer())
                  .correctAnswerAfterChoice(choiceItem.getCorrectAnswerAfterChoice())
                  .firstPossibleAnswer(choiceItem.getPossibleAnswers().size() > 0 ? choiceItem.getPossibleAnswers().get(0) : "")
                  .secondPossibleAnswer(choiceItem.getPossibleAnswers().size() > 1 ? choiceItem.getPossibleAnswers().get(1) : "")
                  .thirdPossibleAnswer(choiceItem.getPossibleAnswers().size() > 2 ? choiceItem.getPossibleAnswers().get(2) : "")
                  .forthPossibleAnswer(choiceItem.getPossibleAnswers().size() > 3 ? choiceItem.getPossibleAnswers().get(3) : "")
                  .lesson(etutorLesson)
                  .build();
              etutorGrammarRepository.save(etutorGrammar);
            }
          } else if (!etutorGrammarContainer.getWordList().isEmpty()) {
            for (Pair<String, String> wordList : etutorGrammarContainer.getWordList()) {
              EtutorGrammar etutorGrammar = EtutorGrammar.builder()
                  .grammarType("WORD")
                  .wordListLeft(wordList.getLeft())
                  .wordListRight(wordList.getRight())
                  .lesson(etutorLesson)
                  .build();
              etutorGrammarRepository.save(etutorGrammar);
            }
          } else if (!etutorGrammarContainer.getNotes().isEmpty()) {
            for (String note : etutorGrammarContainer.getNotes()) {
              EtutorGrammar etutorGrammar = EtutorGrammar.builder()
                  .grammarType("NOTE")
                  .note(note)
                  .lesson(etutorLesson)
                  .build();
              etutorGrammarRepository.save(etutorGrammar);
            }
          }
        }
        etutorLesson.setReady(true);
        etutorLessonRepository.save(etutorLesson);
        log.info("Finish scrapping lesson {}", etutorLesson.getLessonHref());
        sleep(2000);
      } catch (Exception e) {
        log.error("Exception during scrapping lesson {}", etutorLesson.getLessonHref(), e);
      }
    }
    driver.quit();
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private EtutorGrammarContainer webScrapDialogue(WebDriver driver) {
    EtutorGrammarContainer container = new EtutorGrammarContainer();

    List<WebElement> dialogueRowWebElements = driver.findElements(By.className("dialogueRow"));
    for (WebElement dialogueRowWebElement : dialogueRowWebElements) {
      String foreignTranscription = dialogueRowWebElement.findElement(By.className("foreignTranscription")).getText();
      String nativeTranscription = dialogueRowWebElement.findElement(By.className("nativeTranscription")).getText();

      container.getDialogue().add(Pair.of(foreignTranscription, nativeTranscription));
    }
    // log.info(container.getDialogue().toString());
    return container;
  }

  private EtutorGrammarContainer webScrapWordList(String href) throws IOException {
    try {
      EtutorGrammarContainer container = new EtutorGrammarContainer();

      // https://www.etutor.pl/lessons/en/a1/1/2/37205
      // used Jsoup, because selenium don't see elements invisible for user
      Document document = Jsoup.connect(href + "?forceBrowsingMode=1")
          .timeout(10000)
          .cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej")
          .get();

      List<Element> wordListElement = document.select("div.wordListElementScreen");
      List<Element> wordListExercise = document.select("div.wordListExerciseScreen");

      if (wordListElement.size() == wordListExercise.size()) {
        for (int i = 0; i < wordListElement.size(); i++) {

          StringBuilder left = new StringBuilder(StringUtils.isNotEmpty(wordListElement.get(i).select("div.phraseEntity").text().trim())
              ? wordListElement.get(i).select("div.phraseEntity").text().trim()
              : wordListElement.get(i).select("p.phraseEntity").text().trim());
          StringBuilder right = new StringBuilder(wordListExercise.get(i).select("div.masked-writing-command").text().trim());

          List<Element> sentences = wordListElement.get(i).select("ul.sentencesul").select("li");
          for (Element sentence : sentences) {
            String pl = sentence.select("span.sentenceTranslation").text();
            String en = sentence.text().replace(pl, "").trim();
            left.append(" [").append(en).append("]");
            right.append(" [(]").append(pl).append("]");
          }

          container.getWordList().add(Pair.of(left.toString(), right.toString()));
        }
      }
      // log.info(container.getWordList().toString());
      return container;
    } catch (IOException io) {
      log.error("Exception during scrapping word list: {}", href, io);
      throw new IOException(io);
    }
  }

  private EtutorGrammarContainer webScrapNote(WebDriver driver) {
    EtutorGrammarContainer container = new EtutorGrammarContainer();

    String lessonDescription = driver.findElement(By.className("component")).getText();
    for (int i = 0; i < (lessonDescription.length() / 2000 + 1); i++) {
      String note = lessonDescription.substring(i * 2000, (i + 1) * 2000 >= lessonDescription.length() ? lessonDescription.length() : (i + 1) * 2000);
      container.getNotes().add(note);
    }
    // log.info(lessonDescription);
    return container;
  }

  private EtutorGrammarContainer webScrapChoice(String href) throws IOException {
    try {
      EtutorGrammarContainer container = new EtutorGrammarContainer();

      // Document document = Jsoup.connect("https://www.etutor.pl/lessons/en/business-b1/1/3/47991")
      Document document = Jsoup.connect(href)
          .timeout(10000)
          .cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej")
          .get();

      // iterable by questions ex. 1/5
      Elements questions = document.select("div.exercise-numbered-question");
      for (Element questionElement : questions) {
        EtutorChoiceItem etutorChoiceItem = EtutorChoiceItem.builder()
            .question(questionElement.select("div.examChoiceQuestion").text())
            .correctAnswer(questionElement.select("div.component").attr("data-correct-answer"))
            .possibleAnswers(questionElement.select("div.examChoiceOptionBox").stream()
                .map(Element::text)
                .collect(Collectors.toList()))
            .build();

        // iterable by possible answers and insert correct answer to question
        for (Element element : questions.select("div.examChoiceOptionBox")) {
          if (element.select("input.examradioinput").attr("value")
              .equals(questionElement.select("div.component").attr("data-correct-answer"))) {
            try {
              if (!questionElement.select("div.examChoiceQuestion").get(0).select("span.selectedAnswerBox").isEmpty()) {
                questionElement.select("div.examChoiceQuestion").get(0)
                    .select("span.selectedAnswerBox").get(0).text(etutorChoiceItem.getCorrectAnswer());
                String correctAnswerAfterChoice = questionElement.select("div.examChoiceQuestion").get(0).text();
                etutorChoiceItem.setCorrectAnswerAfterChoice(correctAnswerAfterChoice);
                break;
              }
            } catch (Exception e) {
              etutorChoiceItem.setCorrectAnswerAfterChoice(etutorChoiceItem.getCorrectAnswer());
            }
          }
        }
        container.getChoices().add(etutorChoiceItem);
      }
      // log.info(container.getChoices().toString());
      return container;
    } catch (IOException i) {
      log.error("Exception during scrapping choice: {}", href, i);
      throw new IOException(i);
    }

  }

  private EtutorGrammarContainer webScrapReading(WebDriver driver) {
    EtutorGrammarContainer container = new EtutorGrammarContainer();

    String enText = driver.findElement(By.className("readingExerciseForeignText")).getText();
    String plText = driver.findElement(By.className("readingExerciseNativeText")).getText();

    extractReadingText(container, enText, plText);
    // log.info(container.getDialogue().toString());
    return container;
  }

  private void extractReadingText(EtutorGrammarContainer container, String enText, String plText) {
    if (enText.length() > plText.length()) {
      for (int i = 0; i < (enText.length() / MAX_COLUMN_SIZE + 1); i++) {
        String left = enText.substring(i * MAX_COLUMN_SIZE, (i + 1) * MAX_COLUMN_SIZE >= enText.length() ? enText.length() : (i + 1) * MAX_COLUMN_SIZE);
        String right = "";
        if (i * MAX_COLUMN_SIZE <= plText.length()) {
          right = plText.substring(i * MAX_COLUMN_SIZE, (i + 1) * MAX_COLUMN_SIZE >= plText.length() ? plText.length() : (i + 1) * MAX_COLUMN_SIZE);
        }
        container.getDialogue().add(Pair.of(left, right));
      }
    } else {
      for (int i = 0; i < (plText.length() / MAX_COLUMN_SIZE + 1); i++) {
        String right = plText.substring(i * MAX_COLUMN_SIZE, (i + 1) * MAX_COLUMN_SIZE >= plText.length() ? plText.length() : (i + 1) * MAX_COLUMN_SIZE);
        String left = "";
        if (i * MAX_COLUMN_SIZE <= enText.length()) {
          left = enText.substring(i * MAX_COLUMN_SIZE, (i + 1) * MAX_COLUMN_SIZE >= enText.length() ? enText.length() : (i + 1) * MAX_COLUMN_SIZE);
        }
        container.getDialogue().add(Pair.of(left, right));
      }
    }
  }

  public void webScrapLessonAudioAndGrammar() {

//    List<EtutorLesson> etutorLessons = etutorLessonRepository.findAll();
//    etutorLessons = etutorLessons.stream()
//        .filter(etutorLesson -> !etutorLesson.isReady())
//        .collect(Collectors.toList());

    List<LessonRelease> lessonReleases = lessonReleaseRepository.findAll()
        .stream()
        .filter(lesson -> !lesson.getIsReady())
        .collect(Collectors.toList());

    System.setProperty("webdriver.chrome.driver", "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver_83.exe");
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("chromedriver --whitelisted-ips=\"\"");
//    .setProperty("chromedriver --whitelisted-ips", "");
    WebDriver driver = new ChromeDriver(chromeOptions);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
    Cookie cookie = new Cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej");

    driver.get("https://www.etutor.pl/");
    driver.manage().addCookie(cookie);

    for (LessonRelease etutorLesson : lessonReleases) {
      try {
        String courseFolderName = etutorLesson.getCourse().getEnName().replaceAll("/", "");
        new File(AUDIO_FOLDER + courseFolderName).mkdir();
        String lessonFolderName = etutorLesson.getEnName()
            .replace("...", "")
            .replace("?", "")
            .replace(":", "")
            .replaceAll("\"", "")
            .replaceAll("/", "")
            .replaceAll("\\\\", "")
            .replace("!", "").trim();
        new File(AUDIO_FOLDER + courseFolderName + "\\" + lessonFolderName).mkdirs();

        log.info("Start scrapping lesson {}", etutorLesson.getHref());
        List<EtutorGrammarContainer> grammarContainers = new ArrayList<>();

        driver.get(etutorLesson.getHref());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("lessonPagesList")));

        WebElement lessonPagesList = driver.findElement(By.className("lessonPagesList"));
        List<WebElement> exercises = lessonPagesList.findElements(By.tagName("li"));

        List<String> hrefExercises = exercises.stream()
            .map(webElement -> webElement.findElement(By.tagName("a")).getAttribute("href"))
            .collect(Collectors.toList());

        List<String> exerciseNames = exercises.stream().map(WebElement::getText).collect(Collectors.toList());

        for (String hrefExercise : hrefExercises) {
          // log.info("Start scrapping exercise {}", hrefExercise);
          // String exerciseName = exercises.get(hrefExercises.indexOf(hrefExercise)).getText();

          driver.get(hrefExercise +  "?forceBrowsingMode=1");
//          driver.get("https://www.etutor.pl/lessons/en/a1/1/2/37205");

          try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("exercise")));
          } catch (Exception e) {
            try {
              if (driver.findElement(By.id("comicBookContainer")).isDisplayed()) {
                continue;
              }
            } catch (Exception ex) {
            }
            try {
              if (driver.findElement(By.className("lessonhtml")).isDisplayed() &&
                  driver.findElement(By.className("lessonhtml")).getAttribute("data-exercise-title").contains("ćwiczenie wymowy")) {
                continue;
              }
            } catch (Exception exc) {
            }
          }

          WebElement exercise = driver.findElement(By.className("exercise"));

          switch (exercise.getAttribute("data-exercise")) {
            case "dialogue":
              // grammarContainers.add(webScrapDialogue(driver));
              break;
            case "word-list":
              driver.get(hrefExercise + "?forceBrowsingMode=1");
//              grammarContainers.add(webScrapWordList(hrefExercise));

              Document lessonContent = Jsoup.connect(hrefExercise + "?forceBrowsingMode=1")
                  .cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej")
                  .timeout(10000).get();
              Elements audios = lessonContent.select("span.audioIcon");
              log.info("LESSON CONTAINS {} AUDIOS BUT ONLY {} ARE UNIQUE", audios.size(), audios.stream().map(element -> element.attr("data-audio-url")).distinct().count());
              for (Element audio : audios) {
                String audioUrl = audio.attr("data-audio-url");
                String audioName = audioUrl.contains("en-ame")
                    ? "us_" + audioUrl.substring(audioUrl.lastIndexOf("/") + 1, audioUrl.lastIndexOf("."))
                    : "en_" + audioUrl.substring(audioUrl.lastIndexOf("/") + 1, audioUrl.lastIndexOf("."));
                dikiAudio.downloadAudio(new URL("https://www.diki.pl/" + audioUrl), AUDIO_FOLDER + courseFolderName + "\\" + lessonFolderName, audioName);
              }

              break;
            case "note":
            case "writing":
              // grammarContainers.add(webScrapNote(driver));
              break;
            case "pictures-listening":
            case "pictures-choice":
            case "pictures-masked-writing":
            case "masked-writing":
            case "video":
              // I guess it contains only words showed earlier
              break;
            case "choice":
              // grammarContainers.add(webScrapChoice(hrefExercise));

              // TODO chwilowo zakomentowane, zeby pobrac tylko audio
//              List<EtutorGrammar> etutorGrammars = etutorGrammarRepository.findAllByLesson_Id(etutorLesson.getId())
//                  .stream()
//                  .filter(etutorGrammar -> etutorGrammar.getGrammarType().equals("CHOICE"))
//                  .collect(Collectors.toList());
//
//              Document document = Jsoup.connect(hrefExercise)
//                  .timeout(10000)
//                  .cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej")
//                  .get();
//
//              Elements elements = document.select("div.exercise-numbered-question");
//              for (Element element : elements) {
//
//                String question = element.select("div.examChoiceQuestion").text();
//                String answer = element.select("div.component").attr("data-correct-answer");
//                String description = element.select("div.immediateExplanationTranslationContainer").text();
//
//                EtutorGrammar grammar = etutorGrammars.stream()
//                    .filter(etutorGrammar -> etutorGrammar.getQuestion().equals(question) &&
//                        etutorGrammar.getCorrectAnswer().equals(answer))
//                    .findFirst()
//                    .orElseThrow(IllegalStateException::new);
//
//                grammar.setTranslation(description);
//
//                grammar.setName(exerciseNames.get(hrefExercises.indexOf(hrefExercise)));
//                etutorGrammarRepository.save(grammar);

//              }
              break;
            case "reading":
              // grammarContainers.add(webScrapReading(driver));
          }
          sleep(2000);
        }

        etutorLesson.setIsReady(true);
        lessonReleaseRepository.save(etutorLesson);
        log.info("Finish scrapping lesson {}", etutorLesson.getHref());
        sleep(2000);
      } catch (Exception e) {
        log.error("Exception during scrapping lesson {}", etutorLesson.getHref(), e);
      }
    }
    driver.quit();
  }



}
