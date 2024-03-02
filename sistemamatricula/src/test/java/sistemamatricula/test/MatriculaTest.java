package sistemamatricula.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.support.ui.ExpectedConditions.titleIs;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MatriculaTest {

	@Test
	public void testGuardarEmpleado() throws InterruptedException {
		// Inicializa el WebDriver para Chrome
		WebDriver driver = new ChromeDriver();
		
		try{
			// Abre la página web de empleados
			driver.get("http://localhost:8080/empleados");
			
			int cantidadEmpleadosInicial = 0;//CONSULTO LA CANTIDAD DE EMPLEADOS REALES
			
			new WebDriverWait(driver, ofSeconds(30), ofSeconds(1)).until(titleIs("Empleados"));
			
			//ESPERA 3 SEGUNDOS DESPUES DE CARGAR LA PANTALLA
			//Thread.sleep(3000);
			
			// Localiza el campo de entrada de nombre de usuario
			WebElement cIdentidad = driver.findElement(By.xpath("//vaadin-text-field[@id='txt_identidad']/input"));
			WebElement cNombre = driver.findElement(By.xpath("//vaadin-text-field[@id='txt_nombre']/input"));
			WebElement cApellido = driver.findElement(By.xpath("//vaadin-text-field[@id='txt_apellido']/input"));
			WebElement cTelefono = driver.findElement(By.xpath("//vaadin-text-field[@id='txt_telefono']/input"));
			WebElement cHorario = driver.findElement(By.xpath("//vaadin-text-field[@id='txt_horario']/input"));
			WebElement cPuesto = driver.findElement(By.xpath("//vaadin-text-field[@id='txt_puesto']/input"));
			WebElement cSueldo = driver.findElement(By.xpath("//vaadin-number-field[@id='txt_sueldo']/input"));
	
			WebElement bGuardar = driver.findElement(By.xpath("//vaadin-button[@id='btn_guardar']"));
			WebElement bCancelar = driver.findElement(By.xpath("//vaadin-button[@id='btn_cancelar']"));
			WebElement bEliminar = driver.findElement(By.xpath("//vaadin-button[@id='btn_eliminar']"));
	
			
			// Ingresa el nombre de usuario
			cIdentidad.sendKeys("0801199912345");
			cNombre.sendKeys("Pedro");
			cApellido.sendKeys("Perez");
			cTelefono.sendKeys("99420000");
			cHorario.sendKeys("7AM-4PM");
			cPuesto.sendKeys("Administración");
			cSueldo.sendKeys("");
			cSueldo.sendKeys("12000");
			
			//ESPERA 3 SEGUNDOS LUEGO DE LLENAR LOS CAMPOS PARA LUEGO DAR CLICK EN EL BOTON GUARDAR
			//Thread.sleep(3000);
			
			bGuardar.click();
			
			int cantidadEmpleadosFinal = 0;//CONSULTO LA CANTIDAD DE EMPLEADOS REALES

			//SI LA CANTIDAD DE EMPLEADOS AL DARLE CLICK A GUARDAR AUMENTA EN 1
			assertTrue(cantidadEmpleadosFinal == (cantidadEmpleadosInicial+1));
		}finally {
			//CIERRA EL NAVEGADOR ABIERTO
			driver.close();
		}
	}
	
}
