package ma.fsbm.lias_lab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // §2.1 : page d'accueil publique — présentation du laboratoire
    @GetMapping("/")
    public String accueil() {
        return "accueil";
    }
}