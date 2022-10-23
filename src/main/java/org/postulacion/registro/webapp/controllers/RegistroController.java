package org.postulacion.registro.webapp.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.postulacion.registro.webapp.models.entity.Registro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class RegistroController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${base.registro.url}")
	private String url;
	
	@GetMapping(value="registros")
	public String listar(Model model){
		List<Registro> registroList = new ArrayList<>();
		
		Registro[] registros = restTemplate.getForObject(url + "/", Registro[].class);
		registroList = Arrays.asList(registros);
		
		model.addAttribute("titulo", "Lista de Registros");
		model.addAttribute("registros", registroList);
		
		return "registros";
	}
	
	@GetMapping(value="/form")
	public String crear(Map<String, Object> model) {
		Registro registro = new Registro();
		model.put("registro", registro);
		model.put("titulo", "Formulario de Registro");
		
		return "form";
		
	}
	
	@PostMapping(value="/form")
	public String guardar(@Valid Registro registro, BindingResult result, Model model) {
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Registro");
			return "form";
		}
		try {
			
			ResponseEntity<Registro> response = restTemplate.postForEntity(url + "/", registro, Registro.class);
			
		}catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			model.addAttribute("titulo", "Formulario de Registro");
			model.addAttribute("errors", e.getMessage());
			return "form";
		}
		
		
		return "redirect:registros";
	}
	
	@GetMapping(value="/{id}")
	public String detalle(@PathVariable Long id, Map<String, Object> model) {
		
		Map<String, Long> param = new HashMap<>();
		param.put("id", id);
		
		Registro registro = restTemplate.getForObject(url + "/" + id, Registro.class);
		
		model.put("titulo", "Formulario de Registro");
		model.put("registro", registro);
		
		return "form";
	}
	
	@PutMapping(value="/form/{id}")
	public String editar(@Valid Registro registro, BindingResult result, @PathVariable Long id, Model model) {
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Registro");
			return "form";
		}
		try {
			Map<String, Long> param = new HashMap<>();
			param.put("id", id);
			
			Registro registroDB = restTemplate.getForObject(url + "/" + id, Registro.class);
			registroDB.setNombre(registro.getNombre());
			registroDB.setPrecio(registro.getPrecio());
			registroDB.setFecha(new Date());
			
			ResponseEntity<Registro> response = restTemplate.postForEntity(url + "/", registroDB, Registro.class);
			
		}catch(HttpClientErrorException e){
			System.out.println(e.getMessage());
			model.addAttribute("titulo", "Formulario de Registro");
			model.addAttribute("errors", e.getMessage());
			return "form";
		}
		
		
		return "redirect:registros";
	}
	
	@DeleteMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable Long id) {
		
		restTemplate.delete(url + "/delete/" + id);
		
		return "redirect:registros";
	}

}
