package com.wz.arouter;

/**
 * author: wangzheng
 * create on: 2018/1/11
 * description:
 */

public class BindViews {

    public static void inject(Object sourceAndTarget) {
        inject(sourceAndTarget, sourceAndTarget);
    }

    public static void inject(Object source, Object target) {
        Class targetClass = target.getClass();
        ViewInjector<Object> injector = findInjectorByClass(targetClass);
        if (injector != null) {
            injector.inject(source, target);
        }
    }

    private static ViewInjector<Object> findInjectorByClass(Class<?> clazz) {
        ViewInjector<Object> injector = null;
        try {
            String targetName = clazz.getName();
            Class<?> injectorClass = Class.forName(targetName + SEPARATOR + "ViewInjector");
            injector = (ViewInjector<Object>) injectorClass.newInstance();
        } catch (ClassNotFoundException e) {
            injector = findInjectorByClass(clazz.getSuperclass());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return injector;
    }

    public final static String SEPARATOR = "_";
}
