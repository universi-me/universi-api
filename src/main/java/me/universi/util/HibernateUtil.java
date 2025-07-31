package me.universi.util;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class HibernateUtil {
    public static <T> T resolveLazyHibernateObject(T object) {
        if (object instanceof HibernateProxy) {
            if(!Hibernate.isInitialized(object)) {
                Hibernate.initialize(object);
            }
            return (T) ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
        }
        return object;
    }
}
