package rwilk.browseenglish.scrapper.etutor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtutorMaskedWritingItem {

  private String question;
  private String answer;
  private String translation;

}
