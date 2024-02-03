package hn.uth.junit_test_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	private static Random rd;
	
	@BeforeClass
	public static void inicializacion() {
		System.out.println("Ejecutando prueba unitaria");
		rd = new Random();
	}
	
	@Test
	public void sumaConAleatorios() {
		//NEXT DOUBLE GENERA UN NÚMERO ALEATORIO DECIMAL
		double numero1 = generarAleatorio();
		double numero2 = rd.nextDouble();
		double resultadoEsperado = numero1 + numero2;
		
		assertEquals(App.sumar(numero1,numero2), resultadoEsperado, 0.01);
		
		System.out.print("Numero1= "+numero1);
		System.out.print(" Numero2= "+numero2);
		System.out.println(" Resultado= "+resultadoEsperado);
	}
   
	@Test
	public void sumaEnterosPositivos() {
		assertTrue(App.sumar(25,30) == 55);
	}
	
	@Test
	public void sumaEnterosNegativos() {
		assertTrue(App.sumar(-5,-1) == -6);
	}
	
	@Test
	public void sumaDecimalesPositivos() {
		assertTrue(App.sumar(10.1,8.3) == 18.4);
	}
	
	@Test
	public void sumaDecimalesNegativos() {
		assertTrue(App.sumar(-0.5,-0.6) == -1.1);
	}
	
	@Test
	public void sumaDecimalesPequenios() {
		assertEquals(App.sumar(3.99999,4.999999), 8.990, .01);
	}
	
	@Test
	public void sumaCeroYPositivo() {
		assertTrue(App.sumar(0,5) == 5);
	}
	
	@Test
	public void restaPositivosEnteros() {
		assertTrue(App.restar(5,1) == 4);
	}
	
	@Test
	public void restaNegativosEnteros() {
		assertTrue(App.restar(-1,-1) == 0);
	}
	
	@Test
	public void restaPositivosDecimales() {
		assertEquals(App.restar(3.4,0.3), 3.1, .001);
	}
	
	@Test
	public void restaCeroYPositivo() {
		assertTrue(App.restar(0,5) == -5);
	}
	
	@Test
	public void restaCeroYPositivo1() {
		assertTrue(App.restar(1,5) == -4);
	}
	
	@Test
	public void restaCeroYPositivo2() {
		assertTrue(App.restar(2,5) == -3);
	}
	
	@Test
	public void restaCeroYPositivo3() {
		assertTrue(App.restar(3,5) == -2);
	}
	
	@Test
	public void restaCeroYPositivo4() {
		assertTrue(App.restar(4,5) == -1);
	}
	
	@Test
	public void restaCeroYPositivo5() {
		assertTrue(App.restar(5,5) == 0);
	}
	
	@Test
	public void restaCeroYPositivo6() {
		assertTrue(App.restar(6,5) == 1);
	}
	
	@Test
	public void restaCeroYPositivo7() {
		assertTrue(App.restar(7,5) == 2);
	}
	
	@AfterClass
	public static void finalizacion() {
		System.out.println("Prueba unitaria finalizada");
	}
	
	//METODO AUXILIAR, NO TIENE ANOTACIÓN
	private double generarAleatorio() {
		return rd.nextDouble();
	}
}
