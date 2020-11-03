package rwilk.browseenglish.release.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.entity.CourseRelease;
import rwilk.browseenglish.release.entity.ExerciseRelease;
import rwilk.browseenglish.release.entity.ExerciseRowRelease;
import rwilk.browseenglish.release.entity.LessonRelease;
import rwilk.browseenglish.release.entity.NoteRelease;
import rwilk.browseenglish.release.entity.SentenceRelease;
import rwilk.browseenglish.release.entity.WordRelease;
import rwilk.browseenglish.release.repository.CourseReleaseRepository;
import rwilk.browseenglish.release.repository.ExerciseReleaseRepository;
import rwilk.browseenglish.release.repository.ExerciseRowReleaseRepository;
import rwilk.browseenglish.release.repository.LessonReleaseRepository;
import rwilk.browseenglish.release.repository.NoteReleaseRepository;
import rwilk.browseenglish.release.repository.SentenceReleaseRepository;
import rwilk.browseenglish.release.repository.WordReleaseRepository;
import rwilk.browseenglish.repository.WordRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExportReleaseDatabaseService {

  private final CourseReleaseRepository courseReleaseRepository;
  private final LessonReleaseRepository lessonReleaseRepository;
  private final WordRepository wordRepository;
  private final WordReleaseRepository wordReleaseRepository;
  private final SentenceReleaseRepository sentenceReleaseRepository;
  private final ExerciseReleaseRepository exerciseReleaseRepository;
  private final ExerciseRowReleaseRepository exerciseRowReleaseRepository;
  private final NoteReleaseRepository noteReleaseRepository;

  public ExportReleaseDatabaseService(CourseReleaseRepository courseReleaseRepository, LessonReleaseRepository lessonReleaseRepository, WordRepository wordRepository, WordReleaseRepository wordReleaseRepository, SentenceReleaseRepository sentenceReleaseRepository, ExerciseReleaseRepository exerciseReleaseRepository, ExerciseRowReleaseRepository exerciseRowReleaseRepository, NoteReleaseRepository noteReleaseRepository) {
    this.courseReleaseRepository = courseReleaseRepository;
    this.lessonReleaseRepository = lessonReleaseRepository;
    this.wordRepository = wordRepository;
    this.wordReleaseRepository = wordReleaseRepository;
    this.sentenceReleaseRepository = sentenceReleaseRepository;
    this.exerciseReleaseRepository = exerciseReleaseRepository;
    this.exerciseRowReleaseRepository = exerciseRowReleaseRepository;
    this.noteReleaseRepository = noteReleaseRepository;
  }

  public void exportDatabase() {
    exportCourses(courseReleaseRepository.findAll());
    exportLessons(lessonReleaseRepository.findAll().stream()
        // .filter(LessonRelease::getIsReady)
        .collect(Collectors.toList()));
    exportWords(wordReleaseRepository.findAll());
    exportSentences(sentenceReleaseRepository.findAll());
    exportNotes(noteReleaseRepository.findAll());
    exportExercises(exerciseReleaseRepository.findAll());
    exportExerciseRows(exerciseRowReleaseRepository.findAll());
  }

  private void exportCourses(List<CourseRelease> courses) {
    log.info("START GENERATING COURSES");
    List<List<CourseRelease>> chunks = ListUtils.partition(courses, 100);
    StringBuilder sql = new StringBuilder();
    for (List<CourseRelease> chunk : chunks) {
      sql.append("INSERT INTO 'courses' ('id', 'english_name', 'polish_name', 'level', 'image', 'is_custom') VALUES ");

      for (CourseRelease course : chunk) {
        // (33, 'POZIOM 1', NULL, 0, 1, 'POZIOM 1', 1),
        sql
            .append("\n")
            .append("(")
            .append(course.getId())
            .append(", '")
            .append(course.getEnName().replaceAll("'", "''"))
            .append("', '")
            .append(course.getPlName().replaceAll("'", "''"))
            .append("', ")
            .append("1, ") // TODO set level
            .append("NULL")
            .append(", 0");
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

  private void exportLessons(List<LessonRelease> lessons) {
    log.info("START GENERATING LESSONS");
    List<List<LessonRelease>> chunks = ListUtils.partition(lessons, 100);
    StringBuilder sql = new StringBuilder();
    for (List<LessonRelease> chunk : chunks) {
      // INSERT INTO 'lessons' ('id', 'english_name', 'image', 'is_custom', 'polish_name', 'course_id', 'current_order') VALUES

      sql.append("INSERT INTO 'lessons' ('id', 'english_name', 'polish_name', 'image', 'is_custom', 'course_id') VALUES ");

      for (LessonRelease lesson : chunk) {
        //    (771, 'Podstawowe zwroty', NULL, 0, 'Podstawowe zwroty', 33, 20),
        sql
            .append("\n")
            .append("(")
            .append(lesson.getId())
            .append(", '")
            .append(lesson.getEnName().replaceAll("'", "''"))
            .append("', '")
            .append(lesson.getPlName().replaceAll("'", "''"))
            .append("', ")
            .append("NULL")
            .append(", ")
            .append("0, ")
            .append(lesson.getCourse().getId());
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

  private void exportWords(List<WordRelease> words) {
    log.info("START GENERATING WORDS");
    List<List<WordRelease>> chunks = ListUtils.partition(words, 100);
    StringBuilder sql = new StringBuilder();
    for (List<WordRelease> chunk : chunks) {

      sql.append("INSERT INTO 'words' ('id', 'english_name', 'american_name', 'other_name', 'polish_name', 'part_of_speech', 'sound', " +
          "'article', 'comparative', 'superlative', 'past_participle', 'past_tense', 'grammar_type', 'level', 'plural', 'position', 'synonym', 'lesson_id'," +
          "'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series', 'is_custom', 'browse') VALUES ");

      for (WordRelease word : chunk) {
        sql
            .append("\n")
            .append("(")
            .append(word.getId())
            .append(", '")
            .append(word.getEnName() != null ? word.getEnName().replaceAll("'", "''") : "")
            .append("', '")
            .append(word.getUsName() != null ? word.getUsName().replaceAll("'", "''") : "")
            .append("', '")
            .append(word.getOtherNames() != null ? word.getOtherNames().replaceAll("'", "''") : "")
            .append("', '")
            .append(word.getPlName() != null ? word.getPlName().replaceAll("'", "''") : "")
            .append("', '")
            .append(word.getPartOfSpeech() != null ? word.getPartOfSpeech() : "")
            .append("', '")
            .append(word.getSound() != null ? word.getSound().replaceAll("'", "''") : "")
            .append("', '")
            .append(word.getArticle() != null ? word.getArticle() : "")
            .append("', '")
            .append(word.getComparative() != null ? word.getComparative() : "")
            .append("', '")
            .append(word.getSuperlative() != null ? word.getSuperlative() : "")
            .append("', '")
            .append(word.getPastParticiple() != null ? word.getPastParticiple() : "")
            .append("', '")
            .append(word.getPastTense() != null ? word.getPastTense() : "")
            .append("', '")
            .append(word.getGrammarType() != null ? word.getGrammarType() : "")
            .append("', '")
            .append(word.getLevel() != null ? word.getLevel() : "")
            .append("', '")
            .append(word.getPlural() != null ? word.getPlural() : "")
            .append("', ")
            .append(word.getPosition())
            .append(", '")
            .append(word.getSynonym() != null ? word.getSynonym() : "")
            .append("', ")
            .append(word.getLesson().getId())
            .append(", ")
            .append("-1, 0, 0, 0, 0, 0, 1, 0, -1 ");
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

  private void exportSentences(List<SentenceRelease> sentences) {
    log.info("START GENERATING SENTENCES");
    List<List<SentenceRelease>> chunks = ListUtils.partition(sentences, 100);
    StringBuilder sql = new StringBuilder();
    for (List<SentenceRelease> chunk : chunks) {
      sql.append("INSERT INTO 'sentences' ('id', 'english_name', 'polish_name', 'sound', 'word_id', 'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series', 'is_custom') VALUES ");

      for (SentenceRelease sentence : chunk) {
        sql
            .append("\n")
            .append("(")
            .append(sentence.getId())
            .append(", '")
            .append(sentence.getEnSentence().replaceAll("'", "''"))
            .append("', '")
            .append(sentence.getPlSentence().replaceAll("'", "''"))
            .append("', '")
            .append(sentence.getSound() != null ? sentence.getSound().replaceAll("'", "''") : "")
            .append("', ")
            .append(sentence.getWord().getId())
            .append(", ")
            .append("-1, 0, 0, 0, 0, 0, 1, 0");;
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

  private void exportNotes(List<NoteRelease> notes) {
    log.info("START GENERATING NOTES");
    List<List<NoteRelease>> chunks = ListUtils.partition(notes, 100);
    StringBuilder sql = new StringBuilder();
    for (List<NoteRelease> chunk : chunks) {
      sql.append("INSERT INTO 'notes' ('id', 'note', 'position', 'lesson_id') VALUES ");

      for (NoteRelease note : chunk) {
        sql
            .append("\n")
            .append("(")
            .append(note.getId())
            .append(", '")
            .append(note.getNote().replaceAll("'", "''").replaceAll("\n", "\\\\n")) // TODO replace all entity with \n
            .append("', ")
            .append(note.getPosition())
            .append(", ")
            .append(note.getLesson().getId());
        if (chunk.indexOf(note) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("notes.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING NOTES");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING NOTES: ", e);
    }
  }

  private void exportExercises(List<ExerciseRelease> exercises) {
    log.info("START GENERATING EXERCISES");
    List<List<ExerciseRelease>> chunks = ListUtils.partition(exercises, 100);
    StringBuilder sql = new StringBuilder();
    for (List<ExerciseRelease> chunk : chunks) {
      sql.append("INSERT INTO 'exercises' ('id', 'position', 'type', 'name', 'lesson_id', " +
          "'progress', 'skip', 'difficult', 'correct', 'wrong', 'next_repeat', 'series') VALUES ");
      for (ExerciseRelease exercise : chunk) {
        sql
            .append("\n")
            .append("(")
            .append(exercise.getId())
            .append(", ")
            .append(exercise.getPosition())
            .append(", '")
            .append(exercise.getType())
            .append("', '")
            .append(exercise.getName() != null ? exercise.getName().replaceAll("'", "").replaceAll("\"", "") : "")
            .append("', ")
            .append(exercise.getLesson().getId())
            .append(", ")
            .append("-1, 0, 0, 0, 0, 0, 1");
        if (chunk.indexOf(exercise) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("exercises.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING EXERCISES");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING EXERCISES: ", e);
    }
  }

  private void exportExerciseRows(List<ExerciseRowRelease> exercises) {
    log.info("START GENERATING EXERCISE ROWS");
    List<List<ExerciseRowRelease>> chunks = ListUtils.partition(exercises, 100);
    StringBuilder sql = new StringBuilder();
    for (List<ExerciseRowRelease> chunk : chunks) {
      sql.append("INSERT INTO 'exercise_rows' ('id', 'question', 'correct_answer', 'correct_answer_after_choice', 'dialogue_left', 'dialogue_right', " +
          "'first_possible_answer', 'second_possible_answer', 'third_possible_answer', 'forth_possible_answer', 'description', 'exercise_id') VALUES ");

      for (ExerciseRowRelease exercise : chunk) {
        sql
            .append("\n")
            .append("(")
            .append(exercise.getId())
            .append(", '")
            .append(exercise.getQuestion() != null ? exercise.getQuestion().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getCorrectAnswer() != null ? exercise.getCorrectAnswer().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getCorrectAnswerAfterChoice() != null ? exercise.getCorrectAnswerAfterChoice().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getDialogueLeft() != null ? exercise.getDialogueLeft().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getDialogueRight() != null ? exercise.getDialogueRight().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getFirstPossibleAnswer() != null ? exercise.getFirstPossibleAnswer().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getSecondPossibleAnswer() != null ? exercise.getSecondPossibleAnswer().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getThirdPossibleAnswer() != null ? exercise.getThirdPossibleAnswer().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getForthPossibleAnswer() != null ? exercise.getForthPossibleAnswer().replaceAll("'", "''") : "")
            .append("', '")
            .append(exercise.getDescription() != null ? exercise.getDescription().replaceAll("'", "''") : "")
            .append("', ")
            .append(exercise.getExercise().getId());
        if (chunk.indexOf(exercise) + 1 == chunk.size()) {
          sql.append(");\n");
        } else {
          sql.append("),");
        }
      }
    }
    try (PrintWriter out = new PrintWriter("exercise_rows.txt")) {
      out.println(sql.toString());
      log.info("FINISH GENERATING EXERCISE ROWS");
    } catch (Exception e) {
      log.error("ERROR WHILE GENERATING EXERCISE ROWS: ", e);
    }
  }

  public void prepareSounds() throws IOException {

    String source = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio-all\\";
    String desc = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio-lesson\\";
    ArrayList<File> files = new ArrayList<>(Arrays.asList(new File(source)));
    File file = files.get(0);

    ArrayList<File> fileList = new ArrayList<>(Arrays.asList(file.listFiles()));
    List<String> names = fileList.stream().map(File::getName).collect(Collectors.toList());

    List<WordRelease> wordReleases = wordReleaseRepository.findAll().stream().filter(wordRelease -> wordRelease.getLesson().getIsReady()).collect(Collectors.toList());

    for (WordRelease wordRelease : wordReleases) {

      List<String> matchedEnNames = names.stream().filter(name -> name.equals("en_" + convertNameToSoundName(wordRelease.getEnName()) + ".mp3")).collect(Collectors.toList());
      List<String> matchedUsNames = names.stream().filter(name -> name.equals("us_" + convertNameToSoundName(wordRelease.getEnName()) + ".mp3")).collect(Collectors.toList());

      if (matchedEnNames.size() == 1) {
//        log.info("OK {}", wordRelease.getEnName());
      } else {
        log.error("NOT OK {}", wordRelease.getEnName());
      }
    }
  }

  public String convertNameToSoundName(String name) {
    return name.toLowerCase()
        .replaceAll(" ", "_")
        .replaceAll(Pattern.quote("."), "")
        .replaceAll(Pattern.quote(","), "")
        .replaceAll(Pattern.quote(":"), "")
        .replaceAll(Pattern.quote(";"), "")
        .replaceAll(Pattern.quote("!"), "")
        .replaceAll(Pattern.quote("?"), "")
        .replaceAll(Pattern.quote("."), "")
        .replaceAll(Pattern.quote("-"), "")
        .replaceAll(Pattern.quote("'"), "")
        ;
  }


  private static void copyFileUsingStream(File source, File dest) throws IOException {
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(source);
      outputStream = new FileOutputStream(dest);
      byte[] buffer = new byte[4096];
      int length;
      while ((length = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, length);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }

}
