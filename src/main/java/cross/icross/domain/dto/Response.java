package cross.icross.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Response {
    private String result;
    private List<Course> data;


    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("data")
    public List<Course> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<Course> data) {
        this.data = data;
    }
}
