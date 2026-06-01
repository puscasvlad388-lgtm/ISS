package com.bitewise.controller;

import com.bitewise.domain.MealLog;
import com.bitewise.dto.CalorieProgress;
import com.bitewise.dto.MealLogRequest;
import com.bitewise.service.MealLogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/meals")
public class MealLogController {

    private final MealLogService service;

    public MealLogController(MealLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<MealLog> all(@PathVariable Long userId,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date != null) {
            return service.forUserOnDate(userId, date);
        }
        return service.forUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealLog log(@PathVariable Long userId, @Valid @RequestBody MealLogRequest req) {
        return service.log(userId, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/progress")
    public CalorieProgress progress(@PathVariable Long userId,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.dailyProgress(userId, date != null ? date : LocalDate.now());
    }
}
