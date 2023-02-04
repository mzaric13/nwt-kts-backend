package nwt.kts.backend.dto.returnDTO;

import nwt.kts.backend.entity.ImageData;


public class ImageDataDTO {

    private Long id;

    private String name;

    private String type;

    private byte[] imageData;

    public ImageDataDTO() {
    }

    public ImageDataDTO(ImageData imageData) {
        this.id = imageData.getId();
        this.name = imageData.getName();
        this.type = imageData.getType();
        this.imageData = imageData.getImageData();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public byte[] getImageData() {
        return imageData;
    }
}
