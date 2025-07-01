package pt.ul.fc.css.soccernow.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.dto.PlaceDto;
import pt.ul.fc.css.soccernow.entities.Place;
import pt.ul.fc.css.soccernow.repositories.PlaceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceHandlerTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceHandler placeHandler;

    private Place testPlace;
    private PlaceDto testPlaceDto;

    @BeforeEach
    void setUp() {
        testPlace = new Place();
        testPlace.setId(1L);
        testPlace.setStadium("Test Stadium");
        testPlace.setDateTime(LocalDateTime.now().plusHours(2));

        testPlaceDto = new PlaceDto();
        testPlaceDto.setId(1L);
        testPlaceDto.setStadium("Test Stadium");
        testPlaceDto.setDateTime(LocalDateTime.now().plusHours(2));
    }

    @Test
    void getAllPlaces_ShouldReturnAllPlaces() {
        when(placeRepository.findAll()).thenReturn(List.of(testPlace));

        List<PlaceDto> result = placeHandler.getAllPlaces();

        assertEquals(1, result.size());
        assertEquals("Test Stadium", result.get(0).getStadium());
        verify(placeRepository).findAll();
    }

    @Test
    void savePlace_ShouldSaveAndReturnDto() {
        when(placeRepository.save(any(Place.class))).thenReturn(testPlace);

        PlaceDto result = placeHandler.savePlace(testPlace);

        assertEquals(1L, result.getId());
        verify(placeRepository).save(testPlace);
    }

    @Test
    void getPlaceById_ShouldReturnPlace() {
        when(placeRepository.findById(1L)).thenReturn(Optional.of(testPlace));

        PlaceDto result = placeHandler.getPlaceById(1L);

        assertNotNull(result);
        assertEquals("Test Stadium", result.getStadium());
    }

    @Test
    void getPlaceById_ShouldReturnNullWhenNotFound() {
        when(placeRepository.findById(1L)).thenReturn(Optional.empty());

        PlaceDto result = placeHandler.getPlaceById(1L);

        assertNull(result);
    }

    @Test
    void mapToDto_ShouldMapCorrectly() {
        PlaceDto result = placeHandler.mapToDto(testPlace);

        assertEquals(1L, result.getId());
        assertEquals("Test Stadium", result.getStadium());
        assertNotNull(result.getDateTime());
    }

    @Test
    void createPlace_ShouldCreateNewPlace() {
        when(placeRepository.existsByStadium("Test Stadium")).thenReturn(false);
        when(placeRepository.save(any(Place.class))).thenReturn(testPlace);

        PlaceDto result = placeHandler.createPlace(testPlaceDto);

        assertEquals(1L, result.getId());
        verify(placeRepository).save(any(Place.class));
    }

    @Test
    void createPlace_ShouldThrowWhenTimeConflictExists() {
        when(placeRepository.existsByStadium("Test Stadium")).thenReturn(true);
        when(placeRepository.existsConflictingPlace(anyString(), any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
            placeHandler.createPlace(testPlaceDto));
    }

    @Test
    void isPlaceAvailableWithTimeGap_ShouldReturnTrueWhenNoMatchesAtStadium() {
        when(placeRepository.existsByStadium("Empty Stadium")).thenReturn(false);

        boolean result = placeHandler.isPlaceAvailableWithTimeGap("Empty Stadium", LocalDateTime.now());

        assertTrue(result);
        verify(placeRepository, never()).existsConflictingPlace(any(), any(), any());
    }

    @Test
    void isPlaceAvailableWithTimeGap_ShouldReturnFalseWhenConflictExists() {
        LocalDateTime testTime = LocalDateTime.now();
        when(placeRepository.existsByStadium("Test Stadium")).thenReturn(true);
        when(placeRepository.existsConflictingPlace(anyString(), any(), any())).thenReturn(true);

        boolean result = placeHandler.isPlaceAvailableWithTimeGap("Test Stadium", testTime);

        assertFalse(result);
        verify(placeRepository).existsConflictingPlace(
            eq("Test Stadium"),
            eq(testTime.minusMinutes(150)),
            eq(testTime.plusMinutes(150))
        );
    }

    @Test
    void isPlaceAvailableWithTimeGap_ShouldReturnTrueWhenNoConflicts() {
        LocalDateTime testTime = LocalDateTime.now();
        when(placeRepository.existsByStadium("Test Stadium")).thenReturn(true);
        when(placeRepository.existsConflictingPlace(anyString(), any(), any())).thenReturn(false);

        boolean result = placeHandler.isPlaceAvailableWithTimeGap("Test Stadium", testTime);

        assertTrue(result);
    }
}
