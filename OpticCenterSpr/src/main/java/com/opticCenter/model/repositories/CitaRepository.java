package com.opticCenter.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.opticCenter.model.entities.Cita;


@Repository
public interface CitaRepository extends CrudRepository<Cita, Integer> {
	
	// recupero las citas de un usuario
	@Query(value = "SELECT distinct  * FROM cita WHERE id_usuario = ? order by fecha", nativeQuery = true)
	public List<Cita> getCitasDeUsuario(int idUsuario);
	
//	@Query(value = "SELECT distinct c. * FROM cita as c, usuario as u WHERE c.id_usuario = u.id order by c.fecha desc ", nativeQuery = true)
//	public List<Cita> getCitasDeUsuarioSimple(int idUsauario);
	
	@Query(value = "SELECT * FROM cita WHERE id_usuario = ?", nativeQuery = true)
	public Cita findByIdCitaAndIdUsuario (int idUsuario);
	
	@Query(value = "SELECT id_producto  FROM cita WHERE id = ? and id_usuario = ?", nativeQuery = true)
	public Cita findByIdCitaAndIdProducto (int idCita, int idUsuario);

	@Query(value = "SELECT count(distinc c.id) FROM cita as c, usuario as u WHERE u.id = c.id_usuario", nativeQuery = true)
	public long countCitas(int idUsuarioAutenticado);
	
	

}
