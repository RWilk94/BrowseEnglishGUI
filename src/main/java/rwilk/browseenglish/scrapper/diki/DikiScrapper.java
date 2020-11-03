package rwilk.browseenglish.scrapper.diki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import rwilk.browseenglish.model.diki.DikiSentence;
import rwilk.browseenglish.model.diki.DikiTranslation;
import rwilk.browseenglish.model.diki.DikiWord;

@Slf4j
@Component
public class DikiScrapper {

  private List<DikiWord> dikiWords;

  public List<DikiWord> webScrap(String englishWord) {
    log.info("[Diki scrapper] {}", englishWord);
    try {
      dikiWords = new ArrayList<>();

      String url = "http://www.diki.pl/slownik-angielskiego?q=" + englishWord.
          trim()
          .replaceAll("British English", "")
          .trim()
          .replaceAll("American English", "")
          .trim()
          .replaceAll(" ", "+");

      // Wt8bpASp84XmYDz0wRAcjUn8HU5QylhRRXKgem95
      Document document = Jsoup.connect(url).cookie("autoLoginToken", "7Gzy1dCKErpdyBhzvc14Xt6uBoZMFUso0LwclUan").userAgent("Mozilla").timeout(10000).get();
      Elements elements = document.select("div.diki-results-left-column").get(0).child(0)
          .select("div.dictionaryEntity"); // return elements containing translations

      for (Element leftColumnElement : elements) {
        DikiWord dikiWord = new DikiWord();

        Elements wordVersionElements = leftColumnElement.select("div.hws").get(0).select("span.hw");
        Elements languageVersionElements = leftColumnElement.select("div.hws").get(0).select("a.languageVariety");

        //for words with different versions ex. colour (BrE); color (AmE)
        if (wordVersionElements.size() == languageVersionElements.size()) {
          List<String> wordVersions = new ArrayList<>();
          for (int i = 0; i < wordVersionElements.size(); i++) {
            wordVersions.add(wordVersionElements.get(i).text() + " (" + addWordVersion(languageVersionElements.get(i).text()) + ")");
          }
          mapWordVersion(dikiWord, wordVersions);
        } else {
          // for words with one version np. dog; house
          List<String> wordVersions = wordVersionElements.stream().map(Element::text).collect(Collectors.toList());
          mapWordVersion(dikiWord, wordVersions);
        }
        String partOfSpeech = "";

        for (Element meaningElement : leftColumnElement.children()) {

          if (meaningElement.hasClass("partOfSpeechSectionHeader")) {
            partOfSpeech = meaningElement.select("span.partOfSpeech").text();

          } else if (meaningElement.hasClass("foreignToNativeMeanings") || meaningElement.hasClass("nativeToForeignEntrySlices")) {
            for (Element meaningChildElement : meaningElement.children()) {
              DikiTranslation dikiTranslation = new DikiTranslation();
              dikiTranslation.setPlName(meaningChildElement.select("span.hw").stream().map(Element::text).collect(Collectors.joining(", ")));
              dikiTranslation.setGrammarType(meaningChildElement.select("a.grammarTag").text());
              List<DikiSentence> dikiSentences = new ArrayList<>();

              for (Element exampleSentenceElement : meaningChildElement.select("div.exampleSentence")) {
                dikiSentences.add(DikiSentence.builder()
                    .enName(exampleSentenceElement.text().substring(0, exampleSentenceElement.text().lastIndexOf("(")))
                    .plName(exampleSentenceElement.text().substring(exampleSentenceElement.text().lastIndexOf("(") + 1, exampleSentenceElement.text().lastIndexOf(")")))
                    .build());
              }
              dikiTranslation.setPartOfSpeech(partOfSpeech);
              dikiTranslation.setDikiSentences(dikiSentences);
              dikiWord.getDikiTranslations().add(dikiTranslation);
            }
          } else if (meaningElement.hasClass("af")) {
            String text = meaningElement.children().text().replace("stopień wyższy ", "");
            dikiWord.setComparative(text.substring(0, text.indexOf("stopień najwyższy")).trim());
            text = text.substring(dikiWord.getComparative().length()).replace("stopień najwyższy", "");
            dikiWord.setSuperlative(text.trim());
          } else if (meaningElement.hasClass("vf")) {
            if ( meaningElement.children().text().contains("past tense")) {
              String text = meaningElement.children().text().replace("Formy nieregularne: ", "").replace("past tense", "").trim();
              dikiWord.setPastTense(text.substring(0, text.indexOf("past participle")).trim());
              text = text.substring(dikiWord.getPastTense().length()).replace("past participle", "");
              dikiWord.setPastParticiple(text.trim());
            }
          } else if (meaningElement.hasClass("pf")) {
            dikiWord.setPlural(meaningElement.select("span.foreignTermText").text());
          }
        }
        dikiWord.setSource("diki");
        dikiWords.add(dikiWord);
      }
      return dikiWords;
    } catch (Exception e) {
      log.error("[{}] ", englishWord/*, e*/);
      return Collections.emptyList();
    }
  }

  private String addWordVersion(String wordVersion) {
    if (wordVersion.equals("British&nbsp;English") || wordVersion.equals("British English")) {
      return "BrE";
    } else if (wordVersion.equals("American&nbsp;English") || wordVersion.equals("American English")) {
      return "AmE";
    } else {
      return wordVersion;
    }
  }

  private void mapWordVersion(DikiWord dikiWord, List<String> wordVersions) {
    for (int i = 0; i < wordVersions.size(); i++) {
      if (i == 0) {
        dikiWord.setEnName(wordVersions.get(i));
        continue;
      }
      if (wordVersions.get(i).contains("AmE") && StringUtils.isEmpty(dikiWord.getUsName())) {
        dikiWord.setUsName(wordVersions.get(i));
      } else {
        dikiWord.setOtherNames(String.join("\n", wordVersions.get(i)));
        if (dikiWord.getOtherNames() == null) {
          dikiWord.setOtherNames("");
        }
      }
    }
  }

}
