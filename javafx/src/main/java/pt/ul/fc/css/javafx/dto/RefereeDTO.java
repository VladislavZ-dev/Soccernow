package pt.ul.fc.css.javafx.dto;

public class RefereeDTO extends UserDTO {
    private String certificate;

    public RefereeDTO() {}

    public RefereeDTO(String name, Long id, String certificate) {
        super(name, id);
        this.certificate = certificate;
    }

    public String getCertificate() {
        return this.certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    @Override
    public boolean equals(Object obj) {
    	if (obj instanceof RefereeDTO) {
            return super.equals(obj) && certificate == ((RefereeDTO) obj).certificate;
        }
    	return false;
    }

    @Override
    public int hashCode() {
    	return super.hashCode() + certificate.hashCode();
    }
}
