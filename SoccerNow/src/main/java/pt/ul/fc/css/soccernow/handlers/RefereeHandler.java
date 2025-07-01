package pt.ul.fc.css.soccernow.handlers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.entities.Referee;
import pt.ul.fc.css.soccernow.exceptions.RefereeAlreadyExistsException;
import pt.ul.fc.css.soccernow.exceptions.RefereeNotFoundException;
import pt.ul.fc.css.soccernow.repositories.RefereeRepository;

@Service
public class RefereeHandler {
	@Autowired
    private RefereeRepository refereeRepository;

    @Autowired
    private MatchHandler matchHandler;

    public List<RefereeDTO> getAllReferees() {
        List<Referee> referees = refereeRepository.findAll();
        return referees.stream()
                .map(this::mapToDto)
                .toList();
    }

    public RefereeDTO saveReferee(Referee referee) {
        return mapToDto(refereeRepository.save(referee));
    }

    public RefereeDTO getRefereeById(Long id) throws RefereeNotFoundException {
        Optional<Referee> refereeOptional = refereeRepository.findById(id);
        if (refereeOptional.isEmpty()) {
            throw new RefereeNotFoundException(id);
        }
        return mapToDto(refereeOptional.get());
    }

    public RefereeDTO getRefereeByName(String name) throws RefereeNotFoundException {
        Optional<Referee> refereeOptional = refereeRepository.findByName(name);
        if (refereeOptional.isEmpty()) {
            throw new RefereeNotFoundException(name);
        }
        return mapToDto(refereeOptional.get());
    }

    public RefereeDTO updateReferee(Long id, RefereeDTO refereeDto) throws RefereeNotFoundException {
        Optional<Referee> refereeOptional = refereeRepository.findById(id);
        if (refereeOptional.isEmpty()) {
            throw new RefereeNotFoundException(id);
        }

        Referee existingReferee = refereeOptional.get();
        existingReferee.setName(refereeDto.getName());
        existingReferee.setCertificate(refereeDto.getCertificate());

        Referee updatedReferee = refereeRepository.save(existingReferee);
        return mapToDto(updatedReferee);
    }

    @Transactional
    public String deleteRefereeById(Long id) throws RefereeNotFoundException {
        Optional<Referee> refereeOptional = refereeRepository.findById(id);
        if (refereeOptional.isEmpty()) {
            throw new RefereeNotFoundException(id);
        }

        refereeRepository.deleteById(id);
        return "Referee with ID " + id + " deleted succesfully";
    }

    @Transactional
    public String deleteRefereeByName(String name) throws RefereeNotFoundException {
        Optional<Referee> refereeOptional = refereeRepository.findByName(name);
        if (refereeOptional.isEmpty()) {
            throw new RefereeNotFoundException(name);
        }

        refereeRepository.deleteByName(name);
        return "Referee with name " + name + " deleted succesfully";
    }

    public RefereeDTO mapToDto(Referee referee) {
        RefereeDTO refereeDto = new RefereeDTO();
        refereeDto.setId(referee.getId());
        refereeDto.setName(referee.getName());
        refereeDto.setCertificate(referee.getCertificate());
        return refereeDto;
    }

    public RefereeDTO createReferee(RefereeDTO refereeDto) throws RefereeAlreadyExistsException {
        try {
            String nameProvided = refereeDto.getName();
            getRefereeByName(nameProvided);

            throw new RefereeAlreadyExistsException(nameProvided);
        }
        catch (RefereeNotFoundException e) {
            Referee referee = new Referee();
            referee.setName(refereeDto.getName());
            referee.setCertificate(refereeDto.getCertificate());

            Referee savedreferee = refereeRepository.save(referee);

            refereeDto.setId(savedreferee.getId());
            return refereeDto;
        }
    }

    public List<RefereeDTO> searchWithFilters(String name, Integer numMatches, Integer numCards) {
        List<Referee> referees;
        if (name == null || name.equals(""))
            referees = refereeRepository.findAll();
        else 
            referees = refereeRepository.findByNameStartingWith(name);
        List<RefereeDTO> matchingNameReferees = referees.stream().map(this::mapToDto).toList();
        List<RefereeDTO> matchingReferees = new LinkedList<>();
        for (RefereeDTO referee : matchingNameReferees) {
            if (numMatches != null && numMatches != matchHandler.getNumberOfGamesRefereedByReferee(referee.getId()))
                continue;
            if (numCards != null && numCards != matchHandler.getNumberOfCardsIssuedByReferee(referee.getId()))
                continue;
            matchingReferees.add(referee);
        }

        return matchingReferees;
    }
}
