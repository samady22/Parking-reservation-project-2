package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Reservation implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long reservationId;

    @ManyToOne
    @JoinColumn(name = "PARKINGSPOTID")
    private PARKING_SPOT parking_spot;

    @ManyToOne
    @JoinColumn(name = "CARID")
    private CAR car;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    private Double price;


    public Reservation(Date date) {
        this.startDate = date;
    }

    public Reservation(Long reservationId, Date startDate) {
        this.reservationId = reservationId;
        this.startDate = startDate;
    }

    public Reservation() {

    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public Date getDate() {
        return startDate;
    }

    public void setDate(Date date) {
        this.startDate = date;
    }


    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public PARKING_SPOT getParking_spot() {
        return parking_spot;
    }

    public void setParking_spot(PARKING_SPOT parking_spot) {
        this.parking_spot = parking_spot;
    }

    public CAR getCar() {
        return car;
    }

    public void setCar(CAR car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "\nReservation = " +
                "reservationId=" + reservationId +
                ", parking_spot=" + parking_spot +
                ", car=" + car +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", price=" + price;
    }
}
