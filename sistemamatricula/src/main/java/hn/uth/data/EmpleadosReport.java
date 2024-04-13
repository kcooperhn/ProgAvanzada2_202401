package hn.uth.data;

import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class EmpleadosReport implements JRDataSource {
	
	private List<Empleado> empleados;
	private int counter = -1;
	private int maxCounter = 0;

	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
		this.maxCounter = this.empleados.size()-1;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public int getMaxCounter() {
		return maxCounter;
	}

	public void setMaxCounter(int maxCounter) {
		this.maxCounter = maxCounter;
	}

	@Override
	public boolean next() throws JRException {
		if(counter < maxCounter) {
			counter++;
			return true;//AUN HAY DATOS QUE IMPRIMIR
		}
		return false;//YA NO HAY MÁS DATOS QUE IMPRIMIR
	}

	@Override
	public Object getFieldValue(JRField jrField) throws JRException {
		//PERMITE LLENAR LAS VARIABLES (FIELDS) DEFINIDAS EN NUESTRO REPORTE
		if("IDENTIDAD".equals(jrField.getName())) {
			return empleados.get(counter).getIdentidad();
		}else if("NOMBRE_COMPLETO".equals(jrField.getName())) {
			return empleados.get(counter).getNombre() + " "+ empleados.get(counter).getApellido();
		}else if("PUESTO".equals(jrField.getName())) {
			return empleados.get(counter).getNombre_puesto();
		}
		return "";
	}

}
