package com.jinro.webide.login.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CheckController {

    @GetMapping("/checked")
    public ResponseEntity<String> checked(){
        return ResponseEntity.ok("Entered to a secured page.");
    }

}
