package ifrn.pi.eventos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ifrn.pi.eventos.models.Convidado;
import ifrn.pi.eventos.models.Evento;
import ifrn.pi.eventos.repositories.ConvidadoRepository;
import ifrn.pi.eventos.repositories.EventosRepository;

@Controller
@RequestMapping("/eventos")
public class EventosController {

    @Autowired
    private EventosRepository er;

    @Autowired
    private ConvidadoRepository cr;

    // Formulário para adicionar evento
    @GetMapping("/form")
    public String form() {
        return "eventos/formEvento";
    }

    // Salvar evento
    @PostMapping
    public String adicionar(Evento evento) {

        System.out.println(evento);

        er.save(evento);

        return "eventos/evento-adicionado";
    }

    // Listar eventos
    @GetMapping
    public ModelAndView listar() {

        List<Evento> eventos = er.findAll();

        ModelAndView mv = new ModelAndView("eventos/lista");

        mv.addObject("eventos", eventos);

        return mv;
    }

    // Detalhes do evento
    @GetMapping("/{id}")
    public ModelAndView detalhar(@PathVariable Long id) {

        ModelAndView md = new ModelAndView();

        Optional<Evento> opt = er.findById(id);

        if (opt.isEmpty()) {
            md.setViewName("redirect:/eventos");
            return md;
        }

        Evento evento = opt.get();

        md.setViewName("eventos/detalhes");

        md.addObject("evento", evento);

        List<Convidado> convidados = cr.findByEvento(evento);

        md.addObject("convidados", convidados);

        return md;
    }

    // Salvar convidado
    @PostMapping("/{idEvento}")
    public String salvarConvidado(
            @PathVariable Long idEvento,
            Convidado convidado) {

        System.out.println("Id do evento: " + idEvento);
        System.out.println(convidado);

        Optional<Evento> opt = er.findById(idEvento);

        if (opt.isEmpty()) {
            return "redirect:/eventos";
        }

        Evento evento = opt.get();

        convidado.setEvento(evento);

        cr.save(convidado);

        return "redirect:/eventos/" + idEvento;
    }

    // Apagar evento
    @GetMapping("/{id}/remover")
    public String apagarEvento(@PathVariable Long id) {

        Optional<Evento> opt = er.findById(id);

        if (opt.isPresent()) {

            Evento evento = opt.get();

            er.delete(evento);
        }

        return "redirect:/eventos";
    }

    // Apagar convidado
    @GetMapping("/{idEvento}/convidado/{idConvidado}/remover")
    public String apagarConvidado(
            @PathVariable Long idEvento,
            @PathVariable Long idConvidado) {

        System.out.println("Apagando convidado: " + idConvidado);

        Optional<Convidado> opt = cr.findById(idConvidado);

        if (opt.isPresent()) {
            cr.delete(opt.get());
        }

        return "redirect:/eventos/" + idEvento;
    }
}