package rwilk.browseenglish.service;

import org.springframework.stereotype.Service;
import rwilk.browseenglish.model.etutor.EtutorGrammar;
import rwilk.browseenglish.repository.EtutorGrammarRepository;

import java.util.List;

@Service
public class EtutorGrammarService {

  private final EtutorGrammarRepository etutorGrammarRepository;

  public EtutorGrammarService(EtutorGrammarRepository etutorGrammarRepository) {
    this.etutorGrammarRepository = etutorGrammarRepository;
  }

  public List<EtutorGrammar> findAll() {
    return etutorGrammarRepository.findAll();
  }

  public List<EtutorGrammar> findAllByLessonId(Long lessonId) {
    return etutorGrammarRepository.findAllByLesson_Id(lessonId);
  }
}
