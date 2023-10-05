package com.empresa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.empresa.entity.Docente;
import com.empresa.repository.DocenteRepository;

@Service
public class DocenteServiceImpl implements DocenteService {

	@Autowired
	private DocenteRepository repository;


	@Override
	public Docente insertaActualizaDocente(Docente docente) {
		return repository.save(docente);
	}


	@Override
	public List<Docente> listaDocentePorNombreLike(String nombre) {
		return repository.listaPorNombreLike(nombre);
	}

	@Override
	public void eliminaDocente(int idDocente) {
		repository.deleteById(idDocente);
	}


}
