package com.opticCenter.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.opticCenter.jwtSecurity.AutenticadorJWT;
import com.opticCenter.model.entities.Producto;
import com.opticCenter.model.repositories.ProductoRepository;

@CrossOrigin
@RestController
public class ProductoController {
	@Autowired
	ProductoRepository proRepo;
	
	private DTO getDTOFromProducto (Producto p) {
		DTO dto = new DTO();
		if(p != null) {
			dto.put("id", p.getId());
			dto.put("referencia", p.getReferencia());
			dto.put("color", p.getColor());
			dto.put("precio", p.getPrecio());
			dto.put("imagen", p.getImagen());
		}
		return dto;
		
	}
	
	@GetMapping("/productos/listar")
	public DTO getListaProductos(HttpServletRequest request) {
		int idUsuarioAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		DTO dtoProductos = new DTO();
		dtoProductos.put("result", "fail");
		List<DTO> listaProductosDTO = new ArrayList<DTO>();
		try {
			List<Producto> productos = (List<Producto>) this.proRepo.findAll();
			for (Producto p : productos) {
				listaProductosDTO.add(getDTOFromProducto(p));
			}
			dtoProductos.put("result", "ok");
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		dtoProductos.put("productos", listaProductosDTO);
		return dtoProductos;
		
		
	}
	
	@GetMapping("producto/get")
	public DTO getProducto(int id) {
		Producto p = proRepo.findById(id).get();
		return getDTOFromProducto(p);
	}
	
	@PostMapping("producto/nuevo")
	private DTO nuevoProducto(@RequestBody DatosNuevoProducto datosNuevoProducto, HttpServletRequest request) {
		DTO dto = new DTO();

		try {
			Producto p = new Producto();
			p.setReferencia(datosNuevoProducto.referencia);
			p.setColor(datosNuevoProducto.color);
			p.setPrecio(datosNuevoProducto.precio);
			p.setImagen(Base64.decodeBase64((String) datosNuevoProducto.imagen));

			proRepo.save(p);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}
	
	@PutMapping("producto/update")
	private DTO updateProducto(@RequestBody DatosModificarProducto datosModificarProducto, HttpServletRequest request) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			Producto p = this.proRepo.findById(datosModificarProducto.id).get();
			p.setId(datosModificarProducto.id);
			p.setReferencia(datosModificarProducto.referencia);
			p.setColor(datosModificarProducto.color);
			p.setPrecio(datosModificarProducto.precio);
			p.setImagen(Base64.decodeBase64((String) datosModificarProducto.imagen));
			proRepo.save(p);
			dto.put("result", "ok");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}

	@DeleteMapping("producto/delete")
	private DTO deleteProducto(int idproducto, HttpServletRequest request) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			this.proRepo.deleteById(idproducto);
			dto.put("result", "ok");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}
	
	


}

class DatosNuevoProducto {
	String referencia;
	String color;
	BigDecimal precio;
	String imagen;

	/**
	 * Constructor
	 */
	public DatosNuevoProducto(String referencia, String color, BigDecimal precio, String imagen) {
		super();
		this.referencia = referencia;
		this.color = color;
		this.precio = precio;
		this.imagen = imagen;
	}
}

class DatosModificarProducto {
	int id;
	String referencia;
	String color;
	BigDecimal precio;
	String imagen;

	/**
	 * Constructor
	 */
	public DatosModificarProducto(int id, String referencia, String color, BigDecimal precio, String imagen) {
		super();
		this.id = id;
		this.referencia = referencia;
		this.color = color;
		this.precio = precio;
		this.imagen = imagen;
	}
}