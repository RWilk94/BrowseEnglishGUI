package rwilk.browseenglish.scrapper.etutor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.CourseRepository;
import rwilk.browseenglish.repository.LessonRepository;
import rwilk.browseenglish.repository.WordRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EtutorDictionaryScrapper {

  private final CourseRepository courseRepository;
  private final LessonRepository lessonRepository;
  private final WordRepository wordRepository;

  public EtutorDictionaryScrapper(CourseRepository courseRepository, LessonRepository lessonRepository, WordRepository wordRepository) {
    this.courseRepository = courseRepository;
    this.lessonRepository = lessonRepository;
    this.wordRepository = wordRepository;
  }

  public static void main(String[] args) {

//    EtutorDictionaryScrapper scrapper = new EtutorDictionaryScrapper(null, null, null);
//    scrapper.webScrapDictionary("https://www.etutor.pl/thematic-dictionary/slownik-terminow-administracyjnych");

  }

  public void webScrapDictionaryNames() {
    try {
      Document dictionaryMainPage = Jsoup.connect("https://www.etutor.pl/thematic-dictionaries/en/pl")
          .cookie("autoLoginToken", "AG94egW0SBkoCLy0izvNYHDFz4Ky6EgNVERVHPej")
          .timeout(10000).get();

      Elements dictionaryElements = dictionaryMainPage.select("div.basescolumnslist");

      for (Element dictionary : dictionaryElements) {
        String courseName = dictionary.select("div.words-box-title").text();
        Course course = Course.builder()
            .enName(courseName)
            .plName(courseName)
            .build();

        // add course
        course = courseRepository.save(course);

        Elements subDictionaryElements = dictionary.select("li");
        for (Element subDictionary : subDictionaryElements) {
          String lessonName = subDictionary.select("a").text();
          String href = subDictionary.select("a").get(0).attr("href");

          Lesson lesson = Lesson.builder()
              .enName(lessonName)
              .plName(lessonName)
              .href(href)
              .course(course)
              .build();

          // add lesson
          lessonRepository.save(lesson);
        }
      }
    } catch (Exception e) {
      log.error("EtutorDictionaryScrapper: ", e);
    }
  }

  public void webScrapDictionary() {

    List<Lesson> lessons = lessonRepository.findAll()
        .stream()
        // .filter(lesson -> !lesson.isReady() && lesson.getId() > 3153L)
        .collect(Collectors.toList());

    for (Lesson lesson : lessons) {
      webScrapDictionary(lesson);
    }

  }

  private void webScrapDictionary(Lesson lesson) {
    try {
      lesson = lessonRepository.findById(lesson.getId()).get();
      if (lesson.isReady()) {
        return;
      }

      Document dictionaryMainPage = Jsoup.connect(lesson.getHref())
          .cookie("autoLoginToken", "pUWSoWMJglrdOVJciEXqboOuNY0rgrTP1nq1fI9K")
          .timeout(10000).get();

      long pages = Long.parseLong(dictionaryMainPage.select("strong").get(1).text());

      for (int i = 0; i < pages; i+= 50) {
        // Thread.sleep(1000);

        log.info("[WEB SCRAP] {} {} / {}", lesson.getEnName(), i, pages);
        dictionaryMainPage = Jsoup.connect(lesson.getHref() + "?order=popular&thematicDictionaryPage=" + i)
            .cookie("autoLoginToken", "pUWSoWMJglrdOVJciEXqboOuNY0rgrTP1nq1fI9K")
            .timeout(10000).get();

        Elements dictionaryEntityElements = dictionaryMainPage.select("div.dictionaryEntity");

        for (Element dictionaryEntity : dictionaryEntityElements) {

          Elements spanElements = dictionaryEntity.select("span");
          Word word = new Word();
          for (Element span : spanElements) {
            if (span.className().contains("fentrymain")) {
              String textLine = span.text();
              List<String> texts = Arrays.asList(textLine.split(" , "));
              for (String text : texts) {
                text = text.replace("British English", "(British English)")
                    .replace("American English", "(American English)");
                if (text.contains("(American English)") && StringUtils.isEmpty(word.getUsName())) {
                  word.setUsName(text.trim());
                } else if (text.contains("(American English)")) {
                  String otherName = (StringUtils.isNotEmpty(word.getOtherNames()) ? word.getOtherNames() : "").concat(text).concat(";");
                  word.setOtherNames(otherName);
                } else if (StringUtils.isEmpty(word.getEnName())) {
                  word.setEnName(text.trim());
                } else {
                  String otherName = (StringUtils.isNotEmpty(word.getOtherNames()) ? word.getOtherNames() : "").concat(text).concat(";");
                  word.setOtherNames(otherName);
                }
              }
            } else if (span.className().contains("hw")) {
              String plName = (StringUtils.isNotEmpty(word.getPlName()) ? word.getPlName() : "").concat(span.text()).concat("; ");
              word.setPlName(plName);
            }
          }
          if (StringUtils.isNotEmpty(word.getPlName()) && word.getPlName().endsWith(";")) {
            word.setPlName(word.getPlName().substring(0, word.getPlName().length() - 1).trim());
          }
          if (StringUtils.isNotEmpty(word.getOtherNames()) && word.getOtherNames().endsWith(";")) {
            word.setOtherNames(word.getOtherNames().substring(0, word.getOtherNames().length() - 1).trim());
          }
          word.setPlName(StringUtils.isNotEmpty(word.getPlName()) ? word.getPlName().trim() : "");
          word.setOtherNames(StringUtils.isNotEmpty(word.getOtherNames()) ? word.getOtherNames().trim() : "");
          word.setLesson(lesson);
          wordRepository.save(word);
        }
      }
      lesson.setReady(true);
      lessonRepository.save(lesson);
    } catch (Exception e) {
      log.error("EtutorDictionaryScrapper: ", e);
    }
  }


}
