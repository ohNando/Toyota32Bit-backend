package com.toyotabackend.mainplatform.ClassLoader;

import java.lang.reflect.InvocationTargetException;

import com.toyotabackend.mainplatform.Client.SubscriberInterface;

/**
 * Utility class responsible for dynamically loading subscriber classes using reflection.
 * <p>
 * Enables flexible integration of new subscriber implementations at runtime
 * based on class names provided in the configuration.
 */
public class LoadSubscriberClass {

    /**
     * Loads and instantiates a subscriber class at runtime using reflection.
     *
     * @param subscriberPath the fully qualified class name of the subscriber
     * @param paramTypes the constructor parameter types
     * @param params the constructor parameters
     * @return an instance of {@link SubscriberInterface}
     * @throws RuntimeException if the class cannot be loaded or instantiated
     */
    public static SubscriberInterface loadSubscriber(String subscriberPath,
                                                     Class<?>[] paramTypes,
                                                     Object[] params) {
        try {
            Class<?> loadedClass = Class.forName(subscriberPath);
            return (SubscriberInterface) loadedClass
                    .getConstructor(paramTypes)
                    .newInstance(params);
        } catch (ClassNotFoundException |
                 InstantiationException |
                 IllegalAccessException |
                 InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to load subscriber class: " + subscriberPath, e);
        }
    }
}
