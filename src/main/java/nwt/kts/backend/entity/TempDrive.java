package nwt.kts.backend.entity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "temp_drives")
public class TempDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "length", nullable = false)
    private double length;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "temp_drive_tags", joinColumns = @JoinColumn(name = "temp_drive_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    private Set<Tag> tags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "temp_drive_passengers", joinColumns = @JoinColumn(name = "temp_drive_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"))
    private Set<Passenger> passengers;
}
