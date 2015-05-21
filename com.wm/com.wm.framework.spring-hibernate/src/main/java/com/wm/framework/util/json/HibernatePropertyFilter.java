package com.wm.framework.util.json;

import net.sf.json.util.PropertyFilter;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public class HibernatePropertyFilter implements PropertyFilter{
	
	@Override
	public boolean apply(Object obj, String name, Object value) {
		if (value instanceof HibernateProxy) {// hibernate代理对象
			LazyInitializer initializer = ((HibernateProxy) value)
					.getHibernateLazyInitializer();
			if (initializer.isUninitialized()) {
				return true;
			}
		} else if (value instanceof PersistentSet) {// hibernate代理Set集合
			PersistentCollection collection = (PersistentCollection) value;
			if (!collection.wasInitialized()) {
				return true;
			}
			Object val = collection.getValue();
			if (val == null) {
				return true;
			}
		} else if (value instanceof PersistentCollection) {// hibernate代理List集合
			PersistentCollection collection = (PersistentCollection) value;
			if (!collection.wasInitialized()) {
				return true;
			}
			Object val = collection.getValue();
			if (val == null) {
				return true;
			}
		}
		return false;
	}
}
