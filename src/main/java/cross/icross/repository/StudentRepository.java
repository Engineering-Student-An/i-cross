package cross.icross.repository;

import cross.icross.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findStudentById(Long id);

    Student findByLoginId(String loginId);

    @Query("SELECT s FROM Student s WHERE :period MEMBER OF s.announcementPeriod")
    List<Student> findStudentsByAnnouncementPeriod(@Param("period")Integer period);

}
