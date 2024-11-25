package dev.latvian.kubejs.event.forge;

import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.annotations.typing.JSInfo;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
@JSInfo("""
    This class is made for type wrapping purpose, so you don't need to load this class
    
    just provide a string that holds the class name or the class itself loaded by `java(...)` whenever `ClassConvertible` is required""")
public interface ClassConvertible extends Supplier<Class<?>> {

    static ClassConvertible fromRaw(Class<?> raw) {
        return () -> raw;
    }

    static ClassConvertible fromJS(NativeJavaClass njc) {
        return njc::getClassObject;
    }

    static ClassConvertible fromName(String className) {
        return () -> {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    static ClassConvertible of(Object o) {
        if (o instanceof ClassConvertible) {
            return (ClassConvertible) o;
        } else if (o instanceof CharSequence) {
            return fromName(o.toString());
        } else if (o instanceof Class) {
            return fromRaw((Class) o);
        } else if (o instanceof NativeJavaClass) {
            return fromJS((NativeJavaClass) o);
        }
        return null;
    }
}