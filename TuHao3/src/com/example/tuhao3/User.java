package com.example.tuhao3;

public class User {

	public String id = "";
	public String name = "";
	public String displayName = "";
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return id.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof User){
			User tareget = (User)o;
			return id.equals(tareget.id);
		}
		return false;
	}

}
