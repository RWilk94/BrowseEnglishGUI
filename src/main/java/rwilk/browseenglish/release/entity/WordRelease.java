package rwilk.browseenglish.release.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import rwilk.browseenglish.model.diki.DikiWord;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "release_words")
public class WordRelease implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "polish_name", length = 2000)
  String plName;
  @Column(name = "english_name", length = 2000)
  String enName; // British Name
  @Column(name = "american_name", length = 2000)
  String usName; // American Name
  @Column(name = "other_name", length = 2000)
  String otherNames; // rest of variants
  @Column(name = "part_of_speech")
  String partOfSpeech;
  @Column(name = "article")
  String article;
  @Column(name = "grammar_type")
  String grammarType; // [TRANSITIVE] – czasowniki przechodnie; [INTRANSITIVE] – czasowniki nieprzechodnie;
  // [COUNTABLE] – rzeczowniki policzalne;                      [UNCOUNTABLE] – rzeczowniki niepoliczalne;
  // [SINGULAR] – rzeczowniki tylko w liczbie pojedynczej;      [PLURAL] – rzeczowniki tylko w liczbie mnogiej
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
  @Column(name = "synonym")
  String synonym;
  @Column(name = "level")
  String level; // means from which level is word (A1 - C2)
  @Column(name = "source")
  String source;
  @Column(name = "sound", length = 2000)
  String sound;
  @Column(name = "position")
  Long position;

  @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private LessonRelease lesson;

  @Transient
  private List<DikiWord> dikiWords;

}
