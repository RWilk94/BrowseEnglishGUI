package rwilk.browseenglish.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rwilk.browseenglish.model.entity.Sentence;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentenceView implements Serializable {

  private Long id;
  private String enSentence;
  private String plSentence;

  public SentenceView(Sentence sentence) {
    this.id = sentence.getId();
    this.enSentence = sentence.getEnSentence();
    this.plSentence = sentence.getPlSentence();
  }

  @Override
  public String toString() {
    return id + ". " + enSentence + " (" + plSentence + ")";
  }
}
