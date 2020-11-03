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
import rwilk.browseenglish.model.entity.Word;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "diki_words")
public class DikiWord implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "english_name", length = 2000)
  String enName;
  @Column(name = "american_name", length = 2000)
  String usName;
  @Column(name = "other_name", length = 2000)
  String otherNames;
  @Column(name = "comparative")
  String comparative; // stopień wyższy
  @Column(name = "superlative")
  String superlative; // stopień najwyższy
  @Column(name = "past_tense")
  String pastTense; // 2 kolumna
  @Column(name = "past_participle")
  String pastParticiple; // 3 kolumna
  @Column(name = "plural")
  String plural;
  @Column(name = "source")
  String source;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "word_id", nullable = false, referencedColumnName = "id")
  private Word word;

  @Transient
  private List<DikiTranslation> dikiTranslations = new ArrayList<>();

  // List<Meaning> meanings = new ArrayList<>();

}
