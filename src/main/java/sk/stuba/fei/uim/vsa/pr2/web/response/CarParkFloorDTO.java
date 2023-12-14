package sk.stuba.fei.uim.vsa.pr2.web.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CarParkFloorDTO{

    private Long id;
    private String identifier;
    private Long carPark;
    private List<ParkingSpotDTO> spots =new ArrayList<>();

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
