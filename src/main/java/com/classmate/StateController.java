package com.classmate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/state")
public class StateController {

    private final AppStateRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public StateController(AppStateRepository repo) { this.repo = repo; }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String load() {
        return repo.findById(1L).map(AppState::getJson).orElse("{}");
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> save(@RequestBody String body) {
        try {
            JsonNode n = mapper.readTree(body);
            if (!n.isObject() || !n.has("students") || !n.has("cols")) {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        AppState s = repo.findById(1L).orElseGet(AppState::new);
        s.setJson(body);
        repo.save(s);
        return ResponseEntity.noContent().build();
    }
}
