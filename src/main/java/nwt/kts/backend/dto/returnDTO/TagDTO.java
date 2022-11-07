package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Tag;

public class TagDTO {

    private Integer id;

    private String name;

    public TagDTO() {
    }

    public TagDTO(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
