package rwilk.browseenglish.scrapper.ang;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import rwilk.browseenglish.model.entity.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class AngScrapper {

  public static void main(String[] args) {
    AngScrapper angScrapper = new AngScrapper();
    angScrapper.webScrapWithImages("https://www.ang.pl/slownictwo/slownictwo-angielskie-poziom-a1");
  }

  public List<Word> webScrapWithImages(String url) {
    try {
      List<Word> words = new ArrayList<>();
      Document document = Jsoup.connect(url).timeout(10000).get();

      int pages = 1;
      Elements pagination = document.select("ul.pagination"); // .get(0).select("li");
      if (pagination.size() > 0) {
        pagination.get(0).select("li");
        pages = Integer.parseInt(pagination.get(pagination.size() - 1).text());
      }

      for (int i = 1; i <= pages; i++) {
        document = Jsoup.connect(url + "/page/" + i).timeout(10000).get();
        Elements elements = document.select("div.ditem");

        for (Element element : elements) {
          Word word = Word.builder()
              .enName(element.select("p.word").select("a").text())
              .usName("")
              .otherNames("")
              .plName(element.select("p.tr").text())
              .partOfSpeech(element.select("p.word").select("span").attr("class"))
              .grammarType("")
              .comparative("")
              .superlative("")
              .pastTense("")
              .pastParticiple("")
              .plural("")
              .synonym("")
              .source("ang")
              .lesson(null)
              .build();
          words.add(word);
        }
      }
      return words;
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }

}
