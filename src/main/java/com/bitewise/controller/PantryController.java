package com.bitewise.controller;

import com.bitewise.domain.PantryItem;
import com.bitewise.dto.PantryItemRequest;
import com.bitewise.service.PantryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/pantry")
public class PantryController {

    private final PantryService service;

    public PantryController(PantryService service) {
        this.service = service;
    }

    @GetMapping
    public List<PantryItem> all(@PathVariable Long userId) {
        return service.forUser(userId);
    }

    @GetMapping("/expiring")
    public List<PantryItem> expiring(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "7") int days) {
        return service.expiringSoon(userId, days);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PantryItem add(@PathVariable Long userId, @Valid @RequestBody PantryItemRequest req) {
        return service.add(userId, req);
    }

    @PutMapping("/{id}")
    public PantryItem update(@PathVariable Long userId, @PathVariable Long id,
                             @Valid @RequestBody PantryItemRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
