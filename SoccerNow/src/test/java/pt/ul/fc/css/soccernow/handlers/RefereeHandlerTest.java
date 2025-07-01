package pt.ul.fc.css.soccernow.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.entities.Certificate;
import pt.ul.fc.css.soccernow.entities.Referee;
import pt.ul.fc.css.soccernow.exceptions.RefereeAlreadyExistsException;
import pt.ul.fc.css.soccernow.exceptions.RefereeNotFoundException;
import pt.ul.fc.css.soccernow.repositories.RefereeRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefereeHandlerTest {

    @Mock
    private RefereeRepository refereeRepository;

    @InjectMocks
    private RefereeHandler refereeHandler;

    private Referee testReferee;
    private RefereeDTO testRefereeDTO;

    @BeforeEach
    void setUp() {
        testReferee = new Referee();
        testReferee.setId(1L);
        testReferee.setName("John Doe");
        testReferee.setCertificate(Certificate.CERTIFIED);

        testRefereeDTO = new RefereeDTO("John Doe", 1L, Certificate.CERTIFIED);
    }

    @Test
    void getAllReferees_ShouldReturnAllReferees() {
        when(refereeRepository.findAll()).thenReturn(List.of(testReferee));

        List<RefereeDTO> result = refereeHandler.getAllReferees();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals(Certificate.CERTIFIED, result.get(0).getCertificate());
        verify(refereeRepository).findAll();
    }

    @Test
    void getRefereeById_ShouldReturnReferee() throws RefereeNotFoundException {
        when(refereeRepository.findById(1L)).thenReturn(Optional.of(testReferee));

        RefereeDTO result = refereeHandler.getRefereeById(1L);

        assertEquals("John Doe", result.getName());
        assertEquals(Certificate.CERTIFIED, result.getCertificate());
        verify(refereeRepository).findById(1L);
    }

    @Test
    void updateReferee_ShouldUpdateCertificate() throws RefereeNotFoundException {
        RefereeDTO updateDTO = new RefereeDTO("Updated Name", null, Certificate.UNCERTIFIED);

        when(refereeRepository.findById(1L)).thenReturn(Optional.of(testReferee));
        when(refereeRepository.save(any(Referee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefereeDTO result = refereeHandler.updateReferee(1L, updateDTO);

        assertEquals("Updated Name", result.getName());
        assertEquals(Certificate.UNCERTIFIED, result.getCertificate());
        verify(refereeRepository).save(any(Referee.class));
    }

    @Test
    void createReferee_ShouldHandleCertificate() throws RefereeAlreadyExistsException {
        when(refereeRepository.findByName("New Referee")).thenReturn(Optional.empty());
        when(refereeRepository.save(any(Referee.class))).thenAnswer(invocation -> {
            Referee r = invocation.getArgument(0);
            r.setId(2L);
            return r;
        });

        RefereeDTO newReferee = new RefereeDTO("New Referee", null, Certificate.UNCERTIFIED);
        RefereeDTO result = refereeHandler.createReferee(newReferee);

        assertEquals(2L, result.getId());
        assertEquals(Certificate.UNCERTIFIED, result.getCertificate());
    }

    @Test
    void mapToDto_ShouldIncludeCertificate() {
        RefereeDTO result = refereeHandler.mapToDto(testReferee);

        assertEquals(Certificate.CERTIFIED, result.getCertificate());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void equalsAndHashCode_ShouldWorkWithCertificate() {
        RefereeDTO dto1 = new RefereeDTO("Same", 1L, Certificate.CERTIFIED);
        RefereeDTO dto2 = new RefereeDTO("Same", 1L, Certificate.CERTIFIED);
        RefereeDTO dto3 = new RefereeDTO("Same", 1L, Certificate.UNCERTIFIED);

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void deleteReferee_ShouldWorkWithCertificate() throws RefereeNotFoundException {
        when(refereeRepository.findByName("John Doe")).thenReturn(Optional.of(testReferee));
        doNothing().when(refereeRepository).deleteByName("John Doe");

        String result = refereeHandler.deleteRefereeByName("John Doe");

        assertEquals("Referee with name John Doe deleted succesfully", result);
    }
}
