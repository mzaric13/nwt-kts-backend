package nwt.kts.backend.dto.creation;

import java.util.List;

public class ChartObjectCreationDTO {

    private String name;
    private List<SeriesObjectCreationDTO> series;

    public ChartObjectCreationDTO() {

    }

    public ChartObjectCreationDTO(String name, List<SeriesObjectCreationDTO> series) {
        this.name = name;
        this.series = series;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SeriesObjectCreationDTO> getSeries() {
        return series;
    }

    public void setSeries(List<SeriesObjectCreationDTO> series) {
        this.series = series;
    }
}
