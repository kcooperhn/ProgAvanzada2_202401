package hn.uth.controller;

import hn.uth.data.Puesto;
import hn.uth.data.PuestosResponse;
import hn.uth.model.DatabaseRepositoryImpl;
import hn.uth.views.puestos.ViewModelPuestos;

public class InteractorImplPuestos implements InteractorPuestos {
	private DatabaseRepositoryImpl modelo;
	private ViewModelPuestos vista;
	
	public InteractorImplPuestos(ViewModelPuestos view) {
		super();
		this.vista = view;
		this.modelo = DatabaseRepositoryImpl.getInstance("https://apex.oracle.com", 30000L);
	}


	@Override
	public void consultarPuestos() {
		try {
			PuestosResponse respuesta = this.modelo.consultarPuestos();
			if(respuesta == null || respuesta.getCount() == 0 || respuesta.getItems() == null) {
				this.vista.mostrarMensajeError("No hay puestos a mostrar");
			}else {
				this.vista.mostrarPuestosEnGrid(respuesta.getItems());
			}
		}catch(Exception error) {
			error.printStackTrace();
		}
	}

	@Override
	public void crearPuesto(Puesto nuevo) {
		try {
			boolean creado = this.modelo.crearPuesto(nuevo);
			if(creado == true) {
				this.vista.mostrarMensajeExito("Puesto creado exitosamente");
			}else {
				this.vista.mostrarMensajeError("Hay un problema al crear el puesto");
			}
		}catch(Exception error) {
			error.printStackTrace();
		}
	}
	
	@Override
	public void actualizarPuesto(Puesto cambiar) {
		try {
			boolean creado = this.modelo.actualizarPuesto(cambiar);
			if(creado == true) {
				this.vista.mostrarMensajeExito("Puesto modificado exitosamente");
			}else {
				this.vista.mostrarMensajeError("Hay un problema al modificar el puesto");
			}
		}catch(Exception error) {
			error.printStackTrace();
		}
	}
}
