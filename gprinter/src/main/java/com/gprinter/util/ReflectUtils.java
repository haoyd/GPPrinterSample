// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;

public class ReflectUtils
{
    public static <T, E> E getFieldValue(final T t, final Class<T> clazz, final String filedName) {
        if (t == null) {
            return null;
        }
        try {
            final Field field = clazz.getDeclaredField(filedName);
            final boolean isAccessable = field.isAccessible();
            field.setAccessible(true);
            field.setAccessible(isAccessable);
            return (E)field.get(t);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        catch (SecurityException e3) {
            e3.printStackTrace();
        }
        catch (NoSuchFieldException e4) {
            e4.printStackTrace();
        }
        return null;
    }
    
    public static <T, E> E getMethodValue(final T t, final Class<T> clazz, final String methodName) {
        if (t == null) {
            return null;
        }
        try {
            final Method method = clazz.getDeclaredMethod(methodName, (Class<?>[])new Class[0]);
            final Object mValue = method.invoke(t, new Object[0]);
            return (E)mValue;
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e2) {
            e2.printStackTrace();
        }
        catch (SecurityException e3) {
            e3.printStackTrace();
        }
        catch (NoSuchMethodException e4) {
            e4.printStackTrace();
        }
        catch (IllegalArgumentException e5) {
            e5.printStackTrace();
        }
        return null;
    }
    
    public static <S, D> D mappingFieldByField(final S s, final D d) {
        if (s == null || d == null) {
            return d;
        }
        final Field[] sfields = s.getClass().getDeclaredFields();
        final Field[] dfields = d.getClass().getDeclaredFields();
        try {
            for (final Field sfield : sfields) {
                final String sName = sfield.getName();
                final Class sType = sfield.getType();
                sfield.setAccessible(true);
                final boolean sisAccessable = sfield.isAccessible();
                for (final Field dfield : dfields) {
                    final String dName = dfield.getName();
                    final Class dType = dfield.getType();
                    if (sName.equals(dName) && sType.toString().equals(dType.toString())) {
                        final boolean disAccessable = dfield.isAccessible();
                        dfield.setAccessible(true);
                        dfield.set(d, sfield.get(s));
                        dfield.setAccessible(disAccessable);
                        break;
                    }
                }
                sfield.setAccessible(sisAccessable);
            }
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (SecurityException e2) {
            e2.printStackTrace();
        }
        catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        }
        return d;
    }
    
    public static <S, D> D mappingFieldByMethod(final S s, final D d) {
        if (s == null || d == null) {
            return d;
        }
        final Field[] sfields = s.getClass().getDeclaredFields();
        final Field[] dfields = d.getClass().getDeclaredFields();
        final Class scls = s.getClass();
        final Class dcls = d.getClass();
        try {
            for (final Field sfield : sfields) {
                final String sName = sfield.getName();
                final Class sType = sfield.getType();
                final String sfieldName = sName.substring(0, 1).toUpperCase() + sName.substring(1);
                final Method sGetMethod = scls.getMethod("get" + sfieldName, (Class[])new Class[0]);
                final Object value = sGetMethod.invoke(s, new Object[0]);
                for (final Field dfield : dfields) {
                    final String dName = dfield.getName();
                    final Class dType = dfield.getType();
                    if (dName.equals(sName) && sType.toString().equals(dType.toString())) {
                        final Method dSetMethod = dcls.getMethod("set" + sfieldName, sType);
                        dSetMethod.invoke(d, value);
                        break;
                    }
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
        catch (IllegalArgumentException e5) {
            e5.printStackTrace();
        }
        return d;
    }
    
    public static <S, D> D mappingFieldByMethodExcludeParamNames(final S s, final D d, final String... excludeParamNames) {
        if (s == null || d == null) {
            return d;
        }
        final Field[] sfields = s.getClass().getDeclaredFields();
        final Field[] dfields = d.getClass().getDeclaredFields();
        final Class scls = s.getClass();
        final Class dcls = d.getClass();
        try {
            for (final Field sfield : sfields) {
                final String sName = sfield.getName();
                final Class sType = sfield.getType();
                final String sfieldName = sName.substring(0, 1).toUpperCase() + sName.substring(1);
                final Method sGetMethod = scls.getMethod("get" + sfieldName, (Class[])new Class[0]);
                final Object value = sGetMethod.invoke(s, new Object[0]);
                for (final Field dfield : dfields) {
                    final String dName = dfield.getName();
                    final Class dType = dfield.getType();
                    if (dName.equals(sName) && sType.toString().equals(dType.toString()) && excludeParamNames != null) {
                        for (final String excludeParamName : excludeParamNames) {
                            if (!sName.equals(excludeParamName)) {
                                final Method dSetMethod = dcls.getMethod("set" + sfieldName, sType);
                                dSetMethod.invoke(d, value);
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
        catch (IllegalArgumentException e5) {
            e5.printStackTrace();
        }
        return d;
    }
    
    public static <S, D> D mappingFieldByMethodIncludeParamNames(final S s, final D d, final String... includeParamNames) {
        if (s == null || d == null) {
            return d;
        }
        final Field[] sfields = s.getClass().getDeclaredFields();
        final Field[] dfields = d.getClass().getDeclaredFields();
        final Class scls = s.getClass();
        final Class dcls = d.getClass();
        try {
            for (final Field sfield : sfields) {
                final String sName = sfield.getName();
                final Class sType = sfield.getType();
                final String sfieldName = sName.substring(0, 1).toUpperCase() + sName.substring(1);
                final Method sGetMethod = scls.getMethod("get" + sfieldName, (Class[])new Class[0]);
                final Object value = sGetMethod.invoke(s, new Object[0]);
                if (includeParamNames != null) {
                    for (final String excludeParam : includeParamNames) {
                        if (sName.equals(excludeParam)) {
                            for (final Field dfield : dfields) {
                                final String dName = dfield.getName();
                                final Class dType = dfield.getType();
                                if (dName.equals(sName) && sType.toString().equals(dType.toString())) {
                                    final Method dSetMethod = dcls.getMethod("set" + sfieldName, sType);
                                    dSetMethod.invoke(d, value);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
        catch (IllegalAccessException e3) {
            e3.printStackTrace();
        }
        catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
        catch (IllegalArgumentException e5) {
            e5.printStackTrace();
        }
        return d;
    }
}
