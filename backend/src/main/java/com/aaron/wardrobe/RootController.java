package com.aaron.wardrobe;

import org.springframework.web.bind.annotation.*;

@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
        return "Wardrobic backend is running";
    }
}
