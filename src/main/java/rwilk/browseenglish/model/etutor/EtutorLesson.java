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
@Table(name = "etutor_lessons")
public class EtutorLesson implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "name", length = 2000)
  String lessonName;
  @Column(name = "href")
  String lessonHref;
  @Column(name = "is_exercise")
  boolean isExercise;
  @Column(name = "level")
  String level;
  @Column(name = "is_ready")
  boolean isReady;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
  private EtutorCourse course;

}
