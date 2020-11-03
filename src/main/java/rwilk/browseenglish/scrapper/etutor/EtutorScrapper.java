package rwilk.browseenglish.scrapper.etutor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.CourseRepository;
import rwilk.browseenglish.repository.LessonRepository;
import rwilk.browseenglish.repository.SentenceRepository;
import rwilk.browseenglish.repository.WordRepository;
import rwilk.browseenglish.scrapper.audio.DikiAudio;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Etutor words scrapper
 */

@Slf4j
@Component
public class EtutorScrapper {

  private static final String AUDIO_FOLDER = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\";
  private final CourseRepository courseRepository;
  private final LessonRepository lessonRepository;
  private final WordRepository wordRepository;
  private final SentenceRepository sentenceRepository;
  private final DikiAudio dikiAudio;

  @Autowired
  public EtutorScrapper(CourseRepository courseRepository, LessonRepository lessonRepository,
                        WordRepository wordRepository, SentenceRepository sentenceRepository, DikiAudio dikiAudio) {
    this.courseRepository = courseRepository;
    this.lessonRepository = lessonRepository;
    this.wordRepository = wordRepository;
    this.sentenceRepository = sentenceRepository;
    this.dikiAudio = dikiAudio;
  }

  public void webScrap() throws IOException {
    HashMap<String, String> cookies = new HashMap<>();
    cookies.put("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej");

    Document document = Jsoup.connect("https://www.etutor.pl/words ").cookies(cookies).timeout(10000).maxBodySize(0).get();
    Elements titleElements = document.select("div.words-box-title");
    Elements contentElements = document.select("ul.baseItemList");

    if (contentElements.size() != 40) {
      throw new RuntimeException();
    }

    for (int i = 0; i < contentElements.size(); i++) {
      sleep(2000);
      try {
        log.info("START INDEX= {}", i);
        // extractWords(titleElements, contentElements, cookies, i);
        extractAudio(titleElements, contentElements, cookies, i);
        log.info("FINISH INDEX= {}", i);
      } catch (IOException e) {
        log.error("FINISH INDEX= {} WITH ERROR: ", i, e);
      }
    }
  }

