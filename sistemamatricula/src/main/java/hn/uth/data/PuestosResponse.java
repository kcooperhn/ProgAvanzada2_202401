package hn.uth.data;

import java.util.List;

public class PuestosResponse {
	private List<Puesto> items;
	private int count;
	
	public List<Puesto> getItems() {
		return items;
	}
	public void setItems(List<Puesto> items) {
		this.items = items;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
