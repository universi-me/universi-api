package me.universi.projeto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjetoController {

    @GetMapping("/projeto/create")
    public String create(){
        return "/projeto/create";
    }
}
