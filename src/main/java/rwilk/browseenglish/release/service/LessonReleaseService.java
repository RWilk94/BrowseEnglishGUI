package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.repository.LessonReleaseRepository;

@Service
public class LessonReleaseService {

  private final LessonReleaseRepository lessonRepository;

  @Autowired
  public LessonReleaseService(LessonReleaseRepository lessonRepository) {
    this.lessonRepository = lessonRepository;
  }
}
