package cross.icross.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Course {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("shortname")
    private String shortname;

    @JsonProperty("idnumber")
    private String idnumber;

    @JsonProperty("fullname")
    private String fullname;

    @JsonProperty("ename")
    private String ename;

    @JsonProperty("format")
    private String format;

    @JsonProperty("startdate")
    private Long startdate;

    @JsonProperty("groupmode")
    private Integer groupmode;

    @JsonProperty("groupmodeforce")
    private Integer groupmodeforce;

    @JsonProperty("lang")
    private String lang;

    @JsonProperty("numsections")
    private Integer numsections;

    @JsonProperty("year")
    private Integer year;

    @JsonProperty("semester_code")
    private String semesterCode;

//    @JsonProperty("roles")
//    private String roles;

    @JsonProperty("setting")
    private Long setting;

    @JsonProperty("course_type")
    private String course_type;

    @JsonProperty("course_type_name")
    private String course_type_name;

    @JsonProperty("campus")
    private String campus;

    @JsonProperty("campus_name")
    private String campus_name;

    @JsonProperty("cu_visible")
    private Long cu_visible;

    @JsonProperty("study_start")
    private String study_start;

    @JsonProperty("study_end")
    private String study_end;

    @JsonProperty("day_cd")
    private String day_cd;

    @JsonProperty("hour1")
    private String hour1;

    @JsonProperty("room_nm")
    private String room_nm;

    @JsonProperty("bunban_code")
    private String bunban_code;

    @JsonProperty("authentication")
    private Long authentication;

}
