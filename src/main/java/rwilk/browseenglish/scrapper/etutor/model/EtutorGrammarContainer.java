package rwilk.browseenglish.scrapper.etutor.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtutorGrammarContainer {

  private List<String> notes = new ArrayList<>();
  private List<EtutorChoiceItem> choices = new ArrayList<>();
  private List<EtutorMaskedWritingItem> maskedWriting = new ArrayList<>();
  private List<Pair<String, String>> dialogue = new ArrayList<>();
  private List<Pair<String, String>> wordList = new ArrayList<>();

}
