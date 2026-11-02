package org.example.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/actuator")
class HealthCheckController {

    @GetMapping("/health")
    fun health() : ResponseEntity<String>{
        return ResponseEntity.ok("{\"status\":\"UP\"}")
    }
}