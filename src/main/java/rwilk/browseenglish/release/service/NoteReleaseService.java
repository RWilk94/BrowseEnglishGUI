package rwilk.browseenglish.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rwilk.browseenglish.release.repository.NoteReleaseRepository;

@Service
public class NoteReleaseService {

  private final NoteReleaseRepository noteRepository;

  @Autowired
  public NoteReleaseService(NoteReleaseRepository noteRepository) {
    this.noteRepository = noteRepository;
  }
}
