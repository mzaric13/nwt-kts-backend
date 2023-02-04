package nwt.kts.backend.dto.returnDTO;

import java.sql.Timestamp;

public class DatesChartDTO {

    private Timestamp startDate;
    private Timestamp endDate;

    public DatesChartDTO() {

    }

    public DatesChartDTO(Timestamp startDate, Timestamp endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
