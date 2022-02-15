package org.springframework.expression.spel.ast;

import java.util.ArrayList;
import java.util.List;
import org.springframework.expression.PropertyAccessor;
import org.springframework.lang.Nullable;






































public abstract class AstUtils
{
  public static List<PropertyAccessor> getPropertyAccessorsToTry(@Nullable Class<?> targetType, List<PropertyAccessor> propertyAccessors) {
    List<PropertyAccessor> specificAccessors = new ArrayList<>();
    List<PropertyAccessor> generalAccessors = new ArrayList<>();
    for (PropertyAccessor resolver : propertyAccessors) {
      Class<?>[] targets = resolver.getSpecificTargetClasses();
      if (targets == null) {
        generalAccessors.add(resolver);
        continue;
      } 
      if (targetType != null) {
        for (Class<?> clazz : targets) {
          if (clazz == targetType) {
            specificAccessors.add(resolver);
          }
          else if (clazz.isAssignableFrom(targetType)) {
            
            generalAccessors.add(resolver);
          } 
        } 
      }
    } 
    
    List<PropertyAccessor> resolvers = new ArrayList<>(specificAccessors.size() + generalAccessors.size());
    resolvers.addAll(specificAccessors);
    resolvers.addAll(generalAccessors);
    return resolvers;
  }
}
