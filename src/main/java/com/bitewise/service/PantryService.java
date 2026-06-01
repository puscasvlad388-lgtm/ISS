package com.bitewise.service;

import com.bitewise.domain.Ingredient;
import com.bitewise.domain.PantryItem;
import com.bitewise.domain.User;
import com.bitewise.dto.PantryItemRequest;
import com.bitewise.exception.BusinessException;
import com.bitewise.exception.ResourceNotFoundException;
import com.bitewise.repository.PantryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PantryService {

    private final PantryItemRepository pantryItemRepository;
    private final UserService userService;
    private final IngredientService ingredientService;

    public PantryService(PantryItemRepository pantryItemRepository,
                         UserService userService,
                         IngredientService ingredientService) {
        this.pantryItemRepository = pantryItemRepository;
        this.userService = userService;
        this.ingredientService = ingredientService;
    }

    public List<PantryItem> forUser(Long userId) {
        return pantryItemRepository.findByUserId(userId);
    }

    /** Use case: "Verifica alimente pe cale de expirare". */
    public List<PantryItem> expiringSoon(Long userId, int days) {
        LocalDate limit = LocalDate.now().plusDays(days);
        return pantryItemRepository.findByUserIdAndExpirationDateBefore(userId, limit);
    }

    public PantryItem add(Long userId, PantryItemRequest req) {
        User user = userService.findById(userId);
        Ingredient ingredient = ingredientService.findById(req.getIngredientId());
        PantryItem item = new PantryItem(req.getQuantity(), req.getExpirationDate(), user, ingredient);
        return pantryItemRepository.save(item);
    }

    public PantryItem update(Long id, PantryItemRequest req) {
        PantryItem item = findById(id);
        item.setQuantity(req.getQuantity());
        item.setExpirationDate(req.getExpirationDate());
        if (req.getIngredientId() != null) {
            item.setIngredient(ingredientService.findById(req.getIngredientId()));
        }
        return pantryItemRepository.save(item);
    }

    public void delete(Long id) {
        pantryItemRepository.delete(findById(id));
    }

    public PantryItem findById(Long id) {
        return pantryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Elementul de camara cu id " + id + " nu exista"));
    }

    /**
     * «includes» (scade cantitatea): cand utilizatorul consuma un ingredient,
     * scadem cantitatea corespunzatoare din camara, daca exista stoc.
     */
    public void consumeFromPantry(Long userId, Long ingredientId, double amount) {
        List<PantryItem> items = pantryItemRepository.findByUserId(userId).stream()
                .filter(p -> p.getIngredient() != null && p.getIngredient().getId().equals(ingredientId))
                .sorted((a, b) -> {
                    // consumam intai ce expira mai repede
                    if (a.getExpirationDate() == null) return 1;
                    if (b.getExpirationDate() == null) return -1;
                    return a.getExpirationDate().compareTo(b.getExpirationDate());
                })
                .toList();

        double remaining = amount;
        for (PantryItem item : items) {
            if (remaining <= 0) break;
            double take = Math.min(item.getQuantity(), remaining);
            item.setQuantity(item.getQuantity() - take);
            remaining -= take;
            if (item.getQuantity() <= 0) {
                pantryItemRepository.delete(item);
            } else {
                pantryItemRepository.save(item);
            }
        }
        // Nu aruncam eroare daca stocul e insuficient: jurnalul se poate inregistra
        // chiar daca alimentul nu era in camara. Doar scadem ce avem.
    }

    public double availableQuantity(Long userId, Long ingredientId) {
        return pantryItemRepository.findByUserId(userId).stream()
                .filter(p -> p.getIngredient() != null && p.getIngredient().getId().equals(ingredientId))
                .mapToDouble(PantryItem::getQuantity)
                .sum();
    }
}
