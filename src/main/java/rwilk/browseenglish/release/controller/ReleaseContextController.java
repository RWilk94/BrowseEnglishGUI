package rwilk.browseenglish.release.controller;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import rwilk.browseenglish.release.repository.CourseReleaseRepository;
import rwilk.browseenglish.release.repository.ExerciseReleaseRepository;
import rwilk.browseenglish.release.repository.ExerciseRowReleaseRepository;
import rwilk.browseenglish.release.repository.LessonReleaseRepository;
import rwilk.browseenglish.release.repository.NoteReleaseRepository;
import rwilk.browseenglish.release.repository.SentenceReleaseRepository;
import rwilk.browseenglish.release.repository.WordReleaseRepository;
import rwilk.browseenglish.release.service.CourseReleaseService;
import rwilk.browseenglish.release.service.ExerciseReleaseService;
import rwilk.browseenglish.release.service.ExerciseRowReleaseService;
import rwilk.browseenglish.release.service.LessonReleaseService;
import rwilk.browseenglish.release.service.NoteReleaseService;
import rwilk.browseenglish.release.service.SentenceReleaseService;
import rwilk.browseenglish.release.service.WordReleaseService;
import rwilk.browseenglish.repository.EtutorCourseRepository;
import rwilk.browseenglish.repository.EtutorLessonRepository;
import rwilk.browseenglish.service.CourseService;
import rwilk.browseenglish.service.EtutorGrammarService;
import rwilk.browseenglish.service.LessonService;
import rwilk.browseenglish.service.WordService;

@Data
@Controller
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReleaseContextController {

  EtutorCourseRepository etutorCourseRepository;
  EtutorLessonRepository etutorLessonRepository;

  EtutorGrammarService etutorGrammarService;
  CourseService courseService;
  LessonService lessonService;
  WordService wordService;

  CourseReleaseService courseReleaseService;
  CourseReleaseRepository courseReleaseRepository;
  ExerciseReleaseService exerciseReleaseService;
  ExerciseReleaseRepository exerciseReleaseRepository;
  ExerciseRowReleaseService exerciseRowReleaseService;
  ExerciseRowReleaseRepository exerciseRowReleaseRepository;
  LessonReleaseService lessonReleaseService;
  LessonReleaseRepository lessonReleaseRepository;
  NoteReleaseService noteReleaseService;
  NoteReleaseRepository noteReleaseRepository;
  SentenceReleaseService sentenceReleaseService;
  SentenceReleaseRepository sentenceReleaseRepository;
  WordReleaseService wordReleaseService;
  WordReleaseRepository wordReleaseRepository;

  public ReleaseContextController(EtutorCourseRepository etutorCourseRepository, EtutorLessonRepository etutorLessonRepository, EtutorGrammarService etutorGrammarService, CourseService courseService, LessonService lessonService, WordService wordService, CourseReleaseService courseReleaseService, CourseReleaseRepository courseReleaseRepository, ExerciseReleaseService exerciseReleaseService, ExerciseReleaseRepository exerciseReleaseRepository, ExerciseRowReleaseService exerciseRowReleaseService, ExerciseRowReleaseRepository exerciseRowReleaseRepository, LessonReleaseService lessonReleaseService, LessonReleaseRepository lessonReleaseRepository, NoteReleaseService noteReleaseService, NoteReleaseRepository noteReleaseRepository, SentenceReleaseService sentenceReleaseService, SentenceReleaseRepository sentenceReleaseRepository, WordReleaseService wordReleaseService, WordReleaseRepository wordReleaseRepository) {
    this.etutorCourseRepository = etutorCourseRepository;
    this.etutorLessonRepository = etutorLessonRepository;
    this.etutorGrammarService = etutorGrammarService;
    this.courseService = courseService;
    this.lessonService = lessonService;
    this.wordService = wordService;
    this.courseReleaseService = courseReleaseService;
    this.courseReleaseRepository = courseReleaseRepository;
    this.exerciseReleaseService = exerciseReleaseService;
    this.exerciseReleaseRepository = exerciseReleaseRepository;
    this.exerciseRowReleaseService = exerciseRowReleaseService;
    this.exerciseRowReleaseRepository = exerciseRowReleaseRepository;
    this.lessonReleaseService = lessonReleaseService;
    this.lessonReleaseRepository = lessonReleaseRepository;
    this.noteReleaseService = noteReleaseService;
    this.noteReleaseRepository = noteReleaseRepository;
    this.sentenceReleaseService = sentenceReleaseService;
    this.sentenceReleaseRepository = sentenceReleaseRepository;
    this.wordReleaseService = wordReleaseService;
    this.wordReleaseRepository = wordReleaseRepository;
  }

  private ReleaseTableController releaseTableController = null;
  private ReleaseFormController releaseFormController = null;

//  public void setWordReleaseTableController(WordReleaseTableController wordReleaseTableController) {
//    this.wordReleaseTableController = wordReleaseTableController;
//  }

  // WordReleaseTableController wordReleaseTableController;

}
