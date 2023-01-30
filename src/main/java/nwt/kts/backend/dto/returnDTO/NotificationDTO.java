package nwt.kts.backend.dto.returnDTO;

import java.util.Objects;

public class NotificationDTO {

    private int passengerId;
    private String notificationMessage;

    public NotificationDTO() {

    }

    public NotificationDTO(int passengerId, String notificationMessage) {
        this.passengerId = passengerId;
        this.notificationMessage = notificationMessage;
    }


    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDTO that = (NotificationDTO) o;
        return passengerId == that.passengerId && Objects.equals(notificationMessage, that.notificationMessage);
    }
}
