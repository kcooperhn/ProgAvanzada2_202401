package hn.uth.model;

import java.io.IOException;

import hn.uth.data.Empleado;
import hn.uth.data.Puesto;
import hn.uth.data.PuestosResponse;
import hn.uth.data.TrabajadoresResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class DatabaseRepositoryImpl {

	private static DatabaseRepositoryImpl INSTANCE;
	private DatabaseClient client;
	
	//PATRON SINGLETON
	private DatabaseRepositoryImpl(String url, Long timeout) {
		client = new DatabaseClient(url, timeout);
	}
	
	//PATRON SINGLETON
	public static DatabaseRepositoryImpl getInstance(String url, Long timeout) {
		if(INSTANCE == null) {
			synchronized (DatabaseRepositoryImpl.class) {
				if(INSTANCE == null) {
					INSTANCE = new DatabaseRepositoryImpl(url, timeout);
				}
			}
		}
		return INSTANCE;
	}
	
	//CREACIÓN DE OPERACIONES A LA BASE DE DATOS
	public TrabajadoresResponse consultarEmpleados() throws IOException {
		Call<TrabajadoresResponse> call = client.getDB().consultarTrabajadores();
		Response<TrabajadoresResponse> response = call.execute();//AQUI ES DONDE SE LLAMA A LA BASE DE DATOS
		if(response.isSuccessful()){
			return response.body();
		}else {
			return null;
		}
	}
	
	public boolean crearEmpleado(Empleado nuevo) throws IOException {
		Call<ResponseBody> call = client.getDB().crearEmpleado(nuevo);
		Response<ResponseBody> response = call.execute();//AQUI ES DONDE SE LLAMA A LA BASE DE DATOS
		return response.isSuccessful();
	}
	
	public PuestosResponse consultarPuestos() throws IOException {
		Call<PuestosResponse> call = client.getDB().consultarPuestos();
		Response<PuestosResponse> response = call.execute();//AQUI ES DONDE SE LLAMA A LA BASE DE DATOS
		if(response.isSuccessful()){
			return response.body();
		}else {
			return null;
		}
	}
	
	public boolean crearPuesto(Puesto nuevo) throws IOException {
		Call<ResponseBody> call = client.getDB().crearPuesto(nuevo);
		Response<ResponseBody> response = call.execute();//AQUI ES DONDE SE LLAMA A LA BASE DE DATOS
		return response.isSuccessful();
	}
	
	public boolean actualizarPuesto(Puesto cambiar) throws IOException {
		Call<ResponseBody> call = client.getDB().actualizarPuesto(cambiar);
		Response<ResponseBody> response = call.execute();//AQUI ES DONDE SE LLAMA A LA BASE DE DATOS
		return response.isSuccessful();
	}
}
