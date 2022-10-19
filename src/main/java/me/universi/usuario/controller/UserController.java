package me.universi.usuario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;

@Controller
public class UserController {

    @GetMapping("/create")
    public String create(){
        return "create";
    }
}
