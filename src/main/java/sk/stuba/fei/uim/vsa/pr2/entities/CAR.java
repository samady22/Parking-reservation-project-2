package sk.stuba.fei.uim.vsa.pr2.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CAR implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long carId;
    private String brand;
    private String model;
    private String color;
    @Column(unique = true, nullable = false)
    private String vehicleRegistrationPlate;

    @ManyToOne
    @JoinColumn(name = "USERID")
    private User user;


    @OneToMany(mappedBy = "car", fetch = FetchType.LAZY)
    private List<Reservation> reservation = new ArrayList<>();

    public CAR() {
    }

    public CAR(String brand, String model, String color, String vehicleRegistrationPlate) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
    }

    public void addReservation(Reservation res) {
        this.reservation.add(res);
        res.setCar(this);
    }

    public void removeReservation(Reservation rs) {
        reservation.remove(rs);
        rs.setCar(null);
    }

    public List<Reservation> getReservation() {
        return reservation;
    }

    public void setReservation(List<Reservation> reservation) {
        this.reservation = reservation;
    }

    public CAR(Long carId, String brand, String model, String color, String vehicleRegistrationPlate) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVehicleRegistrationPlate() {
        return vehicleRegistrationPlate;
    }

    public void setVehicleRegistrationPlate(String vehicleRegistrationPlate) {
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "\nCAR = " +
                "carId=" + carId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", vehicleRegistrationPlate='" + vehicleRegistrationPlate;
//                + '\'' +
//                ", user=" + user;
    }
}
