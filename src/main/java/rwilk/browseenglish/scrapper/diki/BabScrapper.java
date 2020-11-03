package rwilk.browseenglish.scrapper.diki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.model.diki.DikiWord;

@Slf4j
@Component
public class BabScrapper {

  public static void main(String[] args) {
    BabScrapper babScrapper = new BabScrapper();
    babScrapper.webScrap("child");
  }

  public List<DikiWord> webScrap(String englishWord) {
    log.info("[Bab scrapper] {}", englishWord);
    try {
      List<DikiWord> dikiWords = new ArrayList<>();

      String url = "https://pl.bab.la/slownik/angielski-polski/" + englishWord.
          trim()
          .replaceAll("British English", "")
          .trim()
          .replaceAll("American English", "")
          .trim()
          .replaceAll(" ", "-")
          .replaceAll("something", "sth")
          .replaceAll("somebody", "sb");

      Document document = Jsoup.connect(url)
          .cookie("PHPSESSID", "odhs2tres3v5ho5t0s8asi1b44")
          .userAgent("Mozilla")
          .timeout(10000)
          .get();
      Elements elements = document.select("div.quick-results").get(0).children();

      boolean skip = false;
      for (Element result : elements) {
        if (result.className().equals("quick-results-header")) {
          if (!skip) {
            skip = true;
            continue;
          } else {
            break;
          }
        }
        DikiWord dikiWord = new DikiWord();

        dikiWord.setEnName(result.select("div.quick-result-option").select("a.babQuickResult").text());

        if (!result.select("div.quick-result-overview").select("ul.sense-group-results").isEmpty()) {
          Elements translations = result.select("div.quick-result-overview").select("ul.sense-group-results").get(0).children();
          for (Element translation : translations) {
            DikiTranslation dikiTranslation = new DikiTranslation();
            dikiTranslation.setPlName(translation.text());
            if (!result.select("div.quick-result-option").select("span.suffix").isEmpty()) {
              dikiTranslation.setPartOfSpeech(
                  result.select("div.quick-result-option").select("span.suffix").get(0).text().replace("{", "").replace("}", ""));
            } else {
              dikiTranslation.setPartOfSpeech("");
            }
            dikiTranslation.setGrammarType("");
            dikiWord.getDikiTranslations().add(dikiTranslation);
          }
          dikiWord.setSource("bab");
          dikiWords.add(dikiWord);
        }
      }
      return dikiWords;
    } catch (Exception e) {
      log.error("[{}] ", englishWord/*, e*/);
      return Collections.emptyList();
    }
  }


}
