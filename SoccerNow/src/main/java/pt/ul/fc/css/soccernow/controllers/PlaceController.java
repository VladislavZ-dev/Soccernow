package pt.ul.fc.css.soccernow.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import pt.ul.fc.css.soccernow.dto.PlaceDto;
import pt.ul.fc.css.soccernow.handlers.PlaceHandler;


@RestController
@RequestMapping("/api/places")
@Api(value = "Place API", tags = "Places")
public class PlaceController {

    @Autowired
    private PlaceHandler placeHandler;

    @GetMapping
    @ApiOperation(value = "Get all places", notes = "Returns a list of all places.")
    public ResponseEntity<List<PlaceDto>> getAllPlaces(){
        List<PlaceDto> places = placeHandler.getAllPlaces();
        return ResponseEntity.ok(places);
    }

    @GetMapping("/by-id/{id}")
    @ApiOperation(value = "id", notes = "Returns a place given its id.")
    public ResponseEntity<PlaceDto> getPlaceById (@PathVariable("id") long id) {
    	PlaceDto placeDto = placeHandler.getPlaceById(id);
        if (placeDto != null) {
            return ResponseEntity.ok(placeDto);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ApiOperation(value = "Create place", notes = "Creates a new place and returns the created place DTO.")
    public ResponseEntity<?> createPlace(@RequestBody PlaceDto placeDto) {
        try {
            PlaceDto responseDto = placeHandler.createPlace(placeDto);
            return ResponseEntity.ok(responseDto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity
                    .internalServerError()
                    .body("Unexpected error occurred: " + ex.getMessage());
        }
    }



}
