package rwilk.browseenglish.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rwilk.browseenglish.model.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

  List<Course> findAllByIsCustomIsTrue();

}
