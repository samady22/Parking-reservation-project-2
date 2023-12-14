package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CPF_ID implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long carParkId;
    private String floorIdentifier;

    public CPF_ID() {
    }

    public CPF_ID(Long carParkId, String floorIdentifier) {
        this.carParkId = carParkId;
        this.floorIdentifier = floorIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPF_ID that = (CPF_ID) o;
        return Objects.equals(carParkId, that.carParkId) && Objects.equals(floorIdentifier, that.floorIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carParkId, floorIdentifier);
    }

    public Long getCarParkId() {
        return carParkId;
    }

    public void setCarParkId(Long carParkId) {
        this.carParkId = carParkId;
    }

    public String getFloorIdentifier() {
        return floorIdentifier;
    }

    public void setFloorIdentifier(String floorIdentifier) {
        this.floorIdentifier = floorIdentifier;
    }
}
