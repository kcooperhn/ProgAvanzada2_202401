package hn.uth.model;

import hn.uth.data.Empleado;
import hn.uth.data.Puesto;
import hn.uth.data.PuestosResponse;
import hn.uth.data.TrabajadoresResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface DatabaseRepository {
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@GET("/pls/apex/ingenieria_uth/srh/trabajadores")
	Call<TrabajadoresResponse> consultarTrabajadores();
	
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@POST("/pls/apex/ingenieria_uth/srh/trabajadores")
	Call<ResponseBody> crearEmpleado(@Body Empleado nuevo);
	
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@GET("/pls/apex/ingenieria_uth/srh/puestos")
	Call<PuestosResponse> consultarPuestos();
	
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@POST("/pls/apex/ingenieria_uth/srh/puestos")
	Call<ResponseBody> crearPuesto(@Body Puesto nuevo);
	
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@PUT("/pls/apex/ingenieria_uth/srh/puestos")
	Call<ResponseBody> actualizarPuesto(@Body Puesto cambiar);
}
