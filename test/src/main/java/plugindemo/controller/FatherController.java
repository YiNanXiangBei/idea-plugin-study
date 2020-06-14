package plugindemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FatherController {

    @GetMapping("/father")
    public String father() {
        return "father";
    }

}
