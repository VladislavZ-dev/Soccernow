package pt.ul.fc.css.soccernow.dto;
import pt.ul.fc.css.soccernow.entities.Certificate;

public class RefereeDTO extends UserDTO {
    private Certificate certificate;
    
    public RefereeDTO() {}
    
    public RefereeDTO(String name, Long id, Certificate certificate) {
        super(name, id);
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public void setCertificate(Certificate certificate) {
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