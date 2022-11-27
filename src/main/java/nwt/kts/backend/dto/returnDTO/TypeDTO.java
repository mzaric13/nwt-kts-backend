package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Type;

public class TypeDTO {

    private Integer id;
    private String name;

    public TypeDTO() {

    }

    public TypeDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public TypeDTO(Type type) {
        this.id = type.getId();
        this.name = type.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
