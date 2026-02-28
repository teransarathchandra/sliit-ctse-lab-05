package com.sliit.payment_service;

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
@RequestMapping("/payments")
public class PaymentController {

    private final List<Map<String, Object>> payments = new ArrayList<>();
    private int nextId = 1;

    @GetMapping
    public synchronized List<Map<String, Object>> getPayments() {
        return new ArrayList<>(payments);
    }

    @PostMapping("/process")
    public synchronized ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> request) {
        if (request.get("orderId") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'orderId' is required");
        }
        if (request.get("amount") == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'amount' is required");
        }

        Map<String, Object> payment = new LinkedHashMap<>(request);
        payment.put("id", nextId++);
        payment.put("status", "SUCCESS");
        payments.add(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/{id}")
    public synchronized Map<String, Object> getPaymentById(@PathVariable int id) {
        return payments.stream()
                .filter(payment -> Objects.equals(payment.get("id"), id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }
}
