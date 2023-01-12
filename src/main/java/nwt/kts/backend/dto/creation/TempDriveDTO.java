package nwt.kts.backend.dto.creation;

import nwt.kts.backend.dto.returnDTO.TagDTO;

import java.sql.Timestamp;
import java.util.Set;

public class TempDriveDTO {

    public Timestamp startDate;
    public double price;
    public double length;
    public Set<TagDTO> tags;
    public Set<String> emails;

    public TempDriveDTO() {
    }

    public TempDriveDTO(Timestamp startDate, double price, double length, Set<TagDTO> tags, Set<String> emails) {
        this.startDate = startDate;
        this.price = price;
        this.length = length;
        this.tags = tags;
        this.emails = emails;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }
}
