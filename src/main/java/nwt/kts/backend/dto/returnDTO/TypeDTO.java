package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.Type;

public class TypeDTO {

    private Integer id;
    private String name;
    private double multiplier;

    public TypeDTO() {

    }

    public TypeDTO(Integer id, String name, double multiplier) {
        this.id = id;
        this.name = name;
        this.multiplier = multiplier;
    }

    public TypeDTO(Type type) {
        this.id = type.getId();
        this.name = type.getName();
        this.multiplier = type.getMultiplier();
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

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
