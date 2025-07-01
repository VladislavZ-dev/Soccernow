package pt.ul.fc.css.javafx.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pt.ul.fc.css.javafx.dto.RefereeDTO;

public class RefereeModel {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty certificate = new SimpleStringProperty();

    public RefereeModel(RefereeDTO dto) {
        this.id = new SimpleIntegerProperty((int) dto.getId());
        this.name = new SimpleStringProperty(dto.getName());
        this.certificate = new SimpleStringProperty(dto.getCertificate());
    }

    public int getId() {
        return id.get();

    }

    public void setId(int value) {
        id.set(value);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String value) {
        name.set(value);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCertificate() {
        return certificate.get();
    }

    public void setcertificate(String value) {
        certificate.set(value);
    }

    public StringProperty certificateProperty() {
        return certificate;
    }
}
