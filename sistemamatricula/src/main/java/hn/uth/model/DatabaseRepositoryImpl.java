package hn.uth.model;

import java.io.IOException;

import hn.uth.data.TrabajadoresResponse;
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
}
