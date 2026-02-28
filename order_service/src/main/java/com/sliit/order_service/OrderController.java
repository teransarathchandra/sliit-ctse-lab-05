package com.sliit.order_service;

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
@RequestMapping("/orders")
public class OrderController {

    private final List<Map<String, Object>> orders = new ArrayList<>();
    private int nextId = 1;

    @GetMapping
    public synchronized List<Map<String, Object>> getOrders() {
        return new ArrayList<>(orders);
    }

    @PostMapping
    public synchronized ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        if (request.get("itemId") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'itemId' is required");
        }
        if (request.get("quantity") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'quantity' is required");
        }

        Map<String, Object> order = new LinkedHashMap<>(request);
        order.put("id", nextId++);
        order.put("status", "PENDING");
        orders.add(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public synchronized Map<String, Object> getOrderById(@PathVariable int id) {
        return orders.stream()
                .filter(order -> Objects.equals(order.get("id"), id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
}
