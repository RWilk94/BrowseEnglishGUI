package rwilk.browseenglish.model.entity;

import lombok.*;
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
@Table(name = "words")
public class Word implements Serializable {

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
  @Column(name = "sound")
  String sound;

  @Column(name = "is_sentence_scrapped")
  boolean isSentenceScrapped;
  @Column(name = "is_word_scrapped")
  boolean isWordScrapped;
  @Column(name = "is_word_ready")
  boolean isWordReady;
  @Column(name = "is_word_skipped")
  boolean isWordSkipped;
  @Column(name = "is_word_with_sentence")
  boolean isWordWithSentence;
  @Column(name = "is_word_grouped")
  boolean isWordGrouped;

  @Column(name = "frequency")
  Long frequency;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private Lesson lesson;

//  @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
//  private List<Sentence> sentences;

  @Transient
  private List<DikiWord> dikiWords;

  @Transient
  private List<Sentence> sentences;

  @Override
  public String toString() {
    return enName + " (" + plName + ")";
  }
}
