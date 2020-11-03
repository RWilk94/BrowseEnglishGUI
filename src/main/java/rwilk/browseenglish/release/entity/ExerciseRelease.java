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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "release_exercises")
public class ExerciseRelease implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;

  @Column(name = "type", length = 512)
  String type;

  @Column(name = "position")
  Long position;

  @Column(name = "name")
  String name;


  @OneToMany(mappedBy = "exercise", fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
  List<ExerciseRowRelease> exerciseRowReleases;

  @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "lesson_id", nullable = false, referencedColumnName = "id")
  LessonRelease lesson;

}
