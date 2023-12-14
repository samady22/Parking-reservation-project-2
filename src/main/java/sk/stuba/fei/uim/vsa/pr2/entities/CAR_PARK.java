package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CAR_PARK implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long carParkId;
    @Column(unique = true, nullable = false)
    private String name;
    private String address;
    private Integer pricePerHour;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "car_park",
            orphanRemoval = true)
    private List<CAR_PARK_FLOOR> car_park_floors = new ArrayList<>();


    public CAR_PARK(String name, String address, Integer pricePerHour) {
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
    }

    public CAR_PARK() {
    }


    public void addCarParkFloor(CAR_PARK_FLOOR cpf) {
        this.car_park_floors.add(cpf);
        cpf.setCar_park(this);
    }

    public void removeCarParkFloor(CAR_PARK_FLOOR cpf) {
        car_park_floors.remove(cpf);
        cpf.setCar_park(null);
    }

    public List<CAR_PARK_FLOOR> getCar_park_floors() {
        return car_park_floors;
    }

    public void setCar_park_floors(List<CAR_PARK_FLOOR> car_park_floors) {
        this.car_park_floors = car_park_floors;
    }

    public long getId() {
        return carParkId;
    }

    public void setId(Long carParkId) {
        this.carParkId = carParkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    @Override
    public String toString() {
        return "\nCAR_PARK = " +
                "carParkId=" + carParkId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", pricePerHour=" + pricePerHour;
    }
}
