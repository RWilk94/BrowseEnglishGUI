package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.repository.ExerciseRowReleaseRepository;

@Service
public class ExerciseRowReleaseService {

  private final ExerciseRowReleaseRepository exerciseRowRepository;

  @Autowired
  public ExerciseRowReleaseService(ExerciseRowReleaseRepository exerciseRowRepository) {
    this.exerciseRowRepository = exerciseRowRepository;
  }
}
