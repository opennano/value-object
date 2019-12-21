package com.github.opennano.valuegen.generator.delegates;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.github.opennano.valuegen.GeneratorConfig;
import com.github.opennano.valuegen.generator.TypeInfo;
import com.github.opennano.valuegen.generator.ValueGeneratorDelegate;
import com.github.opennano.valuegen.generator.ValueObjectGenerator;
import com.github.opennano.valuegen.generator.strategies.CycleStrategy;
import com.github.opennano.valuegen.generator.strategies.SubtypeStrategy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ObjectValueDelegate implements ValueGeneratorDelegate {

  private CycleStrategy cycleStrategy;
  private SubtypeStrategy subtypeStrategy;

  private Map<Class<?>, Object> valueCache = new LinkedHashMap<>();

  public ObjectValueDelegate(GeneratorConfig config) {
    cycleStrategy = config.getCycleStrategy();
    subtypeStrategy = config.getSubTypeStrategy();
  }

  @Override
  public boolean handlesClass(Class<?> type) {
    return true;
  }

  @Override
  public Object generateValue(
      TypeInfo typeInfo,
      Field declaringField,
      String nameHint,
      Class<?> owningClass,
      ValueObjectGenerator valueGenerator) {

    // what to do if we get an uninstantiable type (abstract class or interface)?
    // defer to the strategy class to try and find an instantiable subtype
    // note that the strategy does not guarantee it can find a suitable subtype!
    Class<?> valueClass = typeInfo.getResolvedClass();
    if (typeInfo.isAbstract()) {
      try {
        valueClass = subtypeStrategy.apply(typeInfo);
      } catch (Exception e) {
        // TODO logging
        e.printStackTrace();
        return null;
      }
    }

    // handle Objects now or else they'll fail the core Java filter below
    if (valueClass == Object.class) {
      return new Object();
    }

    // if we got a null there's nothing we can do
    // unless there are some interfaces (in which case we can make a proxy)
    Object valueObject;
    if (valueClass == null && typeInfo.getInterfaces().length == 0) {
      return null;
    }

    // if nothing could be done to find a suitable concrete type then use a proxy
    // proxies aren't super useful values but are slightly more helpful than null
    if (valueClass == null
        || valueClass.isInterface()
        || Modifier.isAbstract(valueClass.getModifiers())) {
      return proxiedClass(typeInfo);
    }

    // if we got a core Java type bail
    // do we want to maybe try to handle generating core Java types?
    // not terribly useful and probably very messy...
    //    if (isCoreJava(valueClass)) {
    //      return null;
    //    }

    // are we already in the process of creating a value object of the same type?
    // if so then we've found a cycle--figure out how to handle it
    Object cachedValue = valueCache.get(valueClass);
    if (cachedValue != null) {
      return cycleStrategy.apply(valueClass, cachedValue);
    }

    // no short-circuits apply--we should create a new object
    try {
      valueObject = objenesis.newInstance(valueClass);
      valueCache.put(valueClass, valueObject); // put now before recursing!
      visitFields(valueClass, fld -> generateAndSetFieldValue(fld, valueObject, valueGenerator));
      return valueObject;
    } finally {
      valueCache.remove(valueClass);
    }
  }

  private Object proxiedClass(TypeInfo info) {
    // FIXME info.getPrimaryType
    Class<?> superclass = info.getResolvedClass();
    Class<?>[] interfaces = info.getAdditionalInterfaces();

    // resolved class can end up being an interface
    // cglib needs the superclass and interfaces to be separated
    if (superclass.isInterface()) {
      interfaces =
          Stream.concat(Stream.of(superclass), Stream.of(interfaces)).toArray(Class<?>[]::new);

      superclass = Object.class;
    }

    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(superclass);
    enhancer.setInterfaces(interfaces);
    enhancer.setCallback(
        (MethodInterceptor)
            (obj, method, args, proxy) -> {
              if (method.getDeclaringClass() != Object.class) {
                throw new IllegalAccessException("method calls not supported on this proxy!");
              } else {
                return proxy.invokeSuper(obj, args);
              }
            });

    return enhancer.create();
  }

  public void visitFields(Class<?> valueClass, FieldVisitor visitor) {
    getTypeHeirarchy(valueClass)
        .stream()
        .map(Class::getDeclaredFields)
        .flatMap(Stream::of)
        .filter(this::shouldInclude)
        .forEach(visitor::visit);
  }

  private List<Class<?>> getTypeHeirarchy(Class<?> valueClass) {
    List<Class<?>> typeHeirarchy = new ArrayList<>();
    Class<?> ancestorClass = valueClass;
    do {
      typeHeirarchy.add(ancestorClass);
      ancestorClass = ancestorClass.getSuperclass();
    } while (ancestorClass != null && ancestorClass != Object.class);

    // sort by correct heirarchical order (ancestors first)
    // this seems to produce the most intuitive results
    Collections.reverse(typeHeirarchy);
    return typeHeirarchy;
  }

  private boolean shouldInclude(Field field) {
    return !field.isSynthetic() // ignore things added by the JVM
        && !Modifier.isTransient(field.getModifiers()) // ignore transients
        && !Modifier.isStatic(field.getModifiers()); // ignore static fields
  }

  private void generateAndSetFieldValue(
      Field field, Object owningObject, ValueObjectGenerator valueGenerator) {

    try {
      Object value = valueGenerator.valueFor(field, owningObject.getClass());

      // defeat access modifiers (private and final, in particular)
      field.setAccessible(true);
      field.set(owningObject, value);
    } catch (Exception e) {
      // if we weren't able to generate or set a value just leave the default value
      // TODO log
    }
  }

  private static interface FieldVisitor {
    void visit(Field field);
  }
}
