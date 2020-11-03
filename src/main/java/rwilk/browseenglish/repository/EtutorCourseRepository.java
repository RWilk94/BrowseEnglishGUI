package rwilk.browseenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.etutor.EtutorCourse;

@Repository
public interface EtutorCourseRepository extends JpaRepository<EtutorCourse, Long> {
}
