package hn.uth.model;

import hn.uth.data.TrabajadoresResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface DatabaseRepository {
	@Headers({
	    "Accept: application/json",
	    "User-Agent: Retrofit-Sample-App"
	})
	@GET("/pls/apex/ingenieria_uth/srh/trabajadores")
	Call<TrabajadoresResponse> consultarTrabajadores();
}
