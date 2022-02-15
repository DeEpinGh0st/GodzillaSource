package org.springframework.core;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import kotlinx.coroutines.CompletableDeferredKt;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import kotlinx.coroutines.reactor.ReactorFlowKt;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Single;





















public class ReactiveAdapterRegistry
{
  @Nullable
  private static volatile ReactiveAdapterRegistry sharedInstance;
  private static final boolean reactorPresent;
  private static final boolean rxjava1Present;
  private static final boolean rxjava2Present;
  private static final boolean rxjava3Present;
  private static final boolean flowPublisherPresent;
  private static final boolean kotlinCoroutinesPresent;
  private static final boolean mutinyPresent;
  
  static {
    ClassLoader classLoader = ReactiveAdapterRegistry.class.getClassLoader();
    reactorPresent = ClassUtils.isPresent("reactor.core.publisher.Flux", classLoader);
    flowPublisherPresent = ClassUtils.isPresent("java.util.concurrent.Flow.Publisher", classLoader);
    
    rxjava1Present = (ClassUtils.isPresent("rx.Observable", classLoader) && ClassUtils.isPresent("rx.RxReactiveStreams", classLoader));
    rxjava2Present = ClassUtils.isPresent("io.reactivex.Flowable", classLoader);
    rxjava3Present = ClassUtils.isPresent("io.reactivex.rxjava3.core.Flowable", classLoader);
    kotlinCoroutinesPresent = ClassUtils.isPresent("kotlinx.coroutines.reactor.MonoKt", classLoader);
    mutinyPresent = ClassUtils.isPresent("io.smallrye.mutiny.Multi", classLoader);
  }
  
  private final List<ReactiveAdapter> adapters = new ArrayList<>();






  
  public ReactiveAdapterRegistry() {
    if (reactorPresent) {
      (new ReactorRegistrar()).registerAdapters(this);
      if (flowPublisherPresent)
      {
        (new ReactorJdkFlowAdapterRegistrar()).registerAdapter(this);
      }
    } 

    
    if (rxjava1Present) {
      (new RxJava1Registrar()).registerAdapters(this);
    }
    if (rxjava2Present) {
      (new RxJava2Registrar()).registerAdapters(this);
    }
    if (rxjava3Present) {
      (new RxJava3Registrar()).registerAdapters(this);
    }

    
    if (reactorPresent && kotlinCoroutinesPresent) {
      (new CoroutinesRegistrar()).registerAdapters(this);
    }

    
    if (mutinyPresent) {
      (new MutinyRegistrar()).registerAdapters(this);
    }
  }




  
  public boolean hasAdapters() {
    return !this.adapters.isEmpty();
  }







  
  public void registerReactiveType(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
    if (reactorPresent) {
      this.adapters.add(new ReactorAdapter(descriptor, toAdapter, fromAdapter));
    } else {
      
      this.adapters.add(new ReactiveAdapter(descriptor, toAdapter, fromAdapter));
    } 
  }




  
  @Nullable
  public ReactiveAdapter getAdapter(Class<?> reactiveType) {
    return getAdapter(reactiveType, null);
  }









  
  @Nullable
  public ReactiveAdapter getAdapter(@Nullable Class<?> reactiveType, @Nullable Object source) {
    if (this.adapters.isEmpty()) {
      return null;
    }
    
    Object sourceToUse = (source instanceof Optional) ? ((Optional)source).orElse(null) : source;
    Class<?> clazz = (sourceToUse != null) ? sourceToUse.getClass() : reactiveType;
    if (clazz == null) {
      return null;
    }
    for (ReactiveAdapter adapter : this.adapters) {
      if (adapter.getReactiveType() == clazz) {
        return adapter;
      }
    } 
    for (ReactiveAdapter adapter : this.adapters) {
      if (adapter.getReactiveType().isAssignableFrom(clazz)) {
        return adapter;
      }
    } 
    return null;
  }











  
  public static ReactiveAdapterRegistry getSharedInstance() {
    ReactiveAdapterRegistry registry = sharedInstance;
    if (registry == null) {
      synchronized (ReactiveAdapterRegistry.class) {
        registry = sharedInstance;
        if (registry == null) {
          registry = new ReactiveAdapterRegistry();
          sharedInstance = registry;
        } 
      } 
    }
    return registry;
  }









  
  private static class ReactorAdapter
    extends ReactiveAdapter
  {
    ReactorAdapter(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toPublisherFunction, Function<Publisher<?>, Object> fromPublisherFunction) {
      super(descriptor, toPublisherFunction, fromPublisherFunction);
    }

    
    public <T> Publisher<T> toPublisher(@Nullable Object source) {
      Publisher<T> publisher = super.toPublisher(source);
      return isMultiValue() ? (Publisher<T>)Flux.from(publisher) : (Publisher<T>)Mono.from(publisher);
    }
  }

  
  private static class ReactorRegistrar
  {
    private ReactorRegistrar() {}
    
