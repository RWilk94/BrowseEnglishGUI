package rwilk.browseenglish.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rwilk.browseenglish.model.entity.Course;
import rwilk.browseenglish.model.entity.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

  List<Lesson> findAllByCourse_IsCustom(boolean isCustom);

  List<Lesson> findAllByCourse(Course course);

  List<Lesson> findByPlNameContainsAndCourse_EnNameContains(String plName, String courseEnName);

}
