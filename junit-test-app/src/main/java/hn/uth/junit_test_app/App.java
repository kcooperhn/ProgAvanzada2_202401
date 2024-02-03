package hn.uth.junit_test_app;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ){
        System.out.println( "Hello World!" );
        System.out.println("Menu de la Aplicación");
        
    }

    /**
     * Este método suma dos numeros decimales y responde con otro numero decimal.
     * @param primerNumero número decimal negativo o positivo.
     * @param segundoNumero número decimal negativo o positivo.
     * @return el número decimal que contiene el resultado de la suma.
     */
	public static double sumar(double primerNumero, double segundoNumero) {
		return primerNumero+segundoNumero;
	}

	/**
	 * Este método resta dos números decimales y responde con otro número decimal.
	 * @param primerNumero número decimal negativo o positivo.
	 * @param segundoNumero número decimal negativo o positivo.
	 * @return el número decimal que contiene el restultado de la resta.
	 */
	public static double restar(double primerNumero, double segundoNumero) {
		return primerNumero-segundoNumero;
	}
	
	public static void imprimirMenuPantalla() {
		System.out.println("MENU");
		System.out.println("1. sumar");
		System.out.println("2. restar");
		System.out.println("3. dividir");
		System.out.println("4. multiplicar");
	}
}
