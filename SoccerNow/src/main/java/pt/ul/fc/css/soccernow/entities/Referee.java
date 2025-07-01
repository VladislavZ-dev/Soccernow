package pt.ul.fc.css.soccernow.entities;

import jakarta.persistence.*;

@Entity
public class Referee extends User {
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Certificate certificate;
	
	public Referee() {}
	
    public Referee(String name, Certificate certificate) {
        super(name);
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return this.certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }
}
