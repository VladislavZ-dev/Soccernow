package pt.ul.fc.css.javafx.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PlaceDTO {
    private long id;
	private String stadium;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime dateTime;


	public PlaceDTO() {}

	public PlaceDTO(long id, String stadium, LocalDateTime dateTime) {
		this.setId(id);
		this.setStadium(stadium);
		this.setDateTime(dateTime);
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}
	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
}
