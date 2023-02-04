package nwt.kts.backend.dto.creation;

import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.List;

public class ChartCreationDTO {

    private ChartObjectCreationDTO drivesPerDay;
    private ChartObjectCreationDTO  drivenKilometersPerDay;
    private ChartObjectCreationDTO  moneySpentOrEarnedPerDay;

    public ChartCreationDTO() {

    }

    public ChartCreationDTO(ChartObjectCreationDTO  drivesPerDay,
                            ChartObjectCreationDTO  drivenKilometersPerDay,
                            ChartObjectCreationDTO  moneySpentOrEarnedPerDay) {
        this.drivesPerDay = drivesPerDay;
        this.drivenKilometersPerDay = drivenKilometersPerDay;
        this.moneySpentOrEarnedPerDay = moneySpentOrEarnedPerDay;
    }

    public ChartObjectCreationDTO  getDrivesPerDay() {
        return drivesPerDay;
    }

    public void setDrivesPerDay(ChartObjectCreationDTO drivesPerDay) {
        this.drivesPerDay = drivesPerDay;
    }

    public ChartObjectCreationDTO  getDrivenKilometersPerDay() {
        return drivenKilometersPerDay;
    }

    public void setDrivenKilometresPerDay(ChartObjectCreationDTO  drivenKilometersPerDay) {
        this.drivenKilometersPerDay = drivenKilometersPerDay;
    }

    public ChartObjectCreationDTO  getMoneySpentOrEarnedPerDay() {
        return moneySpentOrEarnedPerDay;
    }

    public void setMoneySpentOrEarnedPerDay(ChartObjectCreationDTO  moneySpentOrEarnedPerDay) {
        this.moneySpentOrEarnedPerDay = moneySpentOrEarnedPerDay;
    }
}
