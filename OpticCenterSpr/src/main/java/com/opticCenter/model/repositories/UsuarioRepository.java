package com.opticCenter.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.opticCenter.model.entities.Cita;
import com.opticCenter.model.entities.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Integer> {

	public Usuario findByDni(String dni);
    public Usuario findByDniAndPassword(String dni,String password);
    
//    @Query(value = "SELECT * FROM Usuario where nombre like ? or email like ?", nativeQuery = true)
//	public List<Usuario> filterByNombreOrEmail(String filtroNombre, String filtroEmail);
    


}
