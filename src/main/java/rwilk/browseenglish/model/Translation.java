package rwilk.browseenglish.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Translation implements Serializable {

  String enName;
  String usName;
  List<String> otherNames = new ArrayList<>();
  String comparative; // stopień wyższy
  String superlative; // stopień najwyższy
  String pastTense; // 2 kolumna
  String pastParticiple; // 3 kolumna
  String plural;
  List<Meaning> meanings = new ArrayList<>();

}
