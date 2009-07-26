package com.tzavellas.circuitbreaker.support;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class ClassSet {
	
	private List<Class<?>> classes = new CopyOnWriteArrayList<Class<?>>();

	public void add(Class<?> c) {
		if (c == null) return;
		for (Class<?> cls: classes)
			if (c.isAssignableFrom(cls)) {
				classes.remove(cls);
			} else if (cls.isAssignableFrom(c)) {
				return;
			}
		classes.add(c);
	}

	public void addAll(Collection<? extends Class<?>> classes) {
		if (classes == null) return;
		for (Class<?> c: classes) add(c);
	}
	
	public boolean contains(Class<?> c) {
		for (Class<?> cls: classes)
			if (cls.isAssignableFrom(c))
				return true;
		return false;
	}

	public boolean isEmpty() {
		return classes.isEmpty();
	}
	
	public int size() {
		return classes.size();
	}
	
	public void clear() {
		classes.clear();
	}

	public boolean remove(Class<?> c) {
		return classes.remove(c);
	}
}
