package hn.uth.controller;

import hn.uth.data.Empleado;

public interface InteractorEmpleados {

	void consultarEmpleados();
	void consultarPuestos();
	void crearEmpleado(Empleado nuevo);
}
