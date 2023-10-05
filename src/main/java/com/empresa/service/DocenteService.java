package com.empresa.service;

import java.util.List;

import com.empresa.entity.Docente;

public interface DocenteService {

	//Para el Crud
	public abstract Docente insertaActualizaDocente(Docente docente);
	public abstract List<Docente> listaDocentePorNombreLike(String nombre);
	public abstract void eliminaDocente(int idDocente);

	
}
