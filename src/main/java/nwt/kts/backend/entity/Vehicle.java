package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name="vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(name="name", nullable=false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="type_id")
    private Type type;

    public Vehicle(){

    }

    public Vehicle(Integer id, String name, Type type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
