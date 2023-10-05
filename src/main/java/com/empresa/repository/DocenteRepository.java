package com.empresa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.empresa.entity.Docente;

public interface DocenteRepository extends JpaRepository<Docente, Integer> {

	


	@Query("select x from Docente x where x.nombre like ?1")
	public List<Docente> listaPorNombreLike(String nombre);
	
	
}


