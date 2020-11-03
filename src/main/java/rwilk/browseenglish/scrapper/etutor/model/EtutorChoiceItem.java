package rwilk.browseenglish.scrapper.etutor.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtutorChoiceItem {

  private String question;
  private String correctAnswer;
  private String correctAnswerAfterChoice;
  private List<String> possibleAnswers;

}
