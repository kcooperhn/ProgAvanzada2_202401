package hn.uth.views.puestos;

import java.util.List;

import hn.uth.data.Puesto;

public interface ViewModelPuestos {
	void mostrarPuestosEnGrid(List<Puesto> items);
	void mostrarMensajeError(String mensaje);
	void mostrarMensajeExito(String mensaje);
}
