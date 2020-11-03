package rwilk.browseenglish.release.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "release_exercise_row")
public class ExerciseRowRelease implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;

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

  @Column(name = "dialogue_left", length = 2000)
  String dialogueLeft;
  @Column(name = "dialogue_right", length = 2000)
  String dialogueRight;

  @Column(name = "description", length = 2000)
  String description;

  @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "exercise_id", nullable = false, referencedColumnName = "id")
  ExerciseRelease exercise;

}
