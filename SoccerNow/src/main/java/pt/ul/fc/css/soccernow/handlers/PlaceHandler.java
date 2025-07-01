package pt.ul.fc.css.soccernow.handlers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ul.fc.css.soccernow.dto.PlaceDto;
import pt.ul.fc.css.soccernow.entities.Place;
import pt.ul.fc.css.soccernow.repositories.PlaceRepository;

@Service
public class PlaceHandler {
	@Autowired
    private PlaceRepository PlaceRepository;

    public List<PlaceDto> getAllPlaces() {
        List<Place> Places = PlaceRepository.findAll();
        return Places.stream()
                .map(this::mapToDto)
                .toList();
    }

    public PlaceDto savePlace(Place Place) {
        return mapToDto(PlaceRepository.save(Place));
    }

    public PlaceDto getPlaceById(long id) {
        Optional<Place> PlaceOptional = PlaceRepository.findById(id);
        if (PlaceOptional.isEmpty()) {
            return null;
        }
        return mapToDto(PlaceOptional.get());
    }

    public PlaceDto mapToDto(Place Place) {
        PlaceDto PlaceDto = new PlaceDto();
        PlaceDto.setId(Place.getId());
        PlaceDto.setStadium(Place.getStadium());
        PlaceDto.setDateTime(Place.getDateTime());
        return PlaceDto;
    }

    public PlaceDto createPlace(PlaceDto PlaceDto) {
    	if (!isPlaceAvailableWithTimeGap(PlaceDto.getStadium(), PlaceDto.getDateTime())) {
    	    throw new IllegalArgumentException("A place is already registered at this stadium within 2h30min of this time.");
    	}
        Place Place = new Place();
        Place.setId(PlaceDto.getId());
        Place.setStadium(PlaceDto.getStadium());
        Place.setDateTime(PlaceDto.getDateTime());

        Place savedPlace = PlaceRepository.save(Place);

        PlaceDto.setId(savedPlace.getId());
        return PlaceDto;
    }

    public boolean isPlaceAvailableWithTimeGap(String stadium, LocalDateTime dateTime) {
    if (!PlaceRepository.existsByStadium(stadium)) {
        return true;
    }

    LocalDateTime start = dateTime.minusMinutes(150);
    LocalDateTime end = dateTime.plusMinutes(150);
    return !PlaceRepository.existsConflictingPlace(stadium, start, end);
}
}
