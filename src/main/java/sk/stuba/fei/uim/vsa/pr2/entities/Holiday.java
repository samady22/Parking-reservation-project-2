package sk.stuba.fei.uim.vsa.pr2.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Holiday implements Serializable {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    private String name;
    @Temporal(TemporalType.DATE)
    private Date date;

    public Holiday() {

    }

    public Long getId() {
        return id;
    }

    public Holiday(String name, Date date) {
        this.name = name;
        this.date = date;
    }

    public Holiday(Long id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "\nHoliday = " +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date;
    }
}
