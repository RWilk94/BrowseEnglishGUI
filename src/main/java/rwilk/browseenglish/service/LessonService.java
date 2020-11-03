package rwilk.browseenglish.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;
import rwilk.browseenglish.repository.LessonRepository;

@Service
public class LessonService {

  private final LessonRepository lessonRepository;

  public LessonService(LessonRepository lessonRepository) {
    this.lessonRepository = lessonRepository;
  }

  public Optional<Lesson> findById(Long id) {
    return lessonRepository.findById(id);
  }

  public List<Lesson> findAllByCourse(Course course) {
    return lessonRepository.findAllByCourse(course);
  }

  public Lesson save(Lesson lesson) {
    return lessonRepository.save(lesson);
  }

  public void delete(Lesson lesson) {
    lessonRepository.delete(lesson);
  }

  public List<Lesson> findAll() {
    return lessonRepository.findAllByCourse_IsCustom(true);
  }
}
