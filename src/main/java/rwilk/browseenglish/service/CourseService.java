package rwilk.browseenglish.service;

import org.springframework.stereotype.Service;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

  private final CourseRepository courseRepository;

  public CourseService(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  public List<Course> findAll() {
    return courseRepository.findAll();
  }

  public Optional<Course> findById(Long id) {
    return courseRepository.findById(id);
  }

  public List<Course> findAllByIsCustomIsTrue() {
    return courseRepository.findAllByIsCustomIsTrue();
  }

  public Course save(Course course) {
    return courseRepository.save(course);
  }

  public void delete(Course course) {
    courseRepository.delete(course);
  }

}
