package com.opticCenter.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.opticCenter.*;
import com.opticCenter.jwtSecurity.AutenticadorJWT;
import com.opticCenter.model.entities.Cita;
import com.opticCenter.model.entities.Usuario;
import com.opticCenter.model.repositories.UsuarioRepository;



@CrossOrigin
@RestController
public class UsuarioController {
	
//	@GetMapping("/usuario")
//	public String doGet() {
//		return "Get - Hola mundo";
//	}
//	
//	@PostMapping("/usuario")
//	public String doPost() {
//		return "Pot - Hola mundo";
//	}
	
	@Autowired
	UsuarioRepository usuRep;
	
	/**
	 * Este método aprovecha que todas las peticiones van a incluir el jwt
	 * y con eso vamos a obtener y devolver un usuario autenticado
	 * @param imagen
	 * @param request
	 * @return
	 */
	@GetMapping("/usuario/getAutenticado")
	public DTO getUsuarioAutenticado (boolean imagen,HttpServletRequest request) {
		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		// Intento localizar un usuario a partir de su id
		Usuario usuAutenticado = usuRep.findById(idUsuAutenticado).get();

		// Finalmente devuelvo el JWT creado, puede estar vacío si la autenticación no ha funcionado
		return getDTOFromUsuario(usuAutenticado, imagen);
	}
	
	/**
	 * Método para obtener la lista de todos los usuarios de la óptica
	 * @return
	 */
	//@GetMapping("/usuarios/listar")
	public List<Usuario> getUsuarios(){
		List<Usuario> listaUsuarios = (List<Usuario>) usuRep.findAll(); 
		return listaUsuarios;	
	}
	
	@GetMapping("/usuarios/listar")
	public DTO getListaUsuarios(HttpServletRequest request, boolean imagen) {
		int idUsuarioAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request);
		DTO dtoUsuarios = new DTO();
		dtoUsuarios.put("result", "fail");
		List<DTO> listaUsuariosDTO = new ArrayList<DTO>();
		try {
			List<Usuario> usuarios = new ArrayList<Usuario>();
			usuarios = (List<Usuario>) this.usuRep.findAll();
			for (Usuario u : usuarios) {
				listaUsuariosDTO.add(getDTOFromUsuario(u, imagen));
			}
			dtoUsuarios.put("result", "ok");
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		dtoUsuarios.put("usuarios", listaUsuariosDTO);
		return dtoUsuarios;
	}
	

	
	/**
	 * Obtiene y devuelve los datos de un usuario, a través de su id
	 */
	@GetMapping("/usuario/get")
	public DTO getUsuario (int id, boolean imagen) {

		// Intento localizar un usuario a partir de su id
		Usuario usu = usuRep.findById(id).get();

		// Finalmente devuelvo el JWT creado, puede estar vacío si la autenticación no ha funcionado
		return getDTOFromUsuario(usu, imagen);
	}
	
	/**
	 * Fabrica un DTO con los datos que queremos enviar de un usuario.
	 * @param usu
	 * @param incluirImagen
	 * @return
	 */
	private DTO getDTOFromUsuario (Usuario usu, boolean incluirImagen) {
		DTO dto = new DTO(); // Voy a devolver un dto
		if (usu != null) {
			dto.put("id", usu.getId());
			dto.put("nombre", usu.getNombre());
			dto.put("apellidos", usu.getApellidos());
			dto.put("email", usu.getEmail());
			dto.put("fechaNacimiento", usu.getFechaNacimiento());
			dto.put("dni", usu.getDni());
			dto.put("password", usu.getPassword());
			dto.put("direccion", usu.getDireccion());
			dto.put("cp", usu.getCp());
			dto.put( "municipio", usu.getMunicipio());
			dto.put("telefono", usu.getTelefono());			
			dto.put("imagen", incluirImagen? usu.getImagen() : "");
			dto.put("rol", usu.getRol());
		}
		return dto;
	}
	
