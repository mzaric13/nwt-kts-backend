package nwt.kts.backend.controller;

import nwt.kts.backend.dto.creation.RatingCreationDTO;
import nwt.kts.backend.dto.returnDTO.RatingDTO;
import nwt.kts.backend.entity.Rating;
import nwt.kts.backend.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
