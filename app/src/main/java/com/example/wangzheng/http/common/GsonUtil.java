package com.example.wangzheng.http.common;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * Created by wangzheng on 2016/7/8.
 */
public class GsonUtil {
    public static Gson mGson = new Gson();

    public static <T> T fromJson(String json, Type type) {
        T data = null;
        try {
            data = mGson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String toJson(Object src) {
        return mGson.toJson(src);
    }

    public static Type getSuperclassType(Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            return Object.class;
        }
        ParameterizedType parameterized =
                (ParameterizedType) genericSuperclass;
        return canonicalize(parameterized.
                getActualTypeArguments()[0]);
    }

    public static Type comboType(final Type actualType,
                                 final Type rawType) {
        return new ParameterizedType() {
            public Type[] getActualTypeArguments() {
                return new Type[]{actualType};
            }
            public Type getOwnerType() {
                return null;
            }
            public Type getRawType() {
                return rawType;
            }
        };
    }

    public static Type canonicalize(Type type) {
        if (type instanceof Class) {
            final Class<?> c = (Class) type;
            return c.isArray() ? new GenericArrayType() {
                public Type getGenericComponentType() {
                    return c.getComponentType();
                }
            } : c;
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType p = (ParameterizedType) type;
            return new ParameterizedType() {
                public Type[] getActualTypeArguments() {
                    return p.getActualTypeArguments();
                }
                public Type getRawType() {
                    return p.getRawType();
                }
                public Type getOwnerType() {
                    return p.getOwnerType();
                }
            };
        } else if (type instanceof GenericArrayType) {
            final GenericArrayType g = (GenericArrayType) type;
            return new GenericArrayType() {
                public Type getGenericComponentType() {
                    return g.getGenericComponentType();
                }
            };
        } else if (type instanceof WildcardType) {
            final WildcardType w = (WildcardType) type;
            return new WildcardType() {
                public Type[] getUpperBounds() {
                    return w.getUpperBounds();
                }
                public Type[] getLowerBounds() {
                    return w.getLowerBounds();
                }
            };
        }else if (type instanceof TypeVariable) {
            final TypeVariable t = (TypeVariable) type;
            return new TypeVariable() {
                public Type[] getBounds() {
                    return t.getBounds();
                }
                public GenericDeclaration getGenericDeclaration() {
                    return t.getGenericDeclaration();
                }
                public String getName() {
                    return t.getName();
                }
            };
        } else {
            return type;
        }
    }

    public static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType)type;
            Type rawType = parameterizedType.getRawType();
            return (Class)rawType;
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(getRawType(componentType), 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class;
        } else if (type instanceof WildcardType) {
            return getRawType(((WildcardType)type).getUpperBounds()[0]);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType, " +
                    "or GenericArrayType, but <" + type + "> is of type " + className);
        }
    }
}
