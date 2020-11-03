package rwilk.browseenglish.model.etutor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "etutor_grammar")
public class EtutorGrammar implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "grammar_type", length = 512)
  String grammarType;
  // note
  @Column(name = "note", length = 2000)
  String note;
  // choice item
  @Column(name = "question", length = 512)
  String question;
  @Column(name = "correct_answer", length = 512)
  String correctAnswer;
  @Column(name = "correct_answer_after_choice", length = 512)
  String correctAnswerAfterChoice;
  @Column(name = "first_possible_answer", length = 512)
  String firstPossibleAnswer;
  @Column(name = "second_possible_answer", length = 512)
  String secondPossibleAnswer;
  @Column(name = "third_possible_answer", length = 512)
  String thirdPossibleAnswer;
  @Column(name = "forth_possible_answer", length = 512)
  String forthPossibleAnswer;
  // masked writting
  // String question;
  // String correctAnswer;
  @Column(name = "translation", length = 512)
  String translation;
  // dialogue
  @Column(name = "dialogue_left", length = 2000)
  String dialogueLeft;
  @Column(name = "dialogue_right", length = 2000)
  String dialogueRight;
  // wordList
  @Column(name = "word_list_left", length = 512)
  String wordListLeft;
  @Column(name = "word_list_right", length = 512)
  String wordListRight;

  @Column(name = "name")
  String name;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  private EtutorLesson lesson;

}
