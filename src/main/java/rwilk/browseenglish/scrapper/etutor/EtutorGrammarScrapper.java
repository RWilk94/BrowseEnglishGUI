package rwilk.browseenglish.scrapper.etutor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import rwilk.browseenglish.model.etutor.EtutorCourse;
import rwilk.browseenglish.model.etutor.EtutorLesson;
import rwilk.browseenglish.repository.EtutorCourseRepository;
import rwilk.browseenglish.repository.EtutorLessonRepository;
import rwilk.browseenglish.scrapper.etutor.model.EtutorChoiceItem;
import rwilk.browseenglish.scrapper.etutor.model.EtutorGrammarContainer;
import rwilk.browseenglish.scrapper.etutor.model.EtutorMaskedWritingItem;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

/**
 * Etutor grammar scrapper
 */
@Slf4j
@Component
public class EtutorGrammarScrapper {

  private static boolean exit = false;
  private final EtutorCourseRepository etutorCourseRepository;
  private final EtutorLessonRepository etutorLessonRepository;

  public EtutorGrammarScrapper(EtutorCourseRepository etutorCourseRepository,
      EtutorLessonRepository etutorLessonRepository) {
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorLessonRepository = etutorLessonRepository;
  }

  //  public static void webScrap() {
  //    FirefoxDriver driver = new FirefoxDriver();
  //    WebDriverWait wait = new WebDriverWait(driver, 15);
  //    driver.navigate().to("https://www.etutor.pl/grammar");
  //  }

  public static void main(String[] args) {
    EtutorGrammarScrapper scrapper = new EtutorGrammarScrapper(null, null);
    scrapper.openAllElements();
    // webScrapLessonTitles();
  }

