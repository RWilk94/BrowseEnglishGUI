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
@Table(name = "etutor_courses")
public class EtutorCourse implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false, unique = true)
  Long id;
  @Column(name = "name", length = 2000)
  String name;
  @Column(name = "href", length = 2000)
  String href;

//  @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REFRESH})
//  private List<EtutorLesson> etutorLessons = new ArrayList<>();

  public EtutorCourse(String name) {
    this.name = name;
  }
}


