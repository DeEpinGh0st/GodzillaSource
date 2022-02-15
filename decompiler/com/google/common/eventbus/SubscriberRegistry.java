package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;






























final class SubscriberRegistry
{
  private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = Maps.newConcurrentMap();
  
  @Weak
  private final EventBus bus;
  
  SubscriberRegistry(EventBus bus) {
    this.bus = (EventBus)Preconditions.checkNotNull(bus);
  }

  
  void register(Object listener) {
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);
    
    for (Map.Entry<Class<?>, Collection<Subscriber>> entry : (Iterable<Map.Entry<Class<?>, Collection<Subscriber>>>)listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> eventMethodsInListener = entry.getValue();
      
      CopyOnWriteArraySet<Subscriber> eventSubscribers = this.subscribers.get(eventType);
      
      if (eventSubscribers == null) {
        CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
        
        eventSubscribers = (CopyOnWriteArraySet<Subscriber>)MoreObjects.firstNonNull(this.subscribers.putIfAbsent(eventType, newSet), newSet);
      } 
      
      eventSubscribers.addAll(eventMethodsInListener);
    } 
  }

  
  void unregister(Object listener) {
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);
    
    for (Map.Entry<Class<?>, Collection<Subscriber>> entry : (Iterable<Map.Entry<Class<?>, Collection<Subscriber>>>)listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> listenerMethodsForType = entry.getValue();
      
      CopyOnWriteArraySet<Subscriber> currentSubscribers = this.subscribers.get(eventType);
      if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType))
      {


        
        throw new IllegalArgumentException("missing event subscriber for an annotated method. Is " + listener + " registered?");
      }
    } 
  }




  
  @VisibleForTesting
  Set<Subscriber> getSubscribersForTesting(Class<?> eventType) {
    return (Set<Subscriber>)MoreObjects.firstNonNull(this.subscribers.get(eventType), ImmutableSet.of());
  }




  
  Iterator<Subscriber> getSubscribers(Object event) {
    ImmutableSet<Class<?>> eventTypes = flattenHierarchy(event.getClass());

    
    List<Iterator<Subscriber>> subscriberIterators = Lists.newArrayListWithCapacity(eventTypes.size());
    
    for (UnmodifiableIterator<Class<?>> unmodifiableIterator = eventTypes.iterator(); unmodifiableIterator.hasNext(); ) { Class<?> eventType = unmodifiableIterator.next();
      CopyOnWriteArraySet<Subscriber> eventSubscribers = this.subscribers.get(eventType);
      if (eventSubscribers != null)
      {
        subscriberIterators.add(eventSubscribers.iterator());
      } }

    
    return Iterators.concat(subscriberIterators.iterator());
  }







  
  private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder()
    .weakKeys()
    .build(new CacheLoader<Class<?>, ImmutableList<Method>>()
      {
        public ImmutableList<Method> load(Class<?> concreteClass) throws Exception
        {
          return SubscriberRegistry.getAnnotatedMethodsNotCached(concreteClass);
        }
      });



  
  private Multimap<Class<?>, Subscriber> findAllSubscribers(Object listener) {
    HashMultimap hashMultimap = HashMultimap.create();
    Class<?> clazz = listener.getClass();
    for (UnmodifiableIterator<Method> unmodifiableIterator = getAnnotatedMethods(clazz).iterator(); unmodifiableIterator.hasNext(); ) { Method method = unmodifiableIterator.next();
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> eventType = parameterTypes[0];
      hashMultimap.put(eventType, Subscriber.create(this.bus, listener, method)); }
    
    return (Multimap<Class<?>, Subscriber>)hashMultimap;
  }
  
  private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
    return (ImmutableList<Method>)subscriberMethodsCache.getUnchecked(clazz);
  }
  
  private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
    Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
    Map<MethodIdentifier, Method> identifiers = Maps.newHashMap();
    for (Class<?> supertype : supertypes) {
      for (Method method : supertype.getDeclaredMethods()) {
        if (method.isAnnotationPresent((Class)Subscribe.class) && !method.isSynthetic()) {
          
          Class<?>[] parameterTypes = method.getParameterTypes();
          Preconditions.checkArgument((parameterTypes.length == 1), "Method %s has @Subscribe annotation but has %s parameters.Subscriber methods must have exactly 1 parameter.", method, parameterTypes.length);





          
          MethodIdentifier ident = new MethodIdentifier(method);
          if (!identifiers.containsKey(ident)) {
            identifiers.put(ident, method);
          }
        } 
      } 
    } 
    return ImmutableList.copyOf(identifiers.values());
  }


  
  private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder()
    .weakKeys()
    .build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>()
      {

        
        public ImmutableSet<Class<?>> load(Class<?> concreteClass)
        {
          return ImmutableSet.copyOf(
              TypeToken.of(concreteClass).getTypes().rawTypes());
        }
      });




  
  @VisibleForTesting
  static ImmutableSet<Class<?>> flattenHierarchy(Class<?> concreteClass) {
    try {
      return (ImmutableSet<Class<?>>)flattenHierarchyCache.getUnchecked(concreteClass);
    } catch (UncheckedExecutionException e) {
      throw Throwables.propagate(e.getCause());
    } 
  }
  
  private static final class MethodIdentifier
  {
    private final String name;
    private final List<Class<?>> parameterTypes;
    
    MethodIdentifier(Method method) {
      this.name = method.getName();
      this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }

    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.name, this.parameterTypes });
    }

    
    public boolean equals(Object o) {
      if (o instanceof MethodIdentifier) {
        MethodIdentifier ident = (MethodIdentifier)o;
        return (this.name.equals(ident.name) && this.parameterTypes.equals(ident.parameterTypes));
      } 
      return false;
    }
  }
}
