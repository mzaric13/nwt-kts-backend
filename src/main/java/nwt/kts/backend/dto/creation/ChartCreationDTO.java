package nwt.kts.backend.dto.creation;

import java.sql.Timestamp;
import java.util.Hashtable;

public class ChartCreationDTO {

    private Hashtable<String, Double> drivesPerDay;
    private Hashtable<String, Double> drivenKilometersPerDay;
    private Hashtable<String, Double> moneySpentOrEarnedPerDay;

    public ChartCreationDTO() {

    }

    public ChartCreationDTO(Hashtable<String, Double> drivesPerDay,
                            Hashtable<String, Double> drivenKilometersPerDay,
                            Hashtable<String, Double> moneySpentOrEarnedPerDay) {
        this.drivesPerDay = drivesPerDay;
        this.drivenKilometersPerDay = drivenKilometersPerDay;
        this.moneySpentOrEarnedPerDay = moneySpentOrEarnedPerDay;
    }

    public Hashtable<String, Double> getDrivesPerDay() {
        return drivesPerDay;
    }

    public void setDrivesPerDay(Hashtable<String, Double> drivesPerDay) {
        this.drivesPerDay = drivesPerDay;
    }

    public Hashtable<String, Double> getDrivenKilometersPerDay() {
        return drivenKilometersPerDay;
    }

    public void setDrivenKilometresPerDay(Hashtable<String, Double> drivenKilometersPerDay) {
        this.drivenKilometersPerDay = drivenKilometersPerDay;
    }

    public Hashtable<String, Double> getMoneySpentOrEarnedPerDay() {
        return moneySpentOrEarnedPerDay;
    }

    public void setMoneySpentOrEarnedPerDay(Hashtable<String, Double> moneySpentOrEarnedPerDay) {
        this.moneySpentOrEarnedPerDay = moneySpentOrEarnedPerDay;
    }
}
