package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.repository.ExerciseReleaseRepository;

@Service
public class ExerciseReleaseService {

  private final ExerciseReleaseRepository exerciseRepository;

  @Autowired
  public ExerciseReleaseService(ExerciseReleaseRepository exerciseRepository) {
    this.exerciseRepository = exerciseRepository;
  }
}
