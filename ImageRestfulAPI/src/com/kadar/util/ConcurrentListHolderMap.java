package com.kadar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentListHolderMap<K,V> {

	private final Map<K, List<V>> map = new ConcurrentHashMap<K, List<V>>(); 
	
	public ConcurrentListHolderMap(){
		
	}
	
	public synchronized void put(K key, V value){
		List<V> list = map.get(key);
		
		if(list != null){
			list.add(value);
			
		}else{
			list = new ArrayList<V>();
			list.add(value);
			map.put(key, list);
		}
				
	}
	
	public synchronized boolean isKeyEmpty(K key){
		if(map.get(key) != null)
			return map.get(key).isEmpty();
		return false;
		//throw new IllegalArgumentException("The key is null: containing no mapping for this key");
	}
	
	public synchronized boolean removeValue(K key, V val){
		if(map.get(key) != null) 
			if(map.get(key).remove(val)){
				if(map.get(key).isEmpty())
					map.remove(key);
				return true;
			} else {
				throw new IllegalArgumentException("The specified key-value pair is not exists");
			}
		else
			throw new IllegalArgumentException("The specified key element is not exists");
	}
	
	public synchronized void removeKey(K key){
		if( map.get(key).isEmpty() ){
			map.remove(key);
			return;
		}
		throw new IllegalArgumentException("The value list is not empty.");		
	}


	@Override
	public synchronized String toString() {
		StringBuilder str = new StringBuilder();
		for(K key : map.keySet()){
			str.append( "[ " + key + ": [ " );
			for(V val: map.get(key)){
				str.append(val + " ");
			}
			str.append("] ] ; ");
		}
		return str.toString();
	}
		
}
