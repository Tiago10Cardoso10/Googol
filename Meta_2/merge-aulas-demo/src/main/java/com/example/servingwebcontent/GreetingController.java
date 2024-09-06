package com.example.servingwebcontent;

import java.rmi.Naming;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;



import hello_callback.Cliente;
import hello_callback.IClient;
import hello_callback.IGateway;
import hello_callback.IBarrels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * Um controlador que gerencia requisições relacionadas a passagem de URLs, palavras, e operações de administração.
 */
@Controller
public class GreetingController {

    /**
     * Manipula a requisição POST para passar um URL.
     *
     * @param url   A URL passada na requisição.
     * @param model O modelo a ser preenchido e enviado à visão.
     * @return ResponseEntity contendo a mensagem de sucesso ou falha.
     */
    @PostMapping("/passar_url")
    public ResponseEntity<String> passar_url(@RequestBody String url, Model model) {
        try {
            IGateway h = (IGateway) Naming.lookup("XPTO");
            Cliente c = new Cliente();
            h.subscribe((IClient) c);


            if (!url.isEmpty()) {
                String cleanedUrl = url.replace("\"", "");
                String resultado = c.enviar_url(cleanedUrl, h);
                return ResponseEntity.ok().body("{\"message\": \"" + resultado + "\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"Link não foi passado.\"}");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("{\"message\": \"Houve erro na conexão.\"}");
        }
    }

    /**
     * Manipula a requisição POST para passar uma palavra.
     *
     * @param palavra A palavra passada na requisição.
     * @param model   O modelo a ser preenchido e enviado à visão.
     * @return ResponseEntity contendo a mensagem de sucesso ou falha.
     */
    @PostMapping("/passar_palavra")
    public ResponseEntity<String> passar_palavra(@RequestBody String palavra, Model model) {
        try {
            IGateway h = (IGateway) Naming.lookup("XPTO");
            Cliente c = new Cliente();
            h.subscribe((IClient) c);


            if (!palavra.isEmpty()) {
                String cleanedUrl = palavra.replace("\"", "");
                h.addString(cleanedUrl);
                String resultado = c.enviar_palavra(cleanedUrl, h);
                return ResponseEntity.ok().body("{\"message\": \"" + resultado + "\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"Palavra não foi passado.\"}");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("{\"message\": \"Houve erro na conexão.\"}");
        }
    }

    /**
     * Manipula a requisição POST para passar um link.
     *
     * @param url   O link passado na requisição.
     * @param model O modelo a ser preenchido e enviado à visão.
     * @return ResponseEntity contendo a mensagem de sucesso ou falha.
     */
    @PostMapping("/passar_link")
    public ResponseEntity<String> passar_link(@RequestBody String url, Model model) {
        try {
            IGateway h = (IGateway) Naming.lookup("XPTO");
            Cliente c = new Cliente();
            h.subscribe((IClient) c);

            if (!url.isEmpty()) {
                String cleanedUrl = url.replace("\"", "");
                h.addString(cleanedUrl);
                String resultado = c.procura_link(cleanedUrl, h);

                return ResponseEntity.ok().body("{\"message\": \"" + resultado + "\"}");
            } else {
                return ResponseEntity.badRequest().body("{\"message\": \"Link para retornar não foi passado.\"}");
            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("{\"message\": \"Houve erro na conexão.\"}");
        }
    }

    /**
     * Manipula a requisição GET para retornar informações de administração.
     *
     * @return ResponseEntity contendo as informações de administração ou uma mensagem de erro em caso de falha.
     */
    @GetMapping("/admin")
    public ResponseEntity<String> admin() {
        try {
            IGateway h = (IGateway) Naming.lookup("XPTO");
            Cliente c = new Cliente();
            h.subscribe((IClient) c);

            int downloaders = h.nr_downloaders();
            int nr_links = h.nr_links();
            String links_visitados = h.mostra_linksV();

            return ResponseEntity.ok().body("{\"message\": \""  + downloaders + ", " +  nr_links + ", " + links_visitados +  "\"}");

        } catch (Exception ex) {
            return ResponseEntity.status(500).body("{\"message\": \"Houve erro na conexão.\"}");
        }
    }

    /**
     * Manipula a requisição GET para retornar as 10 principais palavras.
     *
     * @return ResponseEntity contendo as 10 principais palavras ou uma mensagem de erro em caso de falha.
     */
    @GetMapping("/top10")
    public ResponseEntity<String> top10() {
        try {
            IGateway h = (IGateway) Naming.lookup("XPTO");
            Cliente c = new Cliente();
            h.subscribe((IClient) c);


            String resultado = h.getTop10Words();

            return ResponseEntity.ok().body("{\"message\": \""  +resultado+  "\"}");

        } catch (Exception ex) {
            return ResponseEntity.status(500).body("{\"message\": \"Houve erro na conexão.\"}");
        }
    }
}