package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.RatingCreationDTO;
import nwt.kts.backend.dto.returnDTO.RatingDTO;
import nwt.kts.backend.entity.Rating;
import nwt.kts.backend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;


    @PostMapping(value = "/create-rating", consumes = "application/json", produces = "application/json")
    public ResponseEntity<RatingDTO> createRating(@RequestBody RatingCreationDTO ratingCreationDTO) {
        Rating rating = ratingService.createRating(ratingCreationDTO);
        return new ResponseEntity<>(new RatingDTO(rating), HttpStatus.CREATED);
    }

    @GetMapping(value="/get-drive-ratings/{id}")
    public ResponseEntity<List<RatingDTO>> getDriveRatings(@PathVariable Integer id) {
        List<Rating> ratings = ratingService.getDriveRatings(id);
        List<RatingDTO> ratingDTOS = new ArrayList<>();
        for (Rating rating:
             ratings) {
            ratingDTOS.add(new RatingDTO(rating));
        }
        return new ResponseEntity<>(ratingDTOS, HttpStatus.OK);
    }

    @GetMapping(value="/get-driver-and-vehicle-average-rating/{id}")
    public ResponseEntity<List<Double>> getDriverAndVehicleAverageRating(@PathVariable Integer id) {
        List<Double> ratings = ratingService.getDriverAndVehicleAverageRating(id);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }
}
