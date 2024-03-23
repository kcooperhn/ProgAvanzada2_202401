package hn.uth.controller;

import hn.uth.data.Puesto;

public interface InteractorPuestos {

	void consultarPuestos();
	void crearPuesto(Puesto nuevo);
	void actualizarPuesto(Puesto cambiar);
}
