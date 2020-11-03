package rwilk.browseenglish.model.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "lessons")
public class Lesson implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "english_name", length = 2000)
  String enName;
  @Column(name = "polish_name", length = 2000)
  String plName;
  @Column(name = "href", length = 256)
  String href;
  @Column(name = "is_custom")
  boolean isCustom;
  @Column(name = "is_ready")
  boolean isReady;

  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
  @JoinColumn(name = "course_id", nullable = false, referencedColumnName = "id")
  Course course;

  @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
  //lesson is name of column declared in Word model.
  private List<Word> words = new ArrayList<>();

  @Override
  public String toString() {
    return "[" + id + "] " + enName;
  }
}
