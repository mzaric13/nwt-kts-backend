package nwt.kts.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    public Role() {

    }

    public Role(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
