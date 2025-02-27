package com.yalice.wardrobe_social_app.controllers;

import com.yalice.wardrobe_social_app.entities.Item;
import com.yalice.wardrobe_social_app.entities.User;
import com.yalice.wardrobe_social_app.interfaces.ItemService;
import com.yalice.wardrobe_social_app.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

//     ✅ Create an item
     @PostMapping
     public ResponseEntity<Item> createItem(@RequestBody Item item) {
     User user = userService.findUserbyUserId()
     Long userId = user.getId;
     Optional<Item> createdItem = itemService.createItem(userId, item);
     return createdItem.map(ResponseEntity::ok).orElseGet(() ->
     ResponseEntity.badRequest().build());
     }

    // ✅ Get all items
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Item>> getAllItems(@PathVariable Long userId) {
        List<Item> items = itemService.getAllItems(userId);
        return ResponseEntity.ok(items);
    }

    // ✅ Get item by ID
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemService.getItem(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Get item by name
    @GetMapping("/names/{name}")
    public ResponseEntity<Item> getItemByName(@PathVariable String name) {
        Optional<Item> item = itemService.getItemByName(name);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Update an item
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
}
