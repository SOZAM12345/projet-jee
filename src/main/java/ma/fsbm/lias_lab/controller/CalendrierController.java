package ma.fsbm.lias_lab.controller;

import ma.fsbm.lias_lab.entity.Evenement;
import ma.fsbm.lias_lab.service.EvenementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class CalendrierController {

    @Autowired
    private EvenementService evenementService;

    // Section 20 : "Visualisation des événements" + "Navigation temporelle"
    @GetMapping("/calendrier")
    public String calendrier(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois,
            Model model) {

        LocalDate aujourdHui = LocalDate.now();
        int anneeAffichee = (annee != null) ? annee : aujourdHui.getYear();
        int moisAffiche = (mois != null) ? mois : aujourdHui.getMonthValue();

        YearMonth moisCourant = YearMonth.of(anneeAffichee, moisAffiche);
        LocalDate premierJourMois = moisCourant.atDay(1);
        LocalDate dernierJourMois = moisCourant.atEndOfMonth();

        List<Evenement> tousLesEvenements = evenementService.getEvenementsActifs();
        List<Evenement> evenementsDuMois = new ArrayList<>();
        for (Evenement e : tousLesEvenements) {
            if (e.getDateDebut() == null) continue;
            LocalDate debut = e.getDateDebut();
            LocalDate fin = (e.getDateFin() != null) ? e.getDateFin() : debut;
            boolean chevauche = !fin.isBefore(premierJourMois) && !debut.isAfter(dernierJourMois);
            if (chevauche) {
                evenementsDuMois.add(e);
            }
        }

        Map<Integer, List<Evenement>> evenementsParJour = new TreeMap<>();
        for (Evenement e : evenementsDuMois) {
            LocalDate debut = e.getDateDebut().isBefore(premierJourMois) ? premierJourMois : e.getDateDebut();
            LocalDate fin = e.getDateFin() == null ? debut : e.getDateFin();
            if (fin.isAfter(dernierJourMois)) fin = dernierJourMois;

            LocalDate curseur = debut;
            while (!curseur.isAfter(fin)) {
                evenementsParJour.computeIfAbsent(curseur.getDayOfMonth(), k -> new ArrayList<>()).add(e);
                curseur = curseur.plusDays(1);
            }
        }

        int decalagePremierJour = premierJourMois.getDayOfWeek().getValue() - 1;

        YearMonth moisPrecedent = moisCourant.minusMonths(1);
        YearMonth moisSuivant = moisCourant.plusMonths(1);

        model.addAttribute("anneeAffichee", anneeAffichee);
        model.addAttribute("moisAffiche", moisAffiche);
        model.addAttribute("nomMois", moisCourant.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH));
        model.addAttribute("nombreJoursMois", moisCourant.lengthOfMonth());
        model.addAttribute("decalagePremierJour", decalagePremierJour);
        model.addAttribute("evenementsParJour", evenementsParJour);
        model.addAttribute("anneePrecedente", moisPrecedent.getYear());
        model.addAttribute("moisPrecedent", moisPrecedent.getMonthValue());
        model.addAttribute("anneeSuivante", moisSuivant.getYear());
        model.addAttribute("moisSuivant", moisSuivant.getMonthValue());
        model.addAttribute("estMoisCourant", moisCourant.equals(YearMonth.now()));

        return "calendrier";
    }
}