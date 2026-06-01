package com.bitewise.service;

import com.bitewise.domain.User;
import com.bitewise.domain.WeightTracker;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.WeightTrackerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class WeightTrackerService {

    private final WeightTrackerRepository weightTrackerRepository;
    private final UserService userService;

    public WeightTrackerService(WeightTrackerRepository weightTrackerRepository, UserService userService) {
        this.weightTrackerRepository = weightTrackerRepository;
        this.userService = userService;
    }

    public List<WeightTracker> history(Long userId) {
        return weightTrackerRepository.findByUserIdOrderByDateAsc(userId);
    }

    /** Use case: "Urmareste istoricul greutatii". Actualizeaza si greutatea curenta a profilului. */
    public WeightTracker record(Long userId, Double weight, LocalDate date) {
        User user = userService.findById(userId);
        WeightTracker entry = new WeightTracker(weight, date != null ? date : LocalDate.now(), user);
        WeightTracker saved = weightTrackerRepository.save(entry);
        // sincronizam greutatea curenta din profil cu ultima inregistrare
        user.setWeight(weight);
        return saved;
    }

    public void delete(Long id) {
        WeightTracker entry = weightTrackerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inregistrarea cu id " + id + " nu exista"));
        weightTrackerRepository.delete(entry);
    }
}
