package pt.ul.fc.css.soccernow.dto;

import java.time.LocalDateTime;

public class PlaceDto {

	private long id;
	private String stadium;
	private LocalDateTime dateTime;

	
	public PlaceDto() {}
	
	public PlaceDto(long id, String stadium, LocalDateTime dateTime) {
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
