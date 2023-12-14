package sk.stuba.fei.uim.vsa.pr2.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "USER")
public class User implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long userId;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CAR> cars = new ArrayList<>();


    public void addCar(CAR car1) {
        this.cars.add(car1);
        car1.setUser(this);
    }

    public void removeCar(CAR car1) {
        cars.remove(car1);
        car1.setUser(null);
    }

    public User(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User(Long userId, String firstname, String lastname, String email) {
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }

    public User() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<CAR> getCars() {
        return cars;
    }

    public void setCars(List<CAR> cars) {
        this.cars = cars;
    }

    @Override
    public String toString() {
        return "\nUser = " +
                "userId=" + userId +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'';
    }
}
