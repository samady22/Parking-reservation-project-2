package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CAR_PARK_FLOOR implements Serializable {
    private static final long serialVersionUID = 1L;


    @EmbeddedId
    private CPF_ID id;

    @ManyToOne
    @MapsId("carParkId")
    @JoinColumn(name = "CARPARKID")
    private CAR_PARK car_park;

    @OneToMany(mappedBy = "car_park_floor", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PARKING_SPOT> parking_spots = new ArrayList<>();


    public CAR_PARK_FLOOR() {
    }

    public CAR_PARK_FLOOR(CPF_ID id) {
        this.id = id;
    }

    public CPF_ID getId() {
        return id;
    }

    public void setId(CPF_ID id) {
        this.id = id;
    }

    public CAR_PARK getCar_park() {
        return car_park;
    }

    public void setCar_park(CAR_PARK car_park) {
        this.car_park = car_park;
    }

    public List<PARKING_SPOT> getParking_spots() {
        return parking_spots;
    }

    public void setParking_spots(List<PARKING_SPOT> parking_spots) {
        this.parking_spots = parking_spots;
    }

    public void addParkingSpot(PARKING_SPOT ps) {
        this.parking_spots.add(ps);
        ps.setCar_park_floor(this);
    }

    public void removeParkingSpot(PARKING_SPOT ps) {
        parking_spots.remove(ps);
        ps.setCar_park_floor(null);
    }


    @Override
    public String toString() {
        return "\nCAR_PARK_FLOOR = " +
                "id=" + id.getCarParkId() + ", floorIdentifier= " + id.getFloorIdentifier() +
                ", " + car_park;
    }
}
