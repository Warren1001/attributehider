package com.kabryxis.attributehider.version.wrapper;

import com.kabryxis.kabutils.cache.Cache;

public abstract class Wrappable<T> {
	
	protected T object;
	
	public void set(T object) {
		this.object = object;
	}
	
	public T get() {
		return object;
	}
	
	public void setHandle(Object object) {
		set((T)object);
	}
	
	public Object getHandle() {
		return object;
	}
	
	public void cache() {
		object = null;
		Cache.cache(this);
	}
	
}
