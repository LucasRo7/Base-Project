package com.LS7.util;

import java.util.ArrayList;

public class AIEngine<T> {
	
	// TODO Rest
	public abstract class state{
		public String name;
		public state(String name){
			this.name=name;
			index=0;
		}
		public abstract void run(T obj);
	}
	
	public int index;
	public ArrayList<state> states=new ArrayList<state>();
	public T object;
	
	public AIEngine(T obj){
		object=obj;
		index=0;
	}
	public void nextState(){
		index++;
	}
	public void gotoState(int i){
		index=i;
	}
	public void run(){
		if(index>states.size()){
			index=0;
		}
		try{
			states.get(index).run(object);
		}catch(Exception e){}
	}
	
}
