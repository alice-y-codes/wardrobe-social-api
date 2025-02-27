package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    // ✅ Create an item
    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = userOptional.get().getId();
        Optional<Item> createdItem = itemService.createItem(userId, item);

        return createdItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Item>> getAllItems(@PathVariable Long userId) {
        List<Item> items = itemService.getAllItems(userId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<Item>> getMyItems() {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = userOptional.get().getId();
        List<Item> items = itemService.getAllItems(userId);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItem(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/names/{name}")
    public ResponseEntity<Item> getItemByName(@PathVariable String name) {
        Optional<Item> item = itemService.getItemByName(name);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        Item updatedItem = itemService.updateItem(id, item);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.notFound().build();
    }

    // ✅ Delete an item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Utility method to get the current authenticated user
     * 
     * @return Optional containing the user if found, empty otherwise
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        String username = authentication.getName();
        return userService.findUserByUsername(username);
    }
}
