package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.repository.CourseReleaseRepository;

@Service
public class CourseReleaseService {

  private final CourseReleaseRepository courseRepository;

  @Autowired
  public CourseReleaseService(CourseReleaseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }
}
