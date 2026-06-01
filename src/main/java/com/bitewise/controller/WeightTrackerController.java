package com.bitewise.controller;

import com.bitewise.domain.WeightTracker;
import com.bitewise.service.WeightTrackerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/weights")
public class WeightTrackerController {

    private final WeightTrackerService service;

    public WeightTrackerController(WeightTrackerService service) {
        this.service = service;
    }

    @GetMapping
    public List<WeightTracker> history(@PathVariable Long userId) {
        return service.history(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WeightTracker record(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Double weight = Double.valueOf(body.get("recordedWeight").toString());
        LocalDate date = body.get("date") != null ? LocalDate.parse(body.get("date").toString()) : null;
        return service.record(userId, weight, date);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
