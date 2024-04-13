package hn.uth.services;

import java.io.File;
import java.util.Map;

import org.springframework.util.ResourceUtils;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportGenerator {

	//SABER DONDE ESTA GUARDADO NUESTRO REPORTE PDF FINAL
	private String reportPath;
	
	public boolean generarReportePDF(String nombreReporte, Map<String, Object> parametros, JRDataSource datasource) {
		boolean generado = false;
		
		try {
			//CARGAMOS EL ARCHIVO .JASPER DEL REPORTE A GENERAR
			JasperReport reporte = (JasperReport)JRLoader.loadObjectFromFile(obtenerUbicacionArchivo(nombreReporte + ".jasper"));
			//LLENAR EL REPORTE MEDIANTE UNA IMPRESORA
			JasperPrint impresora = JasperFillManager.fillReport(reporte, parametros, datasource);
			//GENERAMOS LA RUTA DE GUARDADO DEL PDF
			String rutaPDF = generarUbicacionArchivo();
			reportPath = rutaPDF;
			//SE GENERA EL PDF CON LOS DATOS
			JasperExportManager.exportReportToPdfFile(impresora, rutaPDF);
			generado = true;
		}catch(Exception error) {
			error.printStackTrace();
			generado = false;
		}
		
		return generado;
	}

	private String generarUbicacionArchivo() {
		String path = null;
		try {
			//SE CREA UN ARCHIVO VACIO TEMPORAL
			path = File.createTempFile("temp", ".pdf").getAbsolutePath();
		}catch(Exception error) {
			error.printStackTrace();
		}
		return path;
	}

	private String obtenerUbicacionArchivo(String archivo) {
		String path = null;
		try {
			path = ResourceUtils.getFile("classpath:"+archivo).getAbsolutePath();
		}catch(Exception error) {
			error.printStackTrace();
		}
		return path;
	}
	
	public String getReportPath() {
		return this.reportPath;
	}
}
