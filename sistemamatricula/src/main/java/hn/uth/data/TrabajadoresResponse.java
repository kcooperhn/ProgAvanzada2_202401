package hn.uth.data;

import java.util.List;

public class TrabajadoresResponse {

	private List<Empleado> items;
	private int count;
	
	public List<Empleado> getItems() {
		return items;
	}
	public void setItems(List<Empleado> items) {
		this.items = items;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
