package com.bitewise.service;

import com.bitewise.domain.User;
import com.bitewise.dto.BodyMetrics;
import com.bitewise.exception.BusinessException;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizatorul cu id " + id + " nu exista"));
    }

    public User create(User user) {
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Exista deja un utilizator cu acest email");
        }
        return userRepository.save(user);
    }

    public User update(Long id, User data) {
        User existing = findById(id);
        existing.setName(data.getName());
        existing.setEmail(data.getEmail());
        existing.setWeight(data.getWeight());
        existing.setHeight(data.getHeight());
        existing.setAge(data.getAge());
        existing.setGender(data.getGender());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

    /**
     * Calculeaza BMI, categoria BMI, BMR (formula Mifflin-St Jeor) si TDEE.
     * Use case: "Gestioneaza profil (Calcul BMI/BMR)".
     */
    public BodyMetrics computeMetrics(Long userId) {
        User u = findById(userId);
        if (u.getWeight() == null || u.getHeight() == null) {
            throw new BusinessException("Greutatea si inaltimea sunt necesare pentru calcule");
        }

        double heightM = u.getHeight() / 100.0;
        double bmi = u.getWeight() / (heightM * heightM);
        String category = bmiCategory(bmi);

        double bmr = computeBmr(u);
        // Factor de activitate moderat (sedentar-usor): 1.375
        double tdee = bmr * 1.375;

        return new BodyMetrics(round(bmi), category, round(bmr), round(tdee));
    }

    private double computeBmr(User u) {
        int age = u.getAge() != null ? u.getAge() : 30;
        double base = 10 * u.getWeight() + 6.25 * u.getHeight() - 5 * age;
        if (u.getGender() != null && u.getGender().toLowerCase().startsWith("f")) {
            return base - 161; // feminin
        }
        return base + 5; // masculin / implicit
    }

    private String bmiCategory(double bmi) {
        if (bmi < 18.5) return "Subponderal";
        if (bmi < 25) return "Greutate normala";
        if (bmi < 30) return "Supraponderal";
        return "Obezitate";
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
