package com.aaronag.kafka.web;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.aaronag.kafka.service.ProducerService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final ProducerService producerService;
  @PostMapping
  public ResponseEntity<?> post(@RequestBody CreateOrder req,
                                @RequestParam(defaultValue = "false") boolean tx) {
    if (tx) producerService.sendTransactional(req.key, req.value);
    else producerService.send(req.key, req.value);
    return ResponseEntity.accepted().build();
  }

  @Data
  public static class CreateOrder {
    @NotBlank String key;
    @NotBlank String value;
  }
}
