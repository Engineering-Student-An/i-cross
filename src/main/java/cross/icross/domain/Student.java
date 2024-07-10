package cross.icross.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="student_id")
    private Long id;

    private String loginId;
    private String name;

    private String provider;
    private String providerId;

    private String stuId;
    private String password;

    @ElementCollection
    private List<Integer> announcementPeriod = new ArrayList<>();

    private String announcementStyle;

    private String email;

    @ElementCollection
    private List<Long> subjectList = new ArrayList<>();

    // 공지 스타일 설정
    public void setAnnouncementStyle(String announcementStyle) {
        this.announcementStyle = announcementStyle;
    }

    // 공지 주기 설정
    public void setAnnouncementPeriod(List<Integer> announcementPeriod) {
        this.announcementPeriod.clear();
        this.announcementPeriod.addAll(announcementPeriod);
    }

    // 이메일 설정
    public void setEmail(String email) {
        this.email = email;
    }

    // I-Class 정보 설정
    public void setIclassInfo(String stuId, String password) {
        this.stuId = stuId;
        this.password = password;
    }

    // 공지 주기 설정
    public void addSubject(Long subjectId) {
        this.subjectList.add(subjectId);
    }
}