  /**
   * Fajna metodka do webscrapowania gramatyki eTutora.
   */
  public void openAllElements() {
    System.setProperty("webdriver.chrome.driver", "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver.exe");
    WebDriver driver = new ChromeDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6).getSeconds());
    Cookie cookie = new Cookie("autoLoginToken", "7Gzy1dCKErpdyBhzvc14Xt6uBoZMFUso0LwclUan");

    try {
      driver.get("https://www.etutor.pl/");
      driver.manage().addCookie(cookie);

      for (int i = 20; i < 34/*34*/; i++) {
        driver.get("https://www.etutor.pl/grammar");
        List<WebElement> courses = driver.findElements(By.className("rootGrammarTag"));
        // for (WebElement course : courses) {
        // open root element
        WebElement course = courses.get(i);
        log.info("Course name = {}", course.getText());
        WebElement courseTitle = course.findElement(By.className("tagTitle"));
        courseTitle.click();
        EtutorCourse etutorCourse = EtutorCourse.builder().name(course.getText()).build();
        List<EtutorLesson> etutorLessons = new ArrayList<>();
        // get all child elements
        try {
          wait.until(presenceOfElementLocated(By.className("childGrammarTag")));
        } catch (TimeoutException ex) {
          wait.until(presenceOfElementLocated(By.className("screen_grammar")));
        }

        List<WebElement> screenGrammarTags = course.findElements(By.className("screen_grammar"));
        for (WebElement screenGrammar : screenGrammarTags) {
          log.info("Lesson name = {} {}", screenGrammar.getText(),
              screenGrammar.findElement(By.className("lessonLink")).getAttribute("href"));
          etutorLessons.add(EtutorLesson.builder()
              .lessonName(screenGrammar.getText().substring(3).trim())
              .lessonHref(screenGrammar.findElement(By.className("lessonLink")).getAttribute("href"))
              .level(screenGrammar.getText().substring(0, 3))
              .isExercise(false)
              .build());
        }

        List<WebElement> childGrammarTags = course.findElements(By.className("childGrammarTag"));
        for (WebElement childGrammarTag : childGrammarTags) {
          // log.info("Expanding lesson name = {}", childGrammarTag.getText());
          WebElement webElement = childGrammarTag.findElement(By.className("tagTitle"));
          Actions actions = new Actions(driver);
          actions.moveToElement(webElement);
          actions.perform();
          webElement.click();

          try {
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(childGrammarTag, By.className("childGrammarTag")));
          } catch (TimeoutException ex) {
            try {
              wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(childGrammarTag, By.className("screen_grammar")));
            } catch (TimeoutException exc) {
            }
          }

          List<WebElement> childGrammarTags1 = childGrammarTag.findElements(By.className("childGrammarTag"));
          for (WebElement childGrammarTag1 : childGrammarTags1) {
            log.info("Child Lesson name = {}", childGrammarTag1.getText());
            WebElement webElement1 = childGrammarTag1.findElement(By.className("tagTitle"));
            Actions actions1 = new Actions(driver);
            actions1.moveToElement(webElement1);
            actions1.perform();
            webElement1.click();
          }

          List<WebElement> screenGrammars = childGrammarTag.findElements(By.className("screen_grammar"));
          for (WebElement screenGrammar : screenGrammars) {
            log.info("Grammar name = {} {}", screenGrammar.getText(),
                screenGrammar.findElement(By.className("lessonLink")).getAttribute("href"));
            etutorLessons.add(EtutorLesson.builder()
                .lessonName(screenGrammar.getText().substring(3).trim())
                .lessonHref(screenGrammar.findElement(By.className("lessonLink")).getAttribute("href"))
                .level(screenGrammar.getText().substring(0, 3))
                .isExercise(false)
                .build());
          }
        }

        List<WebElement> grammarExerciseListContainer = course.findElements(By.className("grammarExerciseListContainer"));
        for (WebElement grammarExercise : grammarExerciseListContainer) {
          // log.info("Exercise name = {}", grammarExercise.getText());
          WebElement webElement = grammarExercise.findElement(By.tagName("a"));
          Actions actions = new Actions(driver);
          actions.moveToElement(courseTitle);
          actions.perform();
          webElement.click();

          wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(grammarExercise, By.className("lessonLink")));
          List<WebElement> lessonLinks = grammarExercise.findElements(By.className("lessonLink"));
          for (WebElement lessonLink : lessonLinks) {
            log.info("Exercise name = {} {}", lessonLink.getText(), lessonLink.getAttribute("href"));
            etutorLessons.add(EtutorLesson.builder()
                .lessonName(lessonLink.getText().substring(3).trim())
                .lessonHref(lessonLink.getAttribute("href"))
                .level(lessonLink.getText().substring(0, 3))
                .isExercise(false)
                .build());
          }

          try {
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(grammarExercise, By.className("lessonLink")));
          } catch (TimeoutException ex) {
          }

        }
        etutorCourse = etutorCourseRepository.save(etutorCourse);
        for (EtutorLesson lesson : etutorLessons) {
          lesson.setCourse(etutorCourse);
          etutorLessonRepository.save(lesson);
        }

        Actions actions = new Actions(driver);
        actions.moveToElement(courseTitle);
        actions.perform();
        courseTitle.click();
      }

      // }
    } catch (Exception e) {
      // e.printStackTrace();
    } finally {
      driver.quit();
    }

  }

  //  public void webScrapLessonTitles() {
  //    System.setProperty("webdriver.chrome.driver", "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver.exe");
  //    WebDriver driver = new ChromeDriver();
  //    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10).getSeconds());
  //    Cookie cookie = new Cookie("autoLoginToken", "D2SSahlLmGdxqPlQUNAlLT7P5hapk9idUeyyvYdQ");
  //
  //    try {
  //      driver.get("https://www.etutor.pl/");
  //      driver.manage().addCookie(cookie);
  //      driver.get("https://www.etutor.pl/grammar");
  //
  //      List<WebElement> courses = driver.findElements(By.className("grammarTagHeader"));
  //      for (int i = 0; i < courses.size(); i++) {
  //        WebElement course = courses.get(i);
  //        // print course name
  //        // System.out.println(course.getText());
  //        EtutorCourse etutorCourse = new EtutorCourse(course.getText());
  //        // open element
  //        WebElement courseTitle = course.findElement(By.className("tagTitle"));
  //        courseTitle.click();
  //        wait.until(presenceOfElementLocated(By.className("screen_grammar")));
  //        // iterable on every element of additional bookmarks
  //        List<WebElement> grammarTagHeaderWebElements = course.findElements(By.className("grammarTagHeader"));
  //        for (WebElement grammarTagHeaderWebElement : grammarTagHeaderWebElements) {
  //          grammarTagHeaderWebElement.findElement(By.className("tagTitle")).click();
  //        }
  //        Thread.sleep(1000);
  //
  //        List<WebElement> showExercises = course.findElements(By.className("grammarExerciseListContainer"));
  //        for (WebElement showExercise : showExercises) {
  //          showExercise.findElement(By.tagName("a")).click();
  //        }
  //        Thread.sleep(1000);
  //
  //        List<WebElement> lessonLinkWebElements = course.findElements(By.className("lessonLink"));
  //        for (WebElement lessonLink : lessonLinkWebElements) {
  //          // System.out.println(lessonLink.getText() + " " + lessonLink.getAttribute("href"));
  //
  //          String text = !lessonLink.getText().isEmpty() ? lessonLink.getText().substring(2).trim() : "";
  //          String level = !lessonLink.getText().isEmpty() ? lessonLink.getText().substring(0, 2).trim() : "";
  //
  //          if (lessonLink.findElement(By.xpath("./..")).getTagName().equals("ul")) {
  //            etutorCourse.getEtutorLessons().add(EtutorLesson.builder()
  //                .lessonName(text)
  //                .lessonHref(lessonLink.getAttribute("href"))
  //                .level(level)
  //                .isExercise(false)
  //                .build());
  //          } else if (lessonLink.findElement(By.xpath("./..")).getTagName().equals("li")) {
  //            etutorCourse.getEtutorLessons().add(EtutorLesson.builder()
  //                .lessonName(text)
  //                .lessonHref(lessonLink.getAttribute("href"))
  //                .level(level)
  //                .isExercise(true)
  //                .build());
  //          } else {
  //            System.err.println("Error");
  //          }
  //        }
  //        // TODO przelecieć wszystkie kursy i zapisać do bazy danych. Nastęnie klepać webScrap dla każdego.
  //        int elements =
  //            Integer.parseInt(course.getText().substring(course.getText().lastIndexOf("(") + 1, course.getText().lastIndexOf(")")));
  //        System.out.println(elements);
  //        if (etutorCourse.getEtutorLessons().size() == elements) {
  //          System.out.println(etutorCourse.getName());
  //          EtutorCourse c = etutorCourseRepository.save(etutorCourse);
  //          for (EtutorLesson lesson : etutorCourse.getEtutorLessons()) {
  //            lesson.setCourse(c);
  //            etutorLessonRepository.save(lesson);
  //          }
  //          // etutorCourse.toString();
  //        } else {
  //          System.err.println(etutorCourse.getName());
  //          // i--;
  //          // driver.navigate()
  //        }
  //        WebElement element = driver.findElements(By.className("rootGrammarTag")).get(i).findElement(By.className("tagTitle"));
  //        Actions actions = new Actions(driver);
  //        actions.moveToElement(element);
  //        actions.perform();
  //        element.click();
  //        // driver.findElements(By.className("rootGrammarTag")).get(i).findElement(By.className("tagTitle")).click();
  //      }
  //
  //    } catch (Exception e) {
  //      e.printStackTrace();
  //    } finally {
  //      driver.quit();
  //    }
  //
  //  }

  public EtutorGrammarContainer webScrapEtutor(EtutorLesson etutorLesson) {
    EtutorGrammarContainer etutorGrammarContainer = new EtutorGrammarContainer();

    // System.setProperty("webdriver.gecko.driver", "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\driver\\geckodriver.exe");
    System.setProperty("webdriver.chrome.driver", "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver.exe");
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--mute-audio");
    WebDriver driver = new ChromeDriver(chromeOptions);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6).getSeconds());
    Cookie cookie = new Cookie("autoLoginToken", "7Gzy1dCKErpdyBhzvc14Xt6uBoZMFUso0LwclUan");
    try {
      driver.get("https://www.etutor.pl/");
      driver.manage().addCookie(cookie);
      driver.get(etutorLesson.getLessonHref());

      //      List<WebElement> courses = driver.findElements(By.className("grammarTagHeader"));
      //
      //      WebElement course = courses.get(6).findElement(By.className("tagTitle")); // foreach here
      //      course.click(); // otwieranie kursu
      //
      //      /*WebElement screen_grammar = */
      //      wait.until(presenceOfElementLocated(By.className("lessonLink"))); // czekam aż się załaduje zawartość
      //
      //      List<WebElement> lessons = driver.findElements(By.className("lessonLink")); // foreach here // pobieranie wszystkich zawartości
      //      WebElement lessonElement = lessons.get(0);
      //      lessonElement.click(); // otwieranie pierwszego linku

      // label:
      while (driver.findElements(By.className("component")) != null) {
        try {
          wait.until(presenceOfElementLocated(By.className("lessoncontents")));
        } catch (TimeoutException ex) {
          try {
            wait.until(presenceOfElementLocated(By.className("buttonPrimaryStyle")));
          } catch (TimeoutException timeoutException) {
          }
          // ex.printStackTrace();
        }

        if (exit) {
          exit = false;
          break;
        }
        if (isElementPresentByClassName(driver, "exercise")) {
          String dataExercise = driver.findElement(By.className("exercise")).getAttribute("data-exercise");
          switch (dataExercise) {
            case "note":
              webScrapNoteElement(driver, etutorGrammarContainer); // done
              break;
            case "word-list":
              webScrapWordList(driver, etutorGrammarContainer);
              break;
            case "pictures-listening":
              exit = true;
              // webScrapPicturesListening(driver, wait); // nothing interested
              break;
            case "pictures-masked-writing":
              webScrapPicturesMaskedWriting(driver, wait); // nothing interested
              break;
            case "masked-writing":
              webScrapMaskedWriting(driver, wait, etutorGrammarContainer);
              break;
            case "choice":
              webScrapChoice(driver, wait, etutorGrammarContainer); // done
              break;
            case "dialogue":
              webScrapDialogue(driver, etutorGrammarContainer);
              break;
            case "pictures-choice":
              exit = true;
              // webScrapPictureChoice(driver, wait); // nothing interested
              break;
            case "reading":
              webScrapReading(driver);
              break;
            default:
              exit = true;
              break;
          }

        } else if (isElementPresentByClassName(driver, "exercise-done")) {
          driver.findElement(By.id("nextPageButton")).click();
        } else if (isElementPresentByClassName(driver, "finishedLessonCongratulationsH1") || isElementPresentByClassName(driver,
            "finishedLessonCongratulationsH2")) {
          System.err.println("finish");
          break;
        } else if (isElementPresentByClassName(driver, "buttonPrimaryStyle")) {
          driver.findElements(By.className("buttonPrimaryStyle")).get(0).click();
        } else if (isElementPresentById(driver, "comicBookContainer")) {
          exit = true;
        }
      }
    } catch (Exception e) {
      // e.printStackTrace();
    } finally {
      driver.quit();
      return etutorGrammarContainer;
    }
  }

  private void webScrapReading(WebDriver driver) {
    try {
      driver.findElement(By.id("switchToExerciseTestButton")).click();
      exit = true;
    } catch (Exception e) {
      exit = true;
    }
  }

  private void webScrapPictureChoice(WebDriver driver, WebDriverWait wait) {
    try {
      wait.until(presenceOfElementLocated(By.className("picturestable1")));

      WebElement picture = driver.findElement(By.className("picturesGameNonClickableImgDiv"));
      String style = picture.getAttribute("style");
      style = style.substring(style.lastIndexOf("/") + 1, style.lastIndexOf("."));

      List<WebElement> answerTextWebElements = driver.findElements(By.className("answerText"));
      for (WebElement answerText : answerTextWebElements) {
        try {
          String textStyle = answerText.getText();
          // textStyle = textStyle.substring(textStyle.lastIndexOf("/") + 1, textStyle.lastIndexOf("."));
          if (textStyle.equalsIgnoreCase(style) || textStyle.replaceAll("[^a-zA-Z0-9]", "")
              .equalsIgnoreCase(style.replaceAll("[^a-zA-Z0-9]", ""))) {
            answerText.click();
          }
        } catch (Exception e) {
          // e.printStackTrace();
        }
      }
    } catch (Exception e) {
    }
  }

  private void webScrapDialogue(WebDriver driver, EtutorGrammarContainer etutorGrammarContainer) {
    List<WebElement> dialogueRowWebElements = driver.findElements(By.className("dialogueRow"));
    for (WebElement dialogueRowWebElement : dialogueRowWebElements) {
      String foreignTranscription = dialogueRowWebElement.findElement(By.className("foreignTranscription")).getText();
      String nativeTranscription = dialogueRowWebElement.findElement(By.className("nativeTranscription")).getText();

      etutorGrammarContainer.getDialogue().add(Pair.of(foreignTranscription, nativeTranscription));
      // System.out.println(foreignTranscription);
      // System.out.println(nativeTranscription);
    }
    exit = true;
  }

  private void webScrapChoice(WebDriver driver, WebDriverWait wait, EtutorGrammarContainer etutorGrammarContainer) {
    List<WebElement> componentWebElements = driver.findElements(By.className("component"));
    for (WebElement componentWebElement : componentWebElements) {
      // sprawdzamy ktora odpowiedz jest poprawna
      String correctAnswer = componentWebElement.getAttribute("data-correct-answer");
      List<WebElement> examRadioInputs = componentWebElement.findElements(By.className("examradioinput"));
      List<WebElement> examChoiceOptionBox = componentWebElement.findElements(By.className("examChoiceOptionBox"));

      etutorGrammarContainer.getChoices().add(
          EtutorChoiceItem.builder()
              .question(componentWebElement.findElement(By.className("examChoiceQuestion")).getText())
              .correctAnswer(correctAnswer)
              .possibleAnswers(examChoiceOptionBox.stream().map(WebElement::getText).collect(Collectors.toList()))
              .build());

      //      etutorGrammar.getChoices().add(Triple.of(
      //          componentWebElement.findElement(By.className("examChoiceQuestion")).getText(), /* question*/
      //          correctAnswer,
      //          examChoiceOptionBox.stream().map(WebElement::getText).collect(Collectors.toList())
      //      ));

      // w petli po odpowiedziach - zaznaczam poprawna odpowiedz
      for (int i = 0; i < examRadioInputs.size(); i++) {
        if (examRadioInputs.get(i).getAttribute("value").equals(correctAnswer)) {
          examChoiceOptionBox.get(i).click();
          etutorGrammarContainer.getChoices().get(etutorGrammarContainer.getChoices().size() - 1)
              .setCorrectAnswerAfterChoice(componentWebElement.findElement(By.className("examChoiceQuestion")).getText());
          break;
        }
      }
      // klikamy przycisk do zaznaczenie odpowiedzi
      wait.until(presenceOfElementLocated(By.id("solveQuestionButton")));
      List<WebElement> solveQuestionButtons = driver.findElements(By.id("solveQuestionButton"));
      for (WebElement button : solveQuestionButtons) {
        // klikamy odpowiedz
        if (!button.getAttribute("class").contains("hidden")) {
          button.click();
          break;
        }
      }
      wait.until(presenceOfElementLocated(By.id("nextQuestionButton")));
      List<WebElement> nextQuestionButtons = driver.findElements(By.id("nextQuestionButton"));
      for (WebElement nextQuestionButton : nextQuestionButtons) {
        // klikamy odpowiedz
        if (!nextQuestionButton.getAttribute("class").contains("hidden")) {
          nextQuestionButton.click();
          break;
        }
      }

    }
    wait.until(presenceOfElementLocated(By.id("nextExerciseButton")));
    driver.findElement(By.id("nextExerciseButton")).click();
  }

  private void webScrapMaskedWriting(WebDriver driver, WebDriverWait wait, EtutorGrammarContainer etutorGrammarContainer) {
    // przeklikuje literki
    do {
      if (isElementPresentById(driver, "suggestNextLetterButton") && !driver.findElement(By.id("suggestNextLetterButton"))
          .getAttribute("class").contains("hidden")) {
        try {
          driver.findElement(By.id("suggestNextLetterButton")).click();
        } catch (Exception ex) {
        }
      } else {
        break;
      }
    } while (isElementPresentById(driver, "suggestNextLetterButton") && !driver.findElement(By.id("suggestNextLetterButton"))
        .getAttribute("class").contains("hidden"));

    etutorGrammarContainer.getMaskedWriting().add(
        EtutorMaskedWritingItem.builder()
            .question(driver.findElements(By.className("masked-writing-command")).stream().filter(item -> !item.getText().isEmpty()).map(
                WebElement::getText).findFirst().orElse("").replaceAll("\n", " "))
            .answer(driver.findElements(By.className("masked-writing-core")).stream().filter(item -> !item.getText().isEmpty()).map(
                    WebElement::getText).findFirst().orElse("").replaceAll("\n", " "))
            .translation(driver.findElements(By.className("masked-writing-explonation")).stream().filter(item -> !item.getText().isEmpty()).map(
                    WebElement::getText).findFirst().orElse("").replaceAll("\n", " "))
            .build()
    );

    wait.until(presenceOfElementLocated(By.id("nextExerciseButton")));
    // klika przycisk następny przykład albo koniec ćwiczenia
    while (true) {
      if (driver.findElement(By.id("nextExerciseButton")).getAttribute("class").contains("hidden")
          && driver.findElement(By.id("nextQuestionButton")).getAttribute("class").contains("hidden")) {
      } else {
        if (isElementPresentById(driver, "nextExerciseButton") && !driver.findElement(By.id("nextExerciseButton"))
            .getAttribute("class").contains("hidden")) {
          driver.findElement(By.id("nextExerciseButton")).click();
        } else if (isElementPresentById(driver, "nextQuestionButton") && !driver.findElement(By.id("nextQuestionButton"))
            .getAttribute("class").contains("hidden")) {
          driver.findElement(By.id("nextQuestionButton")).click();
        }
        break;
      }
    }
  }

  private void webScrapNoteElement(WebDriver driver, EtutorGrammarContainer etutorGrammarContainer) {
    // print description
    String lessonDescription = driver.findElement(By.className("component")).getText();
    etutorGrammarContainer.getNotes().add(lessonDescription);
    // System.out.println(lessonDescription);
    // next page
    WebElement nextPageButton = driver.findElement(By.id("nextPageButton")); // button next page
    nextPageButton.click();
  }

  private void webScrapWordList(WebDriver driver, EtutorGrammarContainer etutorGrammarContainer) throws InterruptedException {
    List<WebElement> exercises = driver.findElements(By.className("wordListElementScreen"));
    // TODO print desc
    AtomicInteger atomicInteger = new AtomicInteger(0);
    while (isElementPresentById(driver, "discardFromRepetitions")) {
      String[] strings = exercises.stream().map(WebElement::getText).filter(text -> !text.isEmpty()).findFirst().orElse("").split("\n");
      if (strings.length >= 2) {
        etutorGrammarContainer.getWordList().add(Pair.of(strings[0], strings[1]));
      }

      if (atomicInteger.get() % 5 == 0) {
        try {
          driver.findElement(By.id("discardFromRepetitions")).click();
        } catch (Exception e) {
          catchException(driver, e);
        }
      } else if (atomicInteger.get() % 5 == 1) {
        try {
          driver.findElement(By.id("discardFromRepetitions")).sendKeys(Keys.DELETE);
        } catch (Exception e) {
          catchException(driver, e);
        }
      } else if (atomicInteger.get() % 5 == 2) {
        try {
          driver.findElement(By.id("addToRepetitions")).sendKeys(Keys.ENTER);
        } catch (Exception e) {
          catchException(driver, e);
        }
      } else if (atomicInteger.get() % 5 == 3) {
        try {
          driver.findElement(By.id("skipRepetition")).sendKeys(Keys.DELETE);
          driver.findElement(By.id("skipRepetition")).sendKeys(Keys.ENTER);
        } catch (Exception e) {
          catchException(driver, e);
        }
      } else {
        try {
          driver.findElement(By.id("removeFromRepetitions")).sendKeys(Keys.ENTER);
        } catch (Exception e) {
          catchException(driver, e);
        }
      }
      atomicInteger.getAndIncrement();
      // Thread.sleep(300);
    }
  }

  private void catchException(WebDriver driver, Exception e) {
    // e.printStackTrace();
    try {
      if (isElementPresentById(driver, "suggestNextLetterButton")) {
        driver.findElement(By.id("suggestNextLetterButton")).click();
        driver.findElement(By.id("postMaskedWritingButton")).click();
      }
    } catch (Exception ex) {
      // ex.printStackTrace();
    }
  }

  private void webScrapPicturesListening(WebDriver driver, WebDriverWait wait) {
    wait.until(presenceOfElementLocated(By.className("audioIconButton")));
    String dataAudioUrl = driver.findElement(By.className("audioIconButton")).getAttribute("data-audio-url");

    try {
      dataAudioUrl = dataAudioUrl.substring(dataAudioUrl.lastIndexOf("/") + 1, dataAudioUrl.lastIndexOf("."));
      List<WebElement> pictureWebElements = driver.findElements(By.className("picturesGameClickableImgLink"));
      for (WebElement picture : pictureWebElements) {
        String onclick = picture.getAttribute("style");
        // System.out.println("for " + onclick);
        if (onclick.contains(dataAudioUrl) || onclick.contains(dataAudioUrl.replaceAll("[^a-zA-Z0-9 ]", ""))) {
          // System.out.println("click");
          picture.click();
          break;
        }
      }
    } catch (Exception e) {
      // e.printStackTrace();
    }
  }

  private void webScrapPicturesMaskedWriting(WebDriver driver, WebDriverWait wait) {
    wait.until(presenceOfElementLocated(By.id("suggestNextLetterButton")));
    AtomicInteger atomicInteger = new AtomicInteger();
    while (true) {
      try {
        if (isElementPresentById(driver, "suggestNextLetterButton")) {
          driver.findElement(By.id("suggestNextLetterButton")).click();
        } else {
          break;
        }
      } catch (Exception e) {
        if (atomicInteger.incrementAndGet() >= 1000) {
          break;
        }
        // e.printStackTrace();
      }
    }
  }

  private boolean isElementPresentByClassName(WebDriver driver, String className) {
    try {
      driver.findElement(By.className(className));
      return true;
    } catch (NoSuchElementException ex) {
      return false;
    }
  }

  private boolean isElementPresentById(WebDriver driver, String id) {
    try {
      driver.findElement(By.id(id));
      return true;
    } catch (NoSuchElementException ex) {
      return false;
    }
  }

}
