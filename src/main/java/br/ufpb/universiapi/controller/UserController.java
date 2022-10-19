package br.ufpb.universiapi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/create")
    public String create(){
        return "create";
    }
}
