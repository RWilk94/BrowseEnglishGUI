package rwilk.browseenglish.scrapper.wiki;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.WordRepository;

@Slf4j
@Component
public class WikiScrapper {

  @Autowired
  private WordRepository wordRepository;

//  public static void main(String[] args) {
//
//    WikiScrapper wikiScrapper = new WikiScrapper();
//    wikiScrapper.webScrap1000BaseWord();
//
//  }

  public void webScrap1000BaseWord() {
    try {
      List<String> words = new ArrayList<>();
      Document document = Jsoup.connect("https://pl.wiktionary.org/wiki/Indeks:Angielski_-_1000_podstawowych_s%C5%82%C3%B3w")
          .timeout(10000)
          .get();

      Elements elements = document.select("div.mw-parser-output");
      Element mainElement = elements.first();

      Elements ddElements = mainElement.select("dd");

      for (Element dd : ddElements) {
        Elements aElements = dd.select("a");
        for (Element a : aElements) {
          words.add(a.attr("title"));
        }
      }
      updateWordFrequency(words, 0L);
    } catch (Exception e) {
      log.error("[WikiScrapper] ", e);
    }
  }

  public void webScrap1_2000MostFrequentWords() {
    try {
      List<String> words = new ArrayList<>();
      Document document = Jsoup.connect("https://pl.wiktionary.org/wiki/Indeks:Angielski_-_Najpopularniejsze_s%C5%82owa_2001-4000")
          .timeout(10000)
          .get();

      Elements elements = document.select("div.mw-parser-output");
      Element mainElement = elements.first();

      Elements pElements = mainElement.select("p");

      for (Element dd : pElements) {
        Elements aElements = dd.select("a");
        for (Element a : aElements) {
          String title = a.attr("title");
          if (!title.contains(":")) {
            words.add(title);
          }
        }
      }
      updateWordFrequency(words, 4000L);
    } catch (Exception e) {
      log.error("[WikiScrapper] ", e);
    }

  }

  private void updateWordFrequency(List<String> frequencyWords, Long frequency) {

    for (String frequencyWord : frequencyWords) {
      List<Word> words = wordRepository.findAllByEnNameOrEnNameOrUsNameOrUsName(
          frequencyWord, frequencyWord + " (British English)", frequencyWord, frequencyWord + " (American English)");

      if (words.isEmpty()) {
        log.error("[NOT FOUND] " + frequencyWord);
      } else {
        for (Word word : words) {
          if (word.getFrequency() == null || word.getFrequency() > frequency) {
            word.setFrequency(frequency);
            wordRepository.save(word);
          }
        }
      }
    }

  }

}
