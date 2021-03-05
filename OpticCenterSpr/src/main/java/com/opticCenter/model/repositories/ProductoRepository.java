package com.opticCenter.model.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.opticCenter.model.entities.Producto;

@Repository
public interface ProductoRepository extends CrudRepository<Producto, Integer> {
	//@Query(value = "SELECT * FROM producto as p " + "where p.id = ?", nativeQuery = true)
	//public Producto findById(int id);
	
	@Query (value = "SELECT * FROM producto as p " 
	+ "where p.color = ?", nativeQuery = true)
	public Producto findByColor(String color);

}
