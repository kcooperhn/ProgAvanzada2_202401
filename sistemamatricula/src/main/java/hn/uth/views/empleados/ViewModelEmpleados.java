package hn.uth.views.empleados;

import java.util.List;

import hn.uth.data.Empleado;

public interface ViewModelEmpleados {

	void mostrarEmpleadosEnGrid(List<Empleado> items);
	void mostrarMensajeError(String mensaje);
}
