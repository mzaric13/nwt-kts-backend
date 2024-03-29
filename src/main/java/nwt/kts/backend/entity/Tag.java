package nwt.kts.backend.entity;

import nwt.kts.backend.dto.returnDTO.TagDTO;

import javax.persistence.*;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    public Tag() {

    }

    public Tag(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(TagDTO tagDTO) {
        this.id = tagDTO.getId();
        this.name = tagDTO.getName();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
