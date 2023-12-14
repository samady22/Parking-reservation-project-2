package sk.stuba.fei.uim.vsa.pr2.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class PARKING_SPOT implements Serializable {
    private static final long serialVersionUID = 1L;
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long parkingSpotId;

    private String spotIdentifier;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CARPARID", referencedColumnName = "CARPARKID"),
            @JoinColumn(name = "FLOORIDENTIFIER", referencedColumnName = "FLOORIDENTIFIER")

    })
    private CAR_PARK_FLOOR car_park_floor;


    @OneToMany(mappedBy = "parking_spot", fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();

    public PARKING_SPOT(String spotIdentifier) {
        this.spotIdentifier = spotIdentifier;
    }

    public PARKING_SPOT(Long parkingSpotId, String spotIdentifier) {
        this.parkingSpotId = parkingSpotId;
        this.spotIdentifier = spotIdentifier;
    }

    public PARKING_SPOT() {
    }


    public void addReservation(Reservation res) {
        this.reservations.add(res);
        res.setParking_spot(this);
    }

    public void removeReservation(Reservation rs) {
        reservations.remove(rs);
        rs.setParking_spot(null);
    }

    public Long getParkingSpotId() {
        return parkingSpotId;
    }

    public void setParkingSpotId(Long parkingSpotId) {
        this.parkingSpotId = parkingSpotId;
    }

    public List<Reservation> getReservation() {
        return reservations;
    }

    public void setReservation(List<Reservation> reservation) {
        this.reservations = reservation;
    }


    public CAR_PARK_FLOOR getCar_park_floor() {
        return car_park_floor;
    }


    public void setCar_park_floor(CAR_PARK_FLOOR car_park_floor) {
        this.car_park_floor = car_park_floor;
    }


    public String getSpotIdentifier() {
        return spotIdentifier;
    }

    public void setSpotIdentifier(String spotIdentifier) {
        this.spotIdentifier = spotIdentifier;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PARKING_SPOT that = (PARKING_SPOT) o;
        return Objects.equals(parkingSpotId, that.parkingSpotId) && Objects.equals(spotIdentifier, that.spotIdentifier) && Objects.equals(car_park_floor, that.car_park_floor) && Objects.equals(reservations, that.reservations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parkingSpotId, spotIdentifier, car_park_floor, reservations);
    }

    @Override
    public String toString() {
        return "\nPARKING_SPOT = " +
                "parkingSpotId=" + parkingSpotId +
                ", spotIdentifier='" + spotIdentifier + '\'' +
                ", " + car_park_floor;
    }
}
