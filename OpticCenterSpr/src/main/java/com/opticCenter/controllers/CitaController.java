package com.opticCenter.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.opticCenter.jwtSecurity.AutenticadorJWT;
import com.opticCenter.model.entities.Cita;
import com.opticCenter.model.entities.Producto;
import com.opticCenter.model.entities.Usuario;
import com.opticCenter.model.repositories.CitaRepository;
import com.opticCenter.model.repositories.ProductoRepository;
import com.opticCenter.model.repositories.UsuarioRepository;

@CrossOrigin
@RestController
public class CitaController {
	@Autowired CitaRepository citaRepo;
	@Autowired ProductoRepository prodRepo;
	@Autowired UsuarioRepository usuRep;
	
	@GetMapping("/citas/getById")
	public DTO citasParaUsuarioAutenticado(int id, HttpServletRequest request) {
		int idUsuarioAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		Cita c = this.citaRepo.findById(id).get();
		
		return getDtoFromCita(c, idUsuarioAutenticado);
	}
	
	//@GetMapping("/citas/listar")
	public List<Cita> getCitasdeUsuario(HttpServletRequest request){
		int idUsuarioAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		return this.citaRepo.getCitasDeUsuario(idUsuarioAutenticado);
	}

	
	@GetMapping("/citas/listar")
	public DTO citasListarParaUsuarioAutenticado(HttpServletRequest request){
		int idUsuarioAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		DTO dtoCitas = new DTO(); // Creo el DTO que devuelvo al cliente
		dtoCitas.put("result", "fail");
		
		List<DTO> listaCitasEnDTO = new ArrayList<DTO>();
		
		try {
			List<Cita> citas = new ArrayList<Cita>();
			citas = this.citaRepo.getCitasDeUsuario(idUsuarioAutenticado);
			//countCitas = this.citaRepo.countCitas(idUsuarioAutenticado);
			for (Cita c : citas) {
				listaCitasEnDTO.add(getDtoFromCita(c, idUsuarioAutenticado));
			}
			dtoCitas.put("result", "ok");
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		dtoCitas.put("citas", listaCitasEnDTO);
		return dtoCitas;
		
	} 
	
	
	private DTO getDtoDeProducto (Producto p) {
		DTO dto = new DTO();
		dto.put("id", p.getId());
		dto.put("referencia", p.getReferencia());
		dto.put("imagen", p.getImagen());
		dto.put("precio", p.getPrecio());
		dto.put("color", p.getColor());
		return dto;
	}
	
	
	private DTO getDtoFromCita(Cita c, int idUsuarioAutenticado) {
		// DTO a partir de una cita
		DTO dto = new DTO();
			
		dto.put("id", c.getId());
		dto.put("fecha", c.getFecha());
		dto.put("graduacion", c.getGraduacion());
		dto.put("id_usuario", c.getUsuario().getId());
		//dto.put("id_producto", c.getProducto().getId());
		dto.put("id_producto", getDtoDeProducto(c.getProducto()));
		
		//tengo que buscar al usuario de la cita y el producto que se ha llevado\
		//Producto p = productoRepo.findById(id_producto);
		//Usuario u = (Usuario) usuRep.getCitasDeUsuarioSimple(idUsuarioAutenticado);	
//		Cita u = citaRepo.findByIdCitaAndIdUsuario(idUsuarioAutenticado);
//		if(u != null) {
//			dto.put("id_usuario", u.getUsuario().getId());
//		}

		
		//dto.put("id_producto", c.getProducto().getId());
		
		//Cita p = citaRepo.findByIdCitaAndIdProducto(c.getId(), idUsuarioAutenticado);

		return dto;
	}
	
	@PostMapping("cita/nueva")
	private DTO nuevaCita (@RequestBody DatosNuevaCita datosNuevaCita, HttpServletRequest request) {
		DTO dto = new DTO();
		
		dto.put("result", "fail");
		
		try {
			Cita c = new Cita();
			c.setFecha(datosNuevaCita.fecha);
			c.setGraduacion(datosNuevaCita.graduacion);
			
			c.setUsuario(usuRep.findById(datosNuevaCita.id_usuario).get());
			c.setProducto(prodRepo.findById(datosNuevaCita.id_producto).get());
			
			citaRepo.save(c);
			dto.put("result", "ok");
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}
	


}

class DatosNuevaCita{
	Date fecha;
	String graduacion;
	int id_usuario;
	int id_producto;
	
	public DatosNuevaCita(Date fecha, String graduacion, int id_usuario, int id_producto) {
		super();
		this.fecha = fecha;
		this.graduacion = graduacion;
		this.id_usuario = id_usuario;
		this.id_producto = id_producto;
		System.out.println("graduación: " + graduacion + " id_usua: " + id_usuario);
	}
}
