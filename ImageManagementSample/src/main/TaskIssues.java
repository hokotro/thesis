package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TaskIssues {

	ConcurrentMap<String, List<String>> map = new ConcurrentHashMap<String, List<String>>(); 
	
	
	public TaskIssues(){}
	
	
	public synchronized void put(String key, String value){
		List<String> list = map.get(key);
		
		if(list != null){
			list.add(value);
			
		}else{
			list = new ArrayList<String>();
			list.add(value);
			map.put(key, list);
		}
				
	}
	
	public synchronized boolean isKeyEmpty(String key){
		if(map.get(key) != null)
			return map.get(key).isEmpty();
		return false;
		//throw new IllegalArgumentException("The key is null: containing no mapping for this key");
	}
	
	public synchronized void removeValue(String key, String val){
		if(map.get(key) != null) 
			map.get(key).remove(val);
	}
	
	public synchronized void removeKey(String key){
		if( map.get(key).isEmpty() ){
			map.remove(key);
			return;
		}
		throw new IllegalArgumentException("The value list is not empty.");
		
	}


	@Override
	public synchronized String toString() {
		StringBuilder str = new StringBuilder();
		for(String key : map.keySet()){
			str.append( "[ " + key + ": [ " );
			for(String val: map.get(key)){
				str.append(val + " ");
			}
			str.append("] ] ; ");
		}
		return str.toString();
	}
	
	
	
}
