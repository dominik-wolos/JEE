package bp.pai_springboot.controllers;

import bp.pai_springboot.entities.Zadanie;
import bp.pai_springboot.repositories.ZadanieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    @Autowired
    public ZadanieRepository rep;

    @RequestMapping("/")
    @ResponseBody
    public String mainPage() {
        return "Hello Spring Boot from mainPage() method!";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String pageTwo() {
        return "Hello Spring Boot from pageTwo() method!";
    }

    @RequestMapping("/listaZadan")
    @ResponseBody
    public String listaZadan() {
        StringBuilder odp = new StringBuilder();

        // Generowanie testowych zadań
        Zadanie z;
        double k = 1000;
        boolean wyk = false;

        // Sprawdź czy już są jakieś zadania, jeśli nie - wygeneruj testowe
        if (rep.count() < 10) {
            for (int i = 1; i <= 10; i++) {
                z = new Zadanie();
                z.setNazwa("zadanie " + i);
                z.setOpis("Opis czynnosci do wykonania w zadaniu " + i);
                z.setKoszt(k);
                z.setWykonane(wyk);
                wyk = !wyk;
                k += 200.50;
                rep.save(z);
            }
        }

        // Pobierz i wyświetl wszystkie zadania
        odp.append("<h2>Lista wszystkich zadań:</h2>");
        for (Zadanie i : rep.findAll()) {
            odp.append(i).append("<br>");
        }

        return odp.toString();
    }

    @RequestMapping("/delete/{id}")
    @ResponseBody
    public String delete(@PathVariable Long id) {
        StringBuilder odp = new StringBuilder();

        if (rep.existsById(id)) {
            rep.deleteById(id);
            odp.append("Zadanie o id=").append(id).append(" zostało usunięte.<br><br>");
        } else {
            odp.append("Nie znaleziono zadania o id=").append(id).append("<br><br>");
        }

        odp.append("<h3>Pozostałe zadania:</h3>");
        for (Zadanie i : rep.findAll()) {
            odp.append(i).append("<br>");
        }

        return odp.toString();
    }

    @RequestMapping("/wykonane/{status}")
    @ResponseBody
    public String findByWykonane(@PathVariable boolean status) {
        StringBuilder odp = new StringBuilder();
        odp.append("<h2>Zadania wykonane: ").append(status).append("</h2>");

        for (Zadanie z : rep.findByWykonane(status)) {
            odp.append(z).append("<br>");
        }

        return odp.toString();
    }

    @RequestMapping("/kosztMniejszyNiz/{max}")
    @ResponseBody
    public String findByKosztLessThan(@PathVariable double max) {
        StringBuilder odp = new StringBuilder();
        odp.append("<h2>Zadania o koszcie mniejszym niż ").append(max).append(":</h2>");

        for (Zadanie z : rep.findByKosztLessThan(max)) {
            odp.append(z).append("<br>");
        }

        return odp.toString();
    }

    @RequestMapping("/koszt/{min}/{max}")
    @ResponseBody
    public String findByKosztBetween(@PathVariable double min, @PathVariable double max) {
        StringBuilder odp = new StringBuilder();
        odp.append("<h2>Zadania o koszcie pomiędzy ").append(min)
           .append(" a ").append(max).append(":</h2>");

        for (Zadanie z : rep.findByKosztBetween(min, max)) {
            odp.append(z).append("<br>");
        }

        return odp.toString();
    }
}
