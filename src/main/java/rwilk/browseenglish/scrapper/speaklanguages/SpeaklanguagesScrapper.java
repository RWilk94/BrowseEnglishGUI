package rwilk.browseenglish.scrapper.speaklanguages;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class SpeaklanguagesScrapper {

  public List<Word> webScrapLesson(String url) {
    log.info("[SpeaklanguagesScrapper] start " + url);
    try {
      List<Word> words = new ArrayList<>();
      Document document = Jsoup.connect(url).timeout(10000).get();

      Elements elements = document.select("div#content").get(0).select("tr");

      for (Element row : elements) {
        if (row.select("td").size() == 2) {
          Word word = Word.builder()
              .enName(row.select("td").get(0).text())
              .plName(row.select("td").get(1).text())
              .level("A1")
              .usName("")
              .otherNames("")
              .source("speakLanguages")
              .build();
          words.add(word);
        }
      }
      log.info("[SpeaklanguagesScrapper] finish " + url);
      return words;
    } catch (Exception e) {
      log.error("[SpeakLanguagesScrapper] " + url, e);
      return Collections.emptyList();
    }
  }

  public static void main(String[] args) {
    SpeaklanguagesScrapper speaklanguagesScrapper = new SpeaklanguagesScrapper();
    speaklanguagesScrapper.webScrapWords();
  }

  public List<Lesson> webScrapWords() {
    log.info("[SpeaklanguagesScrapper] start ");
    try {
      List<Lesson> lessons = new ArrayList<>();
      // Document document = Jsoup.connect("https://pl.speaklanguages.com/angielski/s%C5%82ownictwo/").timeout(10000).get();
      Document document = Jsoup.connect("https://pl.speaklanguages.com/angielski/zwroty/").timeout(10000).get();

      Elements elements = document.select("div#contents").get(0).select("a");

      for (Element a : elements) {
        Lesson lesson = Lesson.builder()
            .enName(a.text())
            .plName(a.text())
            .isCustom(true)
            .words(webScrapLesson(a.attr("href")))
            .build();
        lessons.add(lesson);
      }
      return lessons;
    } catch (Exception e) {
      log.error("[SpeakLanguagesScrapper] ", e);
      return Collections.emptyList();
    }
  }

}
