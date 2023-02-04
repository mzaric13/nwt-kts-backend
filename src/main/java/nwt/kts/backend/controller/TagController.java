package nwt.kts.backend.controller;

import nwt.kts.backend.dto.returnDTO.TagDTO;
import nwt.kts.backend.entity.Tag;
import nwt.kts.backend.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping(value="/", produces = "application/json")
    public ResponseEntity<List<TagDTO>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        return new ResponseEntity<>(tags.stream().map(TagDTO::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}
