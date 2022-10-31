package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name="vehicle_types")
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    @Column(name="name", nullable=false)
    private String name;

    public Type(){

    }

    public Type(Integer id, String name){
        this.id = id;
        this.name = name;
    }

}
