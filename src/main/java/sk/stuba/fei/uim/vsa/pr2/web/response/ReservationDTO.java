package sk.stuba.fei.uim.vsa.pr2.web.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReservationDTO {

    private Long id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date start;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date end;
    private Double prices;
    private CarDto2 car;
    private SpotDto2 spot;


    private Map<String, Object> otherProperties;

    @JsonAnyGetter
    public Map<String, Object> getOtherProperties() {
        return otherProperties;
    }

    @JsonAnySetter
    public void addOther(String key, Object value) {
        if (otherProperties == null)
            otherProperties = new HashMap<>();
        otherProperties.put(key, value);
    }

}
