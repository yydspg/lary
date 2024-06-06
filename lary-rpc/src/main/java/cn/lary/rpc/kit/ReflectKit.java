package cn.lary.rpc.kit;

import cn.lary.rpc.anno.RpcInject;
import cn.lary.rpc.anno.RpcService;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Stream;

public class ReflectKit {
    private Reflections reflections;
    private static ReflectKit reflectKit;
    static {
        reflectKit = new ReflectKit("cn.lary.rpc");
    }
    public ReflectKit(String packageAddress) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        String[] addresses = packageAddress.split(",");
        Stream.of(addresses).forEach(str -> configurationBuilder.addUrls(ClasspathHelper.forPackage(str.trim())));
        configurationBuilder.setScanners(Scanners.FieldsAnnotated, Scanners.MethodsAnnotated,Scanners.TypesAnnotated);
        reflections =  new Reflections(configurationBuilder);
    }
    public static ReflectKit get() {
        return reflectKit;
    }
    public <T extends Annotation> Set<Class<?>> getBeans(Class<T> anno) {
        return reflections.getTypesAnnotatedWith(anno);
    }
    public <T extends Annotation> Set<Field> getFields(Class<T> anno) {
        return reflections.getFieldsAnnotatedWith(anno);
    }

}
