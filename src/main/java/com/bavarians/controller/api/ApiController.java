package com.bavarians.controller.api;

import com.bavarians.graphql.model.Klient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("api/rest")
public class ApiController {

    @GetMapping("/test")
    public ResponseEntity<Klient> test() throws Exception {
        Klient t = new Klient();
        t.setEmail("dsdas");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(t);
    }
}