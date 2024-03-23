package hn.uth.views.empleados;

import java.util.List;

import hn.uth.data.Empleado;
import hn.uth.data.Puesto;

public interface ViewModelEmpleados {

	void mostrarEmpleadosEnGrid(List<Empleado> items);
	void mostrarPuestosEnCombobox(List<Puesto> items);
	void mostrarMensajeError(String mensaje);
	void mostrarMensajeExito(String mensaje);
}