  public void webScrapAudio() {
    Map<Course, List<Lesson>> lessonsMap = lessonRepository.findAll().stream()
        .filter(l -> !l.isReady())
        .collect(Collectors.groupingBy(Lesson::getCourse));

    lessonsMap.forEach((course, lessons) -> {
      String courseFolder = course.getEnName().replace("ETUTOR", "").trim()
          .replace("Lekcje: ", "").trim();
      new File(AUDIO_FOLDER + courseFolder).mkdirs();

      for (Lesson lesson : lessons) {
        String lessonFolder = lesson.getEnName().substring(lesson.getEnName().indexOf(" ")).trim()
            .replace("...", "")
            .replace("?", "")
            .replace(":", "")
            .replaceAll("\"", "")
            .replaceAll("/", "")
            .replaceAll("\\\\", "")
            .replace("!", "").trim();
        log.info("START WEBSCRAP {} {}", courseFolder, lessonFolder);
        new File(AUDIO_FOLDER + courseFolder + "\\" + lessonFolder).mkdirs();

        HashMap<String, String> cookies = new HashMap<>();
        cookies.put("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej");

        try {
          Document lessonContent = Jsoup.connect(lesson.getHref()).cookies(cookies).timeout(10000).get();
          Elements audios = lessonContent.select("span.audioIcon");
          log.info("LESSON CONTAINS {} AUDIOS BUT ONLY {} ARE UNIQUE", audios.size(), audios.stream().map(element -> element.attr("data-audio-url")).distinct().count());
          for (Element audio : audios) {
            String audioUrl = audio.attr("data-audio-url");
            String audioName = audioUrl.contains("en-ame")
                ? "us_" + audioUrl.substring(audioUrl.lastIndexOf("/") + 1, audioUrl.lastIndexOf("."))
                : "en_" + audioUrl.substring(audioUrl.lastIndexOf("/") + 1, audioUrl.lastIndexOf("."));
            dikiAudio.downloadAudio(new URL("https://www.diki.pl/" + audioUrl), AUDIO_FOLDER + courseFolder + "\\" + lessonFolder, audioName);
            // sleep(1000);
          }

          lesson.setReady(true);
          lessonRepository.save(lesson);
          log.info("FINISH WEBSCRAP {} {}", courseFolder, lessonFolder);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    });

  }


  public void extractWords(Elements titleElements, Elements contentElements, HashMap<String, String> cookies, int i) throws IOException {
    Course course = Course.builder()
        .enName("ETUTOR " + titleElements.get(i).select("a").text().trim())
        .plName("ETUTOR " + titleElements.get(i).select("a").text().trim())
        .build();
    course = courseRepository.save(course);

    Elements lessons = contentElements.get(i).select("li"); // lekcje w kursie

    for (Element lessonElement : lessons) {
      lessonElement.text(); // nazwa leckji // A1 1000 angielskich słówek - 01 - Podstawowe zwroty 20 elementów 10%
      String url = lessonElement.select("a").get(1).attr("href"); // /words/177022

      Lesson lesson =
          Lesson.builder()
              .enName(lessonElement.text().trim())
              .plName(lessonElement.text().trim())
              .course(course)
              .build();
      lesson = lessonRepository.save(lesson);

      Document courseDocument = Jsoup.connect("https://www.etutor.pl" + url).cookies(cookies).timeout(10000).get();
      // Document courseDocument = Jsoup.connect("https://www.etutor.pl/words/199").cookies(cookies).timeout(10000).get();

      Elements elements = courseDocument.select("div.wordListElementScreen");
      for (Element element : elements) {
        Elements lis = element.select("ul.sentencesul").select("li");
        List<Sentence> sentences = new ArrayList<>();
        for (Element li : lis) {
          String text = li.text();
          try {
            if (li.text().contains("Dodaj notatkę Zapisz Anuluj")) {
              String enSentence = text.substring(0, li.text().indexOf("Dodaj notatkę Zapisz Anuluj")).trim();
              String plSentence =
                  text.substring(li.text().indexOf("Dodaj notatkę Zapisz Anuluj")).replace("Dodaj notatkę Zapisz Anuluj", "").trim();

              Sentence sentence = Sentence.builder()
                  .enSentence(enSentence)
                  .plSentence(plSentence)
                  .build();
              sentences.add(sentence);
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

        String enName = element.select("span.hw").text();
        if (StringUtils.isEmpty(enName)) {
          Elements clozeElements = element.select("p.phraseEntity").select("span.cloze");
          enName = element.select("p.phraseEntity").text();
          for (Element clozeElement : clozeElements) {
            enName = enName.replace(clozeElement.text(), "[" + clozeElement.text() + "]");
          }
        }
        String comparative = "";
        String superlative = "";
        String pastTense = "";
        String pastParticiple = "";
        String plural = "";
        String synonym = "";
        ArrayList<String> meaningList = new ArrayList<>(Arrays.asList(enName.split(",")));
        for (int j = 0; j < meaningList.size(); j++) {
          String meaning = meaningList.get(j);
          if (meaning.contains("stopień wyższy")) {
            if (meaning.indexOf("stopień wyższy") > 0) {
              enName = meaning.substring(0, meaning.indexOf("stopień wyższy"));
              meaning = meaning.replace(enName, "");
              comparative = meaning.replace("stopień wyższy", "").trim();
              meaningList.set(j, enName);
            } else {
              comparative = meaning.replace("stopień wyższy", "").trim();
              meaningList.set(j, "");
            }
          } else if (meaning.contains("stopień najwyższy")) {
            superlative = meaning.replace("stopień najwyższy", "").trim();
            meaningList.set(j, "");
          } else if (meaning.contains("past tense")) {
            if (meaning.indexOf("past tense") > 0) {
              enName = meaning.substring(0, meaning.indexOf("past tense"));
              meaning = meaning.replace(enName, "");
              pastTense = meaning.replace("past tense", "").trim();
              meaningList.set(j, enName);
            } else {
              pastTense = meaning.replace("past tense", "").trim();
              meaningList.set(j, "");
            }
          } else if (meaning.contains("past participle")) {
            pastParticiple = meaning.replace("past participle", "").trim();
            meaningList.set(j, "");
          } else if (meaning.contains("slang")) {
            meaningList.set(j, "");
          } else if (meaning.contains("dialect")) {
            meaningList.set(j, "");
          }
        }
        enName = meaningList.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining(", "));
        if (enName.contains("plural:")) {
          plural = enName.substring(enName.indexOf("plural:")).trim();
          enName = enName.replace(plural, "").trim();
          plural = plural.replace("plural:", "").trim();
        }
        String plName = element.select("p.phraseExplanation").text().replace("Dodaj notatkę Zapisz Anuluj", "").trim();
        if (plName.contains("synonim:")) {
          synonym = plName.substring(plName.indexOf("synonim:")).trim();
          plName = plName.replace(synonym, "").trim();
          synonym = synonym.replace("synonim:", "").trim();
        } else if (plName.contains("synonimy:")) {
          synonym = plName.substring(plName.indexOf("synonimy:")).trim();
          plName = plName.replace(synonym, "").trim();
          synonym = synonym.replace("synonimy:", "").trim();
        }

        ArrayList<String> enNames = new ArrayList<>(Arrays.asList(enName.split(",")));
        String englishName = enNames.get(0);
        String americanName = "";
        String otherNames = "";

        if (enNames.size() > 1) {
          if (enNames.get(1).contains("American English")) {
            americanName = enNames.get(1);
            if (enNames.size() > 2) {
              otherNames = enNames.subList(2, enNames.size()).stream().collect(Collectors.joining(","));
            }
          } else {
            otherNames = enNames.subList(1, enNames.size()).stream().collect(Collectors.joining(","));
          }
        }

        Word word =
            Word.builder()
                .enName(englishName.trim())
                .usName(americanName.trim())
                .otherNames(otherNames.trim())
                .plName(plName.trim())
                .lesson(lesson)
                .level(lessonElement.select("span.learningLevelSign").text().trim())
                .comparative(comparative.trim())
                .superlative(superlative.trim())
                .pastTense(pastTense.trim())
                .pastParticiple(pastParticiple.trim())
                .plural(plural.trim())
                .synonym(synonym)
                .source("etutor")
                .build();
        word = wordRepository.save(word);
        for (Sentence sentence : sentences) {
          sentence.setWord(word);
          sentenceRepository.save(sentence);
        }

      }
    }
  }

  public void extractAudio(Elements titleElements, Elements contentElements, HashMap<String, String> cookies, int i) throws IOException {
    String courseFolder = titleElements.get(i).select("a").text().trim();

    // new File(AUDIO_FOLDER + courseFolder).mkdirs();

    Elements lessons = contentElements.get(i).select("li"); // lekcje w kursie
    for (Element lessonElement : lessons) {
      String lessonFolder = lessonElement.select("a").size() == 3
          ? lessonElement.select("a").get(1).text()
          : lessonElement.select("a").get(0).text(); // nazwa leckji // A1 1000 angielskich słówek - 01 - Podstawowe zwroty 20 elementów 10%
      String lessonHref = lessonElement.select("a").size() == 3
          ? lessonElement.select("a").get(1).attr("href")
          : lessonElement.select("a").get(0).attr("href"); // nazwa leckji // A1 1000 angielskich słówek - 01 - Podstawowe zwroty 20 elementów 10%

      String lessonLevel = lessonElement.select("a").size() == 3
          ? lessonElement.select("a").get(0).text()
          : "S";
      String elementsCount = lessonElement.select("a").size() == 3
          ? lessonElement.select("a").get(2).text().substring(0, lessonElement.select("a").get(2).text().indexOf(" "))
          : lessonElement.select("a").get(1).text().substring(0, lessonElement.select("a").get(1).text().indexOf(" "));
      // skip lesson index 23

      String oldLessonName = lessonElement.text().trim();

      List<Lesson> lessonList = lessonRepository.findByPlNameContainsAndCourse_EnNameContains(lessonFolder + " " + elementsCount, courseFolder);
      Lesson lesson = null;
      for (Lesson l : lessonList) {
        if (l.getHref() == null) {
          lesson = l;
          break;
        }
      }
      if (lesson == null) {
        lesson = lessonList.get(0);
      }

//      Optional<Lesson> optionalLesson = Optional.empty();
//      try {
//        optionalLesson = lessonRepository.findByPlNameContainsAndCourse_EnNameContains(lessonFolder + " " + elementsCount, courseFolder);
//      } catch (Exception e) {
//        optionalLesson = Optional.empty();
//        e.printStackTrace();
//      }
      if (lesson != null) {
        // Lesson lesson = optionalLesson.get();
        lesson.setEnName(lessonLevel + " " + lessonFolder);
        // lesson.setPlName(lessonLevel + " " + lessonFolder);
        lesson.setHref("https://www.etutor.pl" + lessonHref);
        lessonRepository.save(lesson);
        // sleep(2000);
      } else {
        throw new IllegalStateException();
      }


//      new File(AUDIO_FOLDER + courseFolder + "\\" + lessonFolder).mkdirs();
//
//      String lessonUrl = lessonElement.select("a").get(1).attr("href"); // /words/177022
//
//      Document lessonContent = Jsoup.connect("https://www.etutor.pl" + lessonUrl).cookies(cookies).timeout(10000).get();
//
//      Elements audios = lessonContent.select("span.audioIcon");
//
//      for (Element audio : audios) {
//        String audioUrl = audio.attr("data-audio-url");
//        dikiAudio.downloadEnDikiAudio(audioUrl, AUDIO_FOLDER + courseFolder + "\\" + lessonFolder);
//        sleep(1000);
//      }
    }
  }

  private void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
