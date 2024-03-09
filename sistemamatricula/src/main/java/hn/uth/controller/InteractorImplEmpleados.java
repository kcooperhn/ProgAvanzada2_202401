package hn.uth.controller;

import hn.uth.data.TrabajadoresResponse;
import hn.uth.model.DatabaseRepositoryImpl;
import hn.uth.views.empleados.ViewModelEmpleados;

public class InteractorImplEmpleados implements InteractorEmpleados {

	private DatabaseRepositoryImpl modelo;
	private ViewModelEmpleados vista;
	
	public InteractorImplEmpleados(ViewModelEmpleados view) {
		super();
		this.vista = view;
		this.modelo = DatabaseRepositoryImpl.getInstance("https://apex.oracle.com", 30000L);
	}

	@Override
	public void consultarEmpleados() {
		try {
			TrabajadoresResponse respuesta = this.modelo.consultarEmpleados();
			if(respuesta == null || respuesta.getCount() == 0 || respuesta.getItems() == null) {
				this.vista.mostrarMensajeError("No hay empleados a mostrar");
			}else {
				this.vista.mostrarEmpleadosEnGrid(respuesta.getItems());
			}
		}catch(Exception error) {
			error.printStackTrace();
		}
	}
}
