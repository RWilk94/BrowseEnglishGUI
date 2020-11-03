package rwilk.browseenglish.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.model.entity.Sentence;
import rwilk.browseenglish.model.entity.Word;
import rwilk.browseenglish.repository.CourseRepository;
import rwilk.browseenglish.repository.LessonRepository;
import rwilk.browseenglish.repository.SentenceRepository;
import rwilk.browseenglish.repository.WordRepository;

import java.io.PrintWriter;
import java.util.List;

@Slf4j
@Service
public class ExportDatabaseService {

  private final CourseRepository courseRepository;
  private final LessonRepository lessonRepository;
  private final WordRepository wordRepository;
  private final SentenceRepository sentenceRepository;

  public ExportDatabaseService(CourseRepository courseRepository, LessonRepository lessonRepository, WordRepository wordRepository, SentenceRepository sentenceRepository) {
    this.courseRepository = courseRepository;
    this.lessonRepository = lessonRepository;
    this.wordRepository = wordRepository;
    this.sentenceRepository = sentenceRepository;
  }

  public void exportDatabase() {
    exportCourses(courseRepository.findAll());
    exportLessons(lessonRepository.findAll());
    exportWords(wordRepository.findAll());
    exportSentences(sentenceRepository.findAll());
  }

  private void exportCourses(List<Course> courses) {
    log.info("START GENERATING COURSES");
    List<List<Course>> chunks = ListUtils.partition(courses, 100);
    StringBuilder sql = new StringBuilder();
    for (List<Course> chunk : chunks) {
      sql.append("INSERT INTO 'courses' ('id', 'english_name', 'image', 'is_custom', 'level', 'polish_name', 'current_order') VALUES ");

      for (Course course : chunk) {
        // (33, 'POZIOM 1', NULL, 0, 1, 'POZIOM 1', 1),
        sql
            .append("\n")
            .append("(")
            .append(course.getId())
            .append(", '")
            .append(course.getEnName().replaceAll("'", "''"))
            .append("', ")
            .append("NULL")
            .append(", ")
            .append("0, ")
            .append("1, ") // TODO set level
            .append("'")
            .append(course.getPlName().replaceAll("'", "''"))
            .append("', ")
            .append(course.getId());
        if (chunk.indexOf(course) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("courses.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING COURSES");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING COURSES: ", e);
    }
  }

  private void exportLessons(List<Lesson> lessons) {
    log.info("START GENERATING LESSONS");
    List<List<Lesson>> chunks = ListUtils.partition(lessons, 100);
    StringBuilder sql = new StringBuilder();
    for (List<Lesson> chunk : chunks) {
      // INSERT INTO 'lessons' ('id', 'english_name', 'image', 'is_custom', 'polish_name', 'course_id', 'current_order') VALUES

      sql.append("INSERT INTO 'lessons' ('id', 'english_name', 'image', 'is_custom', 'polish_name', 'course_id', 'current_order') VALUES ");

      for (Lesson lesson : chunk) {
        //    (771, 'Podstawowe zwroty', NULL, 0, 'Podstawowe zwroty', 33, 20),
        sql
            .append("\n")
            .append("(")
            .append(lesson.getId())
            .append(", '")
            .append(lesson.getEnName().replaceAll("'", "''"))
            .append("', ")
            .append("NULL")
            .append(", ")
            .append("0, ")
            .append("'")
            .append(lesson.getEnName().replaceAll("'", "''"))
            .append("', ")
            .append(lesson.getCourse().getId())
            .append(", ")
            .append(lesson.getId());
        if (chunk.indexOf(lesson) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("lessons.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING LESSONS");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING LESSONS: ", e);
    }
  }

  private void exportWords(List<Word> words) {
    log.info("START GENERATING WORDS");
    List<List<Word>> chunks = ListUtils.partition(words, 100);
    StringBuilder sql = new StringBuilder();
    for (List<Word> chunk : chunks) {

      sql.append("INSERT INTO 'words' ('id', 'polish_word', 'english_word', 'part_of_speech', 'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series', 'is_custom', 'browse', 'lesson_id', 'current_order', 'sound') VALUES ");

      for (Word word : chunk) {
        // (407, 'wyjście', 'exit', 'rzeczownik', -1, 0, 0, 0, 0, 0, 1, 0, -1, 771, 41),
        sql
            .append("\n")
            .append("(")
            .append(word.getId())
            .append(", '")
            .append(word.getPlName().replaceAll("'", "''"))
            .append("', '")
            .append(word.getEnName().replaceAll("'", "''"))
            .append("', '")
            .append(" ") // word.getPartOfSpeech()
            .append("', ")
            .append("-1, 0, 0, 0, 0, 0, 1, 0, -1, ")
            .append(word.getLesson().getId())
            .append(", ")
            .append(word.getId())
            .append(", '")
            .append(word.getSound())
            .append("'");
        if (chunk.indexOf(word) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("words.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING WORDS");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING WORDS: ", e);
    }
  }

  private void exportSentences(List<Sentence> sentences) {
    log.info("START GENERATING SENTENCES");
    List<List<Sentence>> chunks = ListUtils.partition(sentences, 100);
    StringBuilder sql = new StringBuilder();
    for (List<Sentence> chunk : chunks) {
      sql.append("INSERT INTO 'sentences' ('id', 'english_name', 'polish_name', 'word_id') VALUES ");

      for (Sentence sentence : chunk) {
        // (12, 'This is a dead-end alley.', 'To ślepa uliczka.', 4),
        sql
            .append("\n")
            .append("(")
            .append(sentence.getId())
            .append(", '")
            .append(sentence.getEnSentence().replaceAll("'", "''"))
            .append("', '")
            .append(sentence.getPlSentence().replaceAll("'", "''"))
            .append("', ")
            .append(sentence.getWord().getId());
        if (chunk.indexOf(sentence) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("sentences.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING SENTENCES");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING SENTENCES: ", e);
    }
  }

}
