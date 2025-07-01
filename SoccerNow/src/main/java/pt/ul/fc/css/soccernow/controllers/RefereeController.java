package pt.ul.fc.css.soccernow.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ul.fc.css.soccernow.dto.RefereeDTO;
import pt.ul.fc.css.soccernow.exceptions.RefereeAlreadyExistsException;
import pt.ul.fc.css.soccernow.exceptions.RefereeNotFoundException;
import pt.ul.fc.css.soccernow.handlers.RefereeHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/referees")
@Api(value = "referee API", tags = "referees")
public class RefereeController {

    @Autowired
    private RefereeHandler refereeHandler;

    @GetMapping("/referees")
    @ApiOperation(value = "Get all referees", notes = "Returns a list of all referees.")
    public ResponseEntity<List<RefereeDTO>> getAllReferees(){
        List<RefereeDTO> referees = refereeHandler.getAllReferees();
        return ResponseEntity.ok(referees);
    }

    @GetMapping("/referee/id/{id}")
    @ApiOperation(value = "Get referee by ID", notes = "Returns a referee given their ID number.")
    public ResponseEntity<RefereeDTO> getRefereeById(@PathVariable("id") Long id) {
        try {
            RefereeDTO refereeDto = refereeHandler.getRefereeById(id);
            return ResponseEntity.ok(refereeDto);
        }
        catch (RefereeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/referee/{name}")
    @ApiOperation(value = "Get referee by name", notes = "Returns a referee given their name.")
    public ResponseEntity<RefereeDTO> getRefereeByName(@PathVariable("name") String name) {
        try {
            RefereeDTO refereeDto = refereeHandler.getRefereeByName(name);
            return ResponseEntity.ok(refereeDto);
        }
        catch (RefereeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/referee/{id}")
    @ApiOperation(value = "Update referee by ID", notes = "Updates a referee and returns the updated referee DTO.")
    public ResponseEntity<RefereeDTO> updateReferee(@PathVariable("id") Long id, @RequestBody RefereeDTO refereeDto) {
        try {
            RefereeDTO dto = refereeHandler.updateReferee(id, refereeDto);
            return ResponseEntity.ok(dto);
        }
        catch (RefereeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/referee/id/{id}")
    @ApiOperation(value = "Delete referee by ID", notes = "Deletes a referee given their ID number")
    public ResponseEntity<String> deleteRefereeById(@PathVariable("id") Long id) {
        try {
            String response = refereeHandler.deleteRefereeById(id);
            return ResponseEntity.ok(response);
        }
        catch (RefereeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/referee/{name}")
    @ApiOperation(value = "Delete referee by name", notes = "Deletes a referee given their name")
    public ResponseEntity<String> deleteRefereeByName(@PathVariable("name") String name) {
        try {
            String response = refereeHandler.deleteRefereeByName(name);
            return ResponseEntity.ok(response);
        }
        catch (RefereeNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/referee")
    @ApiOperation(value = "Create referee", notes = "Creates a new referee and returns the created referee DTO.")
    public ResponseEntity<RefereeDTO> createReferee(@RequestBody RefereeDTO refereeDto) {
        try {
            RefereeDTO responseDto = refereeHandler.createReferee(refereeDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (RefereeAlreadyExistsException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<List<RefereeDTO>> searchWithFilters(String name, Integer numMatches, Integer numCards){
        List<RefereeDTO> referees = refereeHandler.searchWithFilters(name, numMatches, numCards);
        return ResponseEntity.ok(referees);
    }
}
