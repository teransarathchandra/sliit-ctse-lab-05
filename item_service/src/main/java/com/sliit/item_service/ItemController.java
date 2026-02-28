package com.sliit.item_service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final List<Map<String, Object>> items = new ArrayList<>();
    private int nextId = 1;

    @GetMapping
    public synchronized List<Map<String, Object>> getItems() {
        return new ArrayList<>(items);
    }

    @PostMapping
    public synchronized ResponseEntity<Void> createItem(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'name' is required and must not be blank");
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", nextId++);
        item.put("name", name.trim());
        items.add(item);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public synchronized Map<String, Object> getItemById(@PathVariable int id) {
        return items.stream()
                .filter(item -> Objects.equals(item.get("id"), id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
    }
}
