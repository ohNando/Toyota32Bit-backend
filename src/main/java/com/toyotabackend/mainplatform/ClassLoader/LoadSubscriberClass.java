package com.toyotabackend.mainplatform.ClassLoader;

import java.lang.reflect.InvocationTargetException;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;

/**
 * Class responsible for loading subscriber classes dynamically based on configuration.
 * <p>
 * This class reads the class names of the subscribers from the application properties
 * and dynamically loads the subscriber classes using reflection. It is used to provide
 * flexibility in adding new subscriber implementations without modifying the code.
 * </p>
 */
public class LoadSubscriberClass {
    private static final String subscriberClassPath = "com.toyotabackend.mainplatform.Client.";


    /**
     * Loads the subscriber classes dynamically from the configuration.
     * <p>
     * This method reads the class names of two subscribers from the application properties
     * file and then creates instances of those classes using reflection. The created instances
     * are then cast to the {@link DataProvider} interface type.
     * </p>
     */
    public static SubscriberInterface loadSubscriber(String subscriberName,
                                            Class<?>[] paramTypes,
                                            Object[] params) {
        Class<?> loadedClass = null;
        try {
            loadedClass = Class.forName(subscriberClassPath + subscriberName);
        }catch(ClassNotFoundException exception){
            throw new RuntimeException(exception);
        }

        try{
            return (SubscriberInterface) loadedClass.getConstructor(paramTypes).newInstance();
        }catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