    void registerAdapters(ReactiveAdapterRegistry registry) {
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleOptionalValue(Mono.class, Mono::empty), source -> (Publisher)source, Mono::from);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Flux.class, Flux::empty), source -> (Publisher)source, Flux::from);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Publisher.class, Flux::empty), source -> (Publisher)source, source -> source);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.nonDeferredAsyncValue(CompletionStage.class, EmptyCompletableFuture::new), source -> Mono.fromCompletionStage((CompletionStage)source), source -> Mono.from(source).toFuture());
    }
  }


  
  private static class EmptyCompletableFuture<T>
    extends CompletableFuture<T>
  {
    EmptyCompletableFuture() {
      complete(null);
    }
  }

  
  private static class ReactorJdkFlowAdapterRegistrar
  {
    private ReactorJdkFlowAdapterRegistrar() {}
    
    void registerAdapter(ReactiveAdapterRegistry registry) {
      try {
        String publisherName = "java.util.concurrent.Flow.Publisher";
        Class<?> publisherClass = ClassUtils.forName(publisherName, getClass().getClassLoader());
        
        String adapterName = "reactor.adapter.JdkFlowAdapter";
        Class<?> flowAdapterClass = ClassUtils.forName(adapterName, getClass().getClassLoader());
        
        Method toFluxMethod = flowAdapterClass.getMethod("flowPublisherToFlux", new Class[] { publisherClass });
        Method toFlowMethod = flowAdapterClass.getMethod("publisherToFlowPublisher", new Class[] { Publisher.class });
        Object emptyFlow = ReflectionUtils.invokeMethod(toFlowMethod, null, new Object[] { Flux.empty() });
        
        registry.registerReactiveType(
            ReactiveTypeDescriptor.multiValue(publisherClass, () -> emptyFlow), source -> (Publisher)ReflectionUtils.invokeMethod(toFluxMethod, null, new Object[] { source }), publisher -> ReflectionUtils.invokeMethod(toFlowMethod, null, new Object[] { publisher }));

      
      }
      catch (Throwable throwable) {}
    }
  }

  
  private static class RxJava1Registrar
  {
    private RxJava1Registrar() {}
    
    void registerAdapters(ReactiveAdapterRegistry registry) {
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source -> RxReactiveStreams.toPublisher((Observable)source), RxReactiveStreams::toObservable);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleRequiredValue(Single.class), source -> RxReactiveStreams.toPublisher((Single)source), RxReactiveStreams::toSingle);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source -> RxReactiveStreams.toPublisher((Completable)source), RxReactiveStreams::toCompletable);
    }
  }

  
  private static class RxJava2Registrar
  {
    private RxJava2Registrar() {}
    
    void registerAdapters(ReactiveAdapterRegistry registry) {
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Flowable.class, Flowable::empty), source -> (Publisher)source, Flowable::fromPublisher);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source -> ((Observable)source).toFlowable(BackpressureStrategy.BUFFER), Observable::fromPublisher);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleRequiredValue(Single.class), source -> ((Single)source).toFlowable(), Single::fromPublisher);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty), source -> ((Maybe)source).toFlowable(), source -> Flowable.fromPublisher(source).toObservable().singleElement());



      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source -> ((Completable)source).toFlowable(), Completable::fromPublisher);
    }
  }

  
  private static class RxJava3Registrar
  {
    private RxJava3Registrar() {}
    
    void registerAdapters(ReactiveAdapterRegistry registry) {
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Flowable.class, Flowable::empty), source -> (Publisher)source, Flowable::fromPublisher);




      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source -> ((Observable)source).toFlowable(BackpressureStrategy.BUFFER), Observable::fromPublisher);





      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleRequiredValue(Single.class), source -> ((Single)source).toFlowable(), Single::fromPublisher);


      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty), source -> ((Maybe)source).toFlowable(), Maybe::fromPublisher);




      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source -> ((Completable)source).toFlowable(), Completable::fromPublisher);
    }
  }


  
  private static class CoroutinesRegistrar
  {
    private CoroutinesRegistrar() {}


    
    void registerAdapters(ReactiveAdapterRegistry registry) {
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleOptionalValue(Deferred.class, () -> CompletableDeferredKt.CompletableDeferred(null)), source -> CoroutinesUtils.deferredToMono((Deferred)source), source -> CoroutinesUtils.monoToDeferred(Mono.from(source)));



      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Flow.class, FlowKt::emptyFlow), source -> ReactorFlowKt.asFlux((Flow)source), ReactiveFlowKt::asFlow);
    }
  }

  
  private static class MutinyRegistrar
  {
    private MutinyRegistrar() {}
    
    void registerAdapters(ReactiveAdapterRegistry registry) {
      registry.registerReactiveType(
          ReactiveTypeDescriptor.singleOptionalValue(Uni.class, () -> Uni.createFrom().nothing()), uni -> ((Uni)uni).convert().toPublisher(), publisher -> Uni.createFrom().publisher(publisher));




      
      registry.registerReactiveType(
          ReactiveTypeDescriptor.multiValue(Multi.class, () -> Multi.createFrom().empty()), multi -> (Publisher)multi, publisher -> Multi.createFrom().publisher(publisher));
    }
  }
















  
  public static class SpringCoreBlockHoundIntegration
    implements BlockHoundIntegration
  {
    public void applyTo(BlockHound.Builder builder) {
      builder.allowBlockingCallsInside("org.springframework.core.LocalVariableTableParameterNameDiscoverer", "inspectClass");

      
      String className = "org.springframework.util.ConcurrentReferenceHashMap$Segment";
      builder.allowBlockingCallsInside(className, "doTask");
      builder.allowBlockingCallsInside(className, "clear");
      builder.allowBlockingCallsInside(className, "restructure");
    }
  }
}