	@PostMapping("usuario/nuevo")
	private DTO nuevoUsuario(@RequestBody DatosNuevoUsuario datosNuevoUsuario, HttpServletRequest request) {
		DTO dto = new DTO();
		
		try {
			Usuario u = new Usuario();
			u.setNombre(datosNuevoUsuario.nombre);
			u.setApellidos(datosNuevoUsuario.apellidos);
			u.setEmail(datosNuevoUsuario.email);
			u.setFechaNacimiento(datosNuevoUsuario.fechaNacimiento);
			u.setDni(datosNuevoUsuario.dni);
			u.setPassword(datosNuevoUsuario.password);
			u.setDireccion(datosNuevoUsuario.direccion);
			u.setCp(datosNuevoUsuario.cp);
			u.setMunicipio(datosNuevoUsuario.municipio);
			u.setTelefono(datosNuevoUsuario.telefono);
			u.setImagen(Base64.decodeBase64((String) datosNuevoUsuario.imagen));
			u.setRol(datosNuevoUsuario.rol);
			
			usuRep.save(u);
			
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}
	
	
	/**
	 * Método al que recibe el dni y la contraseña para autentificar al usuario
	 */
	@PostMapping("/usuario/autentica")
	public DTO autenticaUsuario(@RequestBody DatosAutenticacionUsuario datos) {
		DTO dto = new DTO();
		
		// intento localizar a un usuario por su dni y su password
		Usuario usuAtenticado = usuRep.findByDniAndPassword(datos.dni, datos.password);
		if(usuAtenticado != null) {
			dto.put("jwt", AutenticadorJWT.codificaJWT(usuAtenticado));
		}
		
		// así recojo y devuelvo el JWT que se ha creado, si la autentificación falla puede estar vacio
		return dto;
	}
	
	/**
	 * usado para comprobar si una contraseña es igual a la contraseña del usuario autenticado
	 * 
	 */
	@PostMapping("/usuario/ratificaPassword")
	public DTO ratificaPassword (@RequestBody DTO dtoRecibido, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		try {
			Usuario usuarioAutenticado = usuRep.findById(idUsuAutenticado).get(); // Localizo todos los datos del usuario
			String password = (String) dtoRecibido.get("password");  // Compruebo la contraseña
			if (password.equals(usuarioAutenticado.getPassword())) {
				dto.put("result", "ok"); // Devuelvo éxito, las contraseñas son iguales
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return dto;
	}
	
	/**
	 * será usado para modificar la contraseña del usuario si éste o ésta lo desea hacer en su ficha de usuario
	 */
	@PostMapping("/usuario/modificaPassword")
	public DTO modificaPassword (@RequestBody DTO dtoRecibido, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		try {
			Usuario usuarioAutenticado = usuRep.findById(idUsuAutenticado).get(); // Localizo al usuario
			String password = (String) dtoRecibido.get("password");  // Recibo la password que llega en el dtoRecibido
			usuarioAutenticado.setPassword(password); // Modifico la password
			usuRep.save(usuarioAutenticado);  // Guardo el usuario, con nueva password, en la unidad de persistencia
			dto.put("result", "ok"); // Devuelvo éxito
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return dto;
	}
	
	/**
	 * Recibe los datos personales del usuario y los modifica en la bbdd
	 */
	@PostMapping("/usuario/update")
	public DTO modificaDatosUsuario (@RequestBody DTO dtoRecibido, HttpServletRequest request) {
		DTO dto = new DTO(); // Voy a devolver un dto
		dto.put("result", "fail"); // Asumo que voy a fallar, si todo va bien se sobrescribe este valor

		int idUsuAutenticado = AutenticadorJWT.getIdUsuarioDesdeJwtIncrustadoEnRequest(request); // Obtengo el usuario autenticado, por su JWT

		try {
			Usuario usuarioAutenticado = usuRep.findById(idUsuAutenticado).get(); // Localizo al usuario
			// Cargo los datos recibidos en el usuario localizado por su id.
			usuarioAutenticado.setNombre((String) dtoRecibido.get("nombre"));
			usuarioAutenticado.setApellidos((String) dtoRecibido.get("apellidos"));
			usuarioAutenticado.setEmail((String) dtoRecibido.get("email"));	
			usuarioAutenticado.setFechaNacimiento(new Date((long)dtoRecibido.get("fechaNacimiento")));
			usuarioAutenticado.setDni((String) dtoRecibido.get("dni"));
			usuarioAutenticado.setDireccion((String) dtoRecibido.get("direccion"));
			usuarioAutenticado.setMunicipio((String) dtoRecibido.get("municipio"));
			usuarioAutenticado.setCp((int) dtoRecibido.get("cp"));
			usuarioAutenticado.setTelefono((String) dtoRecibido.get("telefono"));
			// las imágenes viajan como string pero hay que cambiarlo a Base64 que es con lo que trabajan las bbdd
			usuarioAutenticado.setImagen(Base64.decodeBase64((String) dtoRecibido.get("imagen")));
			//usuarioAutenticado.setRol((String) dtoRecibido.get("rol"));
			usuRep.save(usuarioAutenticado);  // Guardo el usuario en la unidad de persistencia
			dto.put("result", "ok"); // Devuelvo éxito
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return dto;
	}
	
	@DeleteMapping("usuario/delete")
	private DTO deleteUsuario(int idUsuario, HttpServletRequest request) {
		DTO dto = new DTO();
		dto.put("result", "fail");
		try {
			this.usuRep.deleteById(idUsuario);
			dto.put("result", "ok");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dto;
	}

}

class DatosAutenticacionUsuario{
	String dni;
	String password;
	
	/**
	 * Constructor
	 */
	public DatosAutenticacionUsuario(String dni, String password) {
		super();
		this.dni = dni;
		this.password = password;
	}
}

 class DatosNuevoUsuario{
	 String nombre;
	 String apellidos;
	 String dni;
	 String direccion;
	 int cp;
	 String email;
	 String password;
	 String telefono;
	 Date fechaNacimiento;
	 String imagen;
	 String rol;
	 String municipio;
	 
	 public DatosNuevoUsuario(String nombre, String apellidos, String email, Date fechaNacimiento, 
			 String dni, String password, String direccion, int cp,
			 String municipio, String telefono, String imagen, String rol) {
		 super();
		 this.nombre = nombre;
		 this.apellidos = apellidos;
		 this.email = email;
		 this.fechaNacimiento = fechaNacimiento;
		 this.dni = dni;
		 this.password = password;
		 this.direccion = direccion;
		 this.cp = cp;
		 this.municipio = municipio;
		 this.telefono = telefono;
		 this.imagen = imagen;
		 this.rol = "usuario" ;
	 }
 }
 
 class DatosModificarUsuario{
	 String nombre;
	 String apellidos;
	 String dni;
	 String direccion;
	 int cp;
	 String email;
	 String password;
	 String telefono;
	 Date fechaNacimiento;
	 String imagen;
	 String rol;
	 String municipio;
	 
	 public DatosModificarUsuario(String nombre, String apellidos, String email, Date fechaNacimiento, 
			 String dni, String password, String direccion, int cp,
			 String municipio, String telefono, String imagen, String rol) {
		 super();
		 this.nombre = nombre;
		 this.apellidos = apellidos;
		 this.email = email;
		 this.fechaNacimiento = fechaNacimiento;
		 this.dni = dni;
		 this.password = password;
		 this.direccion = direccion;
		 this.cp = cp;
		 this.municipio = municipio;
		 this.telefono = telefono;
		 this.imagen = imagen;
		 this.rol = "usuario" ;
	 }
 }
