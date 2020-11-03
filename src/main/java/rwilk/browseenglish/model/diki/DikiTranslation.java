package rwilk.browseenglish.model.diki;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Entity
@Table(name = "diki_translations")
public class DikiTranslation implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "polish_name", length = 2000)
  String plName;
  @Column(name = "part_of_speech")
  String partOfSpeech;
  @Column(name = "grammar_type")
  String grammarType; // [TRANSITIVE] – czasowniki przechodnie; [INTRANSITIVE] – czasowniki nieprzechodnie;
  // [COUNTABLE] – rzeczowniki policzalne;                      [UNCOUNTABLE] – rzeczowniki niepoliczalne;
  // [SINGULAR] – rzeczowniki tylko w liczbie pojedynczej;      [PLURAL] – rzeczowniki tylko w liczbie mnogiej

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "diki_word_id", nullable = false, referencedColumnName = "id")
  private DikiWord dikiWord;

  @Transient
  private List<DikiSentence> dikiSentences = new ArrayList<>();

}
