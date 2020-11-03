package rwilk.browseenglish.scrapper.tatoeba;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.extern.slf4j.Slf4j;
import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

@Slf4j
public class TatoebaScrapper {

  private String language = "&from=eng&to=pol";
  private String languageEnToPl = "&from=eng&to=pol";
  private String languagePlToEn = "&from=pol&to=eng";
  private String languageEnToUn = "&from=eng&to=und";
  private String wholeWord = "%3D";

  //  public static void main(String[] args) {
  //    TatoebaScrapper tatoebaScrapper = new TatoebaScrapper();
  //    tatoebaScrapper.webScrapSelenium("by mistake");
  //  }

  public List<String> webScrap(String englishWord, boolean wholeWordOnly) {
    try {
      // add paging
      Document document =
          Jsoup.connect("https://tatoeba.org/pol/sentences/search?query="
              + (wholeWordOnly ? wholeWord : "")
              + englishWord.replaceAll(" ", "+")
              + "&from=eng&to=pol").timeout(8000).get();
      // search whole words

      Elements mainElement = document.select("div#main_content"); //get main content
      Elements pagesElement = mainElement.select("ul.paging"); // get pages navigation content

      Integer pagesNumber = 1;
      if (!pagesElement.isEmpty()) {
        pagesNumber = pagesElement.get(0).children().stream()
            .map(child -> child.children().text())
            .collect(Collectors.toList())
            .stream()
            .filter(StringUtils::isNumeric)
            .map(Integer::valueOf)
            .max(Integer::compareTo).orElse(1);
      }
      Elements translations = mainElement.select("div.sentence-and-translations"); //get 10 translations

      if (pagesNumber > 1) {
        for (int i = 2; i <= pagesNumber; i++) {
          document =
              Jsoup.connect("https://tatoeba.org/pol/sentences/search?query="
                  + (wholeWordOnly ? wholeWord : "")
                  + englishWord.replaceAll(" ", "+")
                  + "&from=eng&to=pol&page=" + i).timeout(8000).get();
          mainElement = document.select("div#main_content"); //get main content
          translations.addAll(mainElement.select("div.sentence-and-translations"));
        }
      }
      /*translations.get(0).select("div").select("div.text").text(); //get english and polish translations
      translations.get(0).select("div").select("div.sentence").select("div.text").text(); //get english translations
      translations.get(0).select("div").select("div.translation").select("div.text").text(); //get polish translations*/

      List<String> collect = translations.stream()
          .map(translation -> translation.select("div").select("div.sentence").select("div.text").text()
              + "("
              + translation.select("div").select("div.translation").select("div.text").text()
              + ")"
          ).collect(Collectors.toList());

      return collect;
      // sentenceScrapperController.initListViewTranslateSentence(FXCollections.observableArrayList(exampleSentenceList));

    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  public List<Sentence> webScrapSelenium(Word word, String language, String otherName) {
    System.setProperty("webdriver.chrome.driver", "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver.exe");
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.addArguments("--mute-audio");
    chromeOptions.addArguments("--headless");
    java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
     System.setProperty("webdriver.chrome.silentOutput", "true");

    WebDriver driver = new ChromeDriver(chromeOptions);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5).getSeconds());

    List<Sentence> sentences = new ArrayList<>();

    String enNameToFindSentences = (StringUtils.isNotEmpty(otherName) ? otherName : word.getEnName())
        .replaceAll("British English", "")
        .trim()
        .replaceAll("American English", "")
        .trim()
        .replaceAll(" ", "+");

    try {
      driver.get("https://tatoeba.org/pol/sentences/search?query=%3D"
          + enNameToFindSentences
          + "&from=eng&to=" + language);
      wait.until(presenceOfElementLocated(By.className("sentence-and-translations")));

      int numberOfPages = 1;

      try {
        numberOfPages = driver.findElement(By.className("paging")).findElements(By.tagName("li")).stream()
            .map(child -> child.getText())
            .collect(Collectors.toList())
            .stream()
            .filter(StringUtils::isNumeric)
            .map(Integer::valueOf)
            .max(Integer::compareTo).orElse(1);
      } catch (Exception e) {}

      for (int j = 1; j <= numberOfPages; j++) {
        log.info("{} {}/{}", word.getEnName(), j, numberOfPages);
        if (j > 1) {
          driver.get("https://tatoeba.org/pol/sentences/search?query=%3D"
              + enNameToFindSentences
              + "&from=eng&to=" + language + "&page=" + j);
          wait.until(presenceOfElementLocated(By.className("sentence-and-translations")));
        }

        List<WebElement> sentenceWebElements = driver.findElements(By.className("sentence-and-translations"));
        for (WebElement sentenceWebElement : sentenceWebElements) {
          String englishSentence = "";
          List<String> polishSentences = new ArrayList<>();
          for (int i = 0; i < sentenceWebElement.findElements(By.className("text")).size(); i++) {
            if (i == 0) {
              englishSentence = sentenceWebElement.findElements(By.className("text")).get(0).getText();
            } else if (language.equals("pol")) {
              polishSentences.add(sentenceWebElement.findElements(By.className("text")).get(i).getText());
            }
          }
          sentences.add(Sentence.builder()
              .word(word)
              .enSentence(englishSentence)
              .plSentence(polishSentences.stream().collect(Collectors.joining(";")))
              .source("tatoeba")
              .build());
        }
      }
      return sentences;
    } catch (Exception e) {
      log.error("TatoebaScrapper ", e);
      // e.printStackTrace();
    } finally {
      driver.quit();
    }
    return Collections.EMPTY_LIST;
  }

}
