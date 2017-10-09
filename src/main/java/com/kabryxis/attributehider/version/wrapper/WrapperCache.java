package com.kabryxis.attributehider.version.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import com.kabryxis.attributehider.version.Version;
import com.kabryxis.kabutils.cache.Cache;

public class WrapperCache {
	
	private final static Map<Class<? extends Wrappable<?>>, Class<? extends Wrappable<?>>> implementations = new HashMap<>();
	
	public static <T extends Wrappable<?>> T get(Class<T> clazz) {
		Class<? extends Wrappable<?>> implementation = implementations.get(clazz);
		if(implementation == null) {
			String name = clazz.getSimpleName();
			try {
				implementation = (Class<? extends Wrappable<?>>)Class.forName(clazz.getPackage().getName() + ".impl." + name + Version.STRING);
				implementations.put(clazz, implementation);
				Cache.allocateCache(implementation, new LinkedBlockingQueue<>());
			}
			catch(ClassNotFoundException e) {
				throw new IllegalStateException("Could not find an implementation for " + name + " for the current Spigot version.", e);
			}
		}
		return (T)Cache.get(implementation);
	}
	
}
