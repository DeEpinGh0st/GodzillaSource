package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.internal.InternalFutureFailureAccess;
import com.google.common.util.concurrent.internal.InternalFutures;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.j2objc.annotations.ReflectionSupport;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;

































@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
public abstract class AbstractFuture<V>
  extends InternalFutureFailureAccess
  implements ListenableFuture<V>
{
  static {
    AtomicHelper helper;
  }
  
  private static final boolean GENERATE_CANCELLATION_CAUSES = Boolean.parseBoolean(
      System.getProperty("guava.concurrent.generate_cancellation_cause", "false"));



  
  static interface Trusted<V>
    extends ListenableFuture<V> {}



  
  static abstract class TrustedFuture<V>
    extends AbstractFuture<V>
    implements Trusted<V>
  {
    @CanIgnoreReturnValue
    public final V get() throws InterruptedException, ExecutionException {
      return super.get();
    }


    
    @CanIgnoreReturnValue
    public final V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return super.get(timeout, unit);
    }

    
    public final boolean isDone() {
      return super.isDone();
    }

    
    public final boolean isCancelled() {
      return super.isCancelled();
    }

    
    public final void addListener(Runnable listener, Executor executor) {
      super.addListener(listener, executor);
    }

    
    @CanIgnoreReturnValue
    public final boolean cancel(boolean mayInterruptIfRunning) {
      return super.cancel(mayInterruptIfRunning);
    }
  }

  
  private static final Logger log = Logger.getLogger(AbstractFuture.class.getName());

  
  private static final long SPIN_THRESHOLD_NANOS = 1000L;

  
  private static final AtomicHelper ATOMIC_HELPER;

  
  static {
    Throwable thrownUnsafeFailure = null;
    Throwable thrownAtomicReferenceFieldUpdaterFailure = null;
    
    try {
      helper = new UnsafeAtomicHelper();
    } catch (Throwable unsafeFailure) {
      thrownUnsafeFailure = unsafeFailure;








      
      try {
        helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Thread.class, "thread"), AtomicReferenceFieldUpdater.newUpdater(Waiter.class, Waiter.class, "next"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Waiter.class, "waiters"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Listener.class, "listeners"), AtomicReferenceFieldUpdater.newUpdater(AbstractFuture.class, Object.class, "value"));
      } catch (Throwable atomicReferenceFieldUpdaterFailure) {



        
        thrownAtomicReferenceFieldUpdaterFailure = atomicReferenceFieldUpdaterFailure;
        helper = new SynchronizedHelper();
      } 
    } 
    ATOMIC_HELPER = helper;



    
    Class<?> ensureLoaded = LockSupport.class;


    
    if (thrownAtomicReferenceFieldUpdaterFailure != null) {
      log.log(Level.SEVERE, "UnsafeAtomicHelper is broken!", thrownUnsafeFailure);
      log.log(Level.SEVERE, "SafeAtomicHelper is broken!", thrownAtomicReferenceFieldUpdaterFailure);
    } 
  }

  
  private static final class Waiter
  {
    static final Waiter TOMBSTONE = new Waiter(false);

    
    volatile Thread thread;

    
    volatile Waiter next;

    
    Waiter(boolean unused) {}

    
    Waiter() {
      AbstractFuture.ATOMIC_HELPER.putThread(this, Thread.currentThread());
    }


    
    void setNext(Waiter next) {
      AbstractFuture.ATOMIC_HELPER.putNext(this, next);
    }



    
    void unpark() {
      Thread w = this.thread;
      if (w != null) {
        this.thread = null;
        LockSupport.unpark(w);
      } 
    }
  }











  
  private void removeWaiter(Waiter node) {
    node.thread = null;
    
    label22: while (true) {
      Waiter pred = null;
      Waiter curr = this.waiters;
      if (curr == Waiter.TOMBSTONE) {
        return;
      }
      
      while (curr != null) {
        Waiter succ = curr.next;
        if (curr.thread != null) {
          pred = curr;
        } else if (pred != null) {
          pred.next = succ;
          if (pred.thread == null) {
            continue label22;
          }
        } else if (!ATOMIC_HELPER.casWaiters(this, curr, succ)) {
          continue label22;
        } 
        curr = succ;
      } 
      break;
    } 
  }
  
  private static final class Listener
  {
    static final Listener TOMBSTONE = new Listener(null, null);
    
    final Runnable task;
    
    final Executor executor;
    Listener next;
    
    Listener(Runnable task, Executor executor) {
      this.task = task;
      this.executor = executor;
    }
  }
  private volatile Object value;
  private volatile Listener listeners;
  private static final Object NULL = new Object();
  private volatile Waiter waiters;
  
  private static final class Failure {
    static final Failure FALLBACK_INSTANCE = new Failure(new Throwable("Failure occurred while trying to finish a future.")
        {
          
          public synchronized Throwable fillInStackTrace()
          {
            return this;
          }
        });

    
    Failure(Throwable exception) {
      this.exception = (Throwable)Preconditions.checkNotNull(exception);
    }
    
    final Throwable exception; }
  
  private static final class Cancellation { static final Cancellation CAUSELESS_INTERRUPTED;
    static final Cancellation CAUSELESS_CANCELLED;
    final boolean wasInterrupted;
    final Throwable cause;
    
    static {
      if (AbstractFuture.GENERATE_CANCELLATION_CAUSES) {
        CAUSELESS_CANCELLED = null;
        CAUSELESS_INTERRUPTED = null;
      } else {
        CAUSELESS_CANCELLED = new Cancellation(false, null);
        CAUSELESS_INTERRUPTED = new Cancellation(true, null);
      } 
    }



    
    Cancellation(boolean wasInterrupted, Throwable cause) {
      this.wasInterrupted = wasInterrupted;
      this.cause = cause;
    } }

  
  private static final class SetFuture<V>
    implements Runnable {
    final AbstractFuture<V> owner;
    final ListenableFuture<? extends V> future;
    
    SetFuture(AbstractFuture<V> owner, ListenableFuture<? extends V> future) {
      this.owner = owner;
      this.future = future;
    }

    
    public void run() {
      if (this.owner.value != this) {
        return;
      }
      
      Object valueToSet = AbstractFuture.getFutureValue(this.future);
      if (AbstractFuture.ATOMIC_HELPER.casValue(this.owner, this, valueToSet)) {
        AbstractFuture.complete(this.owner);
      }
    }
  }
































































  
  @CanIgnoreReturnValue
  public V get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
    long timeoutNanos = unit.toNanos(timeout);
    long remainingNanos = timeoutNanos;
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Object localValue = this.value;
    if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0) {
      return getDoneValue(localValue);
    }
    
    long endNanos = (remainingNanos > 0L) ? (System.nanoTime() + remainingNanos) : 0L;
    
    if (remainingNanos >= 1000L) {
      Waiter oldHead = this.waiters;
      if (oldHead != Waiter.TOMBSTONE) {
        Waiter node = new Waiter();
        label77: while (true) {
          node.setNext(oldHead);
          if (ATOMIC_HELPER.casWaiters(this, oldHead, node)) {
            do {
              LockSupport.parkNanos(this, remainingNanos);
              
              if (Thread.interrupted()) {
                removeWaiter(node);
                throw new InterruptedException();
              } 


              
              localValue = this.value;
              if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0) {
                return getDoneValue(localValue);
              }

              
              remainingNanos = endNanos - System.nanoTime();
            } while (remainingNanos >= 1000L);
            
            removeWaiter(node);
            
            break;
          } 
          
          oldHead = this.waiters;
          if (oldHead == Waiter.TOMBSTONE)
            break label77; 
        } 
      } else {
        return getDoneValue(this.value);
      } 
    } 
    
    while (remainingNanos > 0L) {
      localValue = this.value;
      if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0) {
        return getDoneValue(localValue);
      }
      if (Thread.interrupted()) {
        throw new InterruptedException();
      }
      remainingNanos = endNanos - System.nanoTime();
    } 
    
    String futureToString = toString();
    String unitString = unit.toString().toLowerCase(Locale.ROOT);
    String message = "Waited " + timeout + " " + unit.toString().toLowerCase(Locale.ROOT);
    
    if (remainingNanos + 1000L < 0L) {
      
      message = message + " (plus ";
      long overWaitNanos = -remainingNanos;
      long overWaitUnits = unit.convert(overWaitNanos, TimeUnit.NANOSECONDS);
      long overWaitLeftoverNanos = overWaitNanos - unit.toNanos(overWaitUnits);
      boolean shouldShowExtraNanos = (overWaitUnits == 0L || overWaitLeftoverNanos > 1000L);
      
      if (overWaitUnits > 0L) {
        message = message + overWaitUnits + " " + unitString;
        if (shouldShowExtraNanos) {
          message = message + ",";
        }
        message = message + " ";
      } 
      if (shouldShowExtraNanos) {
        message = message + overWaitLeftoverNanos + " nanoseconds ";
      }
      
      message = message + "delay)";
    } 


    
    if (isDone()) {
      throw new TimeoutException(message + " but future completed as timeout expired");
    }
    throw new TimeoutException(message + " for " + futureToString);
  }









  
  @CanIgnoreReturnValue
  public V get() throws InterruptedException, ExecutionException {
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
    Object localValue = this.value;
    if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0) {
      return getDoneValue(localValue);
    }
    Waiter oldHead = this.waiters;
    if (oldHead != Waiter.TOMBSTONE) {
      Waiter node = new Waiter();
      do {
        node.setNext(oldHead);
        if (ATOMIC_HELPER.casWaiters(this, oldHead, node)) {
          while (true) {
            
            LockSupport.park(this);
            
            if (Thread.interrupted()) {
              removeWaiter(node);
              throw new InterruptedException();
            } 

            
            localValue = this.value;
            if ((((localValue != null) ? 1 : 0) & (!(localValue instanceof SetFuture) ? 1 : 0)) != 0) {
              return getDoneValue(localValue);
            }
          } 
        }
        oldHead = this.waiters;
      } while (oldHead != Waiter.TOMBSTONE);
    } 

    
    return getDoneValue(this.value);
  }



  
  private V getDoneValue(Object obj) throws ExecutionException {
    if (obj instanceof Cancellation)
      throw cancellationExceptionWithCause("Task was cancelled.", ((Cancellation)obj).cause); 
    if (obj instanceof Failure)
      throw new ExecutionException(((Failure)obj).exception); 
    if (obj == NULL) {
      return null;
    }
    
    V asV = (V)obj;
    return asV;
  }


  
  public boolean isDone() {
    Object localValue = this.value;
    return ((localValue != null)) & (!(localValue instanceof SetFuture));
  }

  
  public boolean isCancelled() {
    Object localValue = this.value;
    return localValue instanceof Cancellation;
  }














  
  @CanIgnoreReturnValue
  public boolean cancel(boolean mayInterruptIfRunning) {
    Object localValue = this.value;
    boolean rValue = false;
    if ((((localValue == null) ? 1 : 0) | localValue instanceof SetFuture) != 0) {

      
      Object valueToSet = GENERATE_CANCELLATION_CAUSES ? new Cancellation(mayInterruptIfRunning, new CancellationException("Future.cancel() was called.")) : (mayInterruptIfRunning ? Cancellation.CAUSELESS_INTERRUPTED : Cancellation.CAUSELESS_CANCELLED);





      
      AbstractFuture<?> abstractFuture = this;
      do {
        while (ATOMIC_HELPER.casValue(abstractFuture, localValue, valueToSet)) {
          rValue = true;

          
          if (mayInterruptIfRunning) {
            abstractFuture.interruptTask();
          }
          complete(abstractFuture);
          if (localValue instanceof SetFuture) {

            
            ListenableFuture<?> futureToPropagateTo = ((SetFuture)localValue).future;
            if (futureToPropagateTo instanceof Trusted) {






              
              AbstractFuture<?> trusted = (AbstractFuture)futureToPropagateTo;
              localValue = trusted.value;
              if ((((localValue == null) ? 1 : 0) | localValue instanceof SetFuture) != 0) {
                abstractFuture = trusted;
                continue;
              } 
              // Byte code: goto -> 190
            } 
            futureToPropagateTo.cancel(mayInterruptIfRunning);
            
            break;
          } 
          // Byte code: goto -> 190
        } 
        localValue = abstractFuture.value;
      } while (localValue instanceof SetFuture);
    } 





    
    return rValue;
  }









  
  protected void interruptTask() {}








  
  protected final boolean wasInterrupted() {
    Object localValue = this.value;
    return (localValue instanceof Cancellation && ((Cancellation)localValue).wasInterrupted);
  }






  
  public void addListener(Runnable listener, Executor executor) {
    Preconditions.checkNotNull(listener, "Runnable was null.");
    Preconditions.checkNotNull(executor, "Executor was null.");








    
    if (!isDone()) {
      Listener oldHead = this.listeners;
      if (oldHead != Listener.TOMBSTONE) {
        Listener newNode = new Listener(listener, executor);
        do {
          newNode.next = oldHead;
          if (ATOMIC_HELPER.casListeners(this, oldHead, newNode)) {
            return;
          }
          oldHead = this.listeners;
        } while (oldHead != Listener.TOMBSTONE);
      } 
    } 

    
    executeListener(listener, executor);
  }












  
  @CanIgnoreReturnValue
  protected boolean set(V value) {
    Object valueToSet = (value == null) ? NULL : value;
    if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
      complete(this);
      return true;
    } 
    return false;
  }












  
  @CanIgnoreReturnValue
  protected boolean setException(Throwable throwable) {
    Object valueToSet = new Failure((Throwable)Preconditions.checkNotNull(throwable));
    if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {
      complete(this);
      return true;
    } 
    return false;
  }


























  
  @Beta
  @CanIgnoreReturnValue
  protected boolean setFuture(ListenableFuture<? extends V> future) {
    Preconditions.checkNotNull(future);
    Object localValue = this.value;
    if (localValue == null) {
      if (future.isDone()) {
        Object value = getFutureValue(future);
        if (ATOMIC_HELPER.casValue(this, null, value)) {
          complete(this);
          return true;
        } 
        return false;
      } 
      SetFuture<V> valueToSet = new SetFuture<>(this, future);
      if (ATOMIC_HELPER.casValue(this, null, valueToSet)) {

        
        try {
          future.addListener(valueToSet, DirectExecutor.INSTANCE);
        } catch (Throwable t) {
          Failure failure;


          
          try {
            failure = new Failure(t);
          } catch (Throwable oomMostLikely) {
            failure = Failure.FALLBACK_INSTANCE;
          } 
          
          boolean bool = ATOMIC_HELPER.casValue(this, valueToSet, failure);
        } 
        return true;
      } 
      localValue = this.value;
    } 

    
    if (localValue instanceof Cancellation)
    {
      future.cancel(((Cancellation)localValue).wasInterrupted);
    }
    return false;
  }






  
  private static Object getFutureValue(ListenableFuture<?> future) {
    if (future instanceof Trusted) {



      
      Object v = ((AbstractFuture)future).value;
      if (v instanceof Cancellation) {


        
        Cancellation c = (Cancellation)v;
        if (c.wasInterrupted) {
          v = (c.cause != null) ? new Cancellation(false, c.cause) : Cancellation.CAUSELESS_CANCELLED;
        }
      } 


      
      return v;
    } 
    if (future instanceof InternalFutureFailureAccess) {
      
      Throwable throwable = InternalFutures.tryInternalFastPathGetFailure((InternalFutureFailureAccess)future);
      if (throwable != null) {
        return new Failure(throwable);
      }
    } 
    boolean wasCancelled = future.isCancelled();
    
    if (((!GENERATE_CANCELLATION_CAUSES ? 1 : 0) & wasCancelled) != 0) {
      return Cancellation.CAUSELESS_CANCELLED;
    }
    
    try {
      Object v = getUninterruptibly(future);
      if (wasCancelled) {
        return new Cancellation(false, new IllegalArgumentException("get() did not throw CancellationException, despite reporting isCancelled() == true: " + future));
      }




      
      return (v == null) ? NULL : v;
    } catch (ExecutionException exception) {
      if (wasCancelled) {
        return new Cancellation(false, new IllegalArgumentException("get() did not throw CancellationException, despite reporting isCancelled() == true: " + future, exception));
      }





      
      return new Failure(exception.getCause());
    } catch (CancellationException cancellation) {
      if (!wasCancelled) {
        return new Failure(new IllegalArgumentException("get() threw CancellationException, despite reporting isCancelled() == false: " + future, cancellation));
      }



      
      return new Cancellation(false, cancellation);
    } catch (Throwable t) {
      return new Failure(t);
    } 
  }




  
  private static <V> V getUninterruptibly(Future<V> future) throws ExecutionException {
    boolean interrupted = false;
    
    while (true) {
      try {
        return future.get();
      } catch (InterruptedException e) {

      
      } finally {
        
        if (interrupted) {
          Thread.currentThread().interrupt();
        }
      } 
    } 
  }
  
  private static void complete(AbstractFuture<?> future) {
    Listener next = null;
    
    label17: while (true) {
      future.releaseWaiters();




      
      future.afterDone();
      
      next = future.clearListeners(next);
      future = null;
      while (next != null) {
        Listener curr = next;
        next = next.next;
        Runnable task = curr.task;
        if (task instanceof SetFuture) {
          SetFuture<?> setFuture = (SetFuture)task;




          
          future = setFuture.owner;
          if (future.value == setFuture) {
            Object valueToSet = getFutureValue(setFuture.future);
            if (ATOMIC_HELPER.casValue(future, setFuture, valueToSet)) {
              continue label17;
            }
          } 
          continue;
        } 
        executeListener(task, curr.executor);
      } 
      break;
    } 
  }


















  
  @Beta
  @ForOverride
  protected void afterDone() {}

















  
  protected final Throwable tryInternalFastPathGetFailure() {
    if (this instanceof Trusted) {
      Object obj = this.value;
      if (obj instanceof Failure) {
        return ((Failure)obj).exception;
      }
    } 
    return null;
  }




  
  final void maybePropagateCancellationTo(Future<?> related) {
    if ((((related != null) ? 1 : 0) & isCancelled()) != 0) {
      related.cancel(wasInterrupted());
    }
  }


  
  private void releaseWaiters() {
    while (true) {
      Waiter head = this.waiters;
      if (ATOMIC_HELPER.casWaiters(this, head, Waiter.TOMBSTONE)) {
        for (Waiter currentWaiter = head; currentWaiter != null; currentWaiter = currentWaiter.next) {
          currentWaiter.unpark();
        }
        return;
      } 
    } 
  }








  
  private Listener clearListeners(Listener onto) {
    while (true) {
      Listener head = this.listeners;
      if (ATOMIC_HELPER.casListeners(this, head, Listener.TOMBSTONE)) {
        Listener reversedList = onto;
        while (head != null) {
          Listener tmp = head;
          head = head.next;
          tmp.next = reversedList;
          reversedList = tmp;
        } 
        return reversedList;
      } 
    } 
  }
  
  public String toString() {
    StringBuilder builder = (new StringBuilder()).append(super.toString()).append("[status=");
    if (isCancelled()) {
      builder.append("CANCELLED");
    } else if (isDone()) {
      addDoneString(builder);
    } else {
      String pendingDescription;
      try {
        pendingDescription = pendingToString();
      } catch (RuntimeException e) {

        
        pendingDescription = "Exception thrown from implementation: " + e.getClass();
      } 

      
      if (pendingDescription != null && !pendingDescription.isEmpty()) {
        builder.append("PENDING, info=[").append(pendingDescription).append("]");
      } else if (isDone()) {
        addDoneString(builder);
      } else {
        builder.append("PENDING");
      } 
    } 
    return builder.append("]").toString();
  }






  
  protected String pendingToString() {
    Object localValue = this.value;
    if (localValue instanceof SetFuture)
      return "setFuture=[" + userObjectToString(((SetFuture)localValue).future) + "]"; 
    if (this instanceof ScheduledFuture) {
      return "remaining delay=[" + ((ScheduledFuture)this)
        .getDelay(TimeUnit.MILLISECONDS) + " ms]";
    }
    
    return null;
  }
  
  private void addDoneString(StringBuilder builder) {
    try {
      V value = getUninterruptibly(this);
      builder.append("SUCCESS, result=[").append(userObjectToString(value)).append("]");
    } catch (ExecutionException e) {
      builder.append("FAILURE, cause=[").append(e.getCause()).append("]");
    } catch (CancellationException e) {
      builder.append("CANCELLED");
    } catch (RuntimeException e) {
      builder.append("UNKNOWN, cause=[").append(e.getClass()).append(" thrown from get()]");
    } 
  }





  
  private String userObjectToString(Object o) {
    if (o == this) {
      return "this future";
    }
    return String.valueOf(o);
  }




  
  private static void executeListener(Runnable runnable, Executor executor) {
    try {
      executor.execute(runnable);
    } catch (RuntimeException e) {


      
      log.log(Level.SEVERE, "RuntimeException while executing runnable " + runnable + " with executor " + executor, e);
    } 
  }

  
  private static abstract class AtomicHelper
  {
    private AtomicHelper() {}

    
    abstract void putThread(AbstractFuture.Waiter param1Waiter, Thread param1Thread);

    
    abstract void putNext(AbstractFuture.Waiter param1Waiter1, AbstractFuture.Waiter param1Waiter2);

    
    abstract boolean casWaiters(AbstractFuture<?> param1AbstractFuture, AbstractFuture.Waiter param1Waiter1, AbstractFuture.Waiter param1Waiter2);

    
    abstract boolean casListeners(AbstractFuture<?> param1AbstractFuture, AbstractFuture.Listener param1Listener1, AbstractFuture.Listener param1Listener2);

    
    abstract boolean casValue(AbstractFuture<?> param1AbstractFuture, Object param1Object1, Object param1Object2);
  }

  
  private static final class UnsafeAtomicHelper
    extends AtomicHelper
  {
    static final Unsafe UNSAFE;
    static final long LISTENERS_OFFSET;
    static final long WAITERS_OFFSET;
    static final long VALUE_OFFSET;
    static final long WAITER_THREAD_OFFSET;
    static final long WAITER_NEXT_OFFSET;
    
    private UnsafeAtomicHelper() {}
    
    static {
      Unsafe unsafe = null;
      try {
        unsafe = Unsafe.getUnsafe();
      } catch (SecurityException tryReflectionInstead) {
        
        try {
          unsafe = AccessController.<Unsafe>doPrivileged(new PrivilegedExceptionAction<Unsafe>()
              {
                public Unsafe run() throws Exception
                {
                  Class<Unsafe> k = Unsafe.class;
                  for (Field f : k.getDeclaredFields()) {
                    f.setAccessible(true);
                    Object x = f.get((Object)null);
                    if (k.isInstance(x)) {
                      return k.cast(x);
                    }
                  } 
                  throw new NoSuchFieldError("the Unsafe");
                }
              });
        } catch (PrivilegedActionException e) {
          throw new RuntimeException("Could not initialize intrinsics", e.getCause());
        } 
      } 
      try {
        Class<?> abstractFuture = AbstractFuture.class;
        WAITERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("waiters"));
        LISTENERS_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("listeners"));
        VALUE_OFFSET = unsafe.objectFieldOffset(abstractFuture.getDeclaredField("value"));
        WAITER_THREAD_OFFSET = unsafe.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("thread"));
        WAITER_NEXT_OFFSET = unsafe.objectFieldOffset(AbstractFuture.Waiter.class.getDeclaredField("next"));
        UNSAFE = unsafe;
      } catch (Exception e) {
        Throwables.throwIfUnchecked(e);
        throw new RuntimeException(e);
      } 
    }

    
    void putThread(AbstractFuture.Waiter waiter, Thread newValue) {
      UNSAFE.putObject(waiter, WAITER_THREAD_OFFSET, newValue);
    }

    
    void putNext(AbstractFuture.Waiter waiter, AbstractFuture.Waiter newValue) {
      UNSAFE.putObject(waiter, WAITER_NEXT_OFFSET, newValue);
    }


    
    boolean casWaiters(AbstractFuture<?> future, AbstractFuture.Waiter expect, AbstractFuture.Waiter update) {
      return UNSAFE.compareAndSwapObject(future, WAITERS_OFFSET, expect, update);
    }


    
    boolean casListeners(AbstractFuture<?> future, AbstractFuture.Listener expect, AbstractFuture.Listener update) {
      return UNSAFE.compareAndSwapObject(future, LISTENERS_OFFSET, expect, update);
    }


    
    boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
      return UNSAFE.compareAndSwapObject(future, VALUE_OFFSET, expect, update);
    }
  }

  
  private static final class SafeAtomicHelper
    extends AtomicHelper
  {
    final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> waiterThreadUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> waiterNextUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> waitersUpdater;
    
    final AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> listenersUpdater;
    final AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater;
    
    SafeAtomicHelper(AtomicReferenceFieldUpdater<AbstractFuture.Waiter, Thread> waiterThreadUpdater, AtomicReferenceFieldUpdater<AbstractFuture.Waiter, AbstractFuture.Waiter> waiterNextUpdater, AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Waiter> waitersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, AbstractFuture.Listener> listenersUpdater, AtomicReferenceFieldUpdater<AbstractFuture, Object> valueUpdater) {
      this.waiterThreadUpdater = waiterThreadUpdater;
      this.waiterNextUpdater = waiterNextUpdater;
      this.waitersUpdater = waitersUpdater;
      this.listenersUpdater = listenersUpdater;
      this.valueUpdater = valueUpdater;
    }

    
    void putThread(AbstractFuture.Waiter waiter, Thread newValue) {
      this.waiterThreadUpdater.lazySet(waiter, newValue);
    }

    
    void putNext(AbstractFuture.Waiter waiter, AbstractFuture.Waiter newValue) {
      this.waiterNextUpdater.lazySet(waiter, newValue);
    }

    
    boolean casWaiters(AbstractFuture<?> future, AbstractFuture.Waiter expect, AbstractFuture.Waiter update) {
      return this.waitersUpdater.compareAndSet(future, expect, update);
    }

    
    boolean casListeners(AbstractFuture<?> future, AbstractFuture.Listener expect, AbstractFuture.Listener update) {
      return this.listenersUpdater.compareAndSet(future, expect, update);
    }

    
    boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
      return this.valueUpdater.compareAndSet(future, expect, update);
    }
  }


  
  private static final class SynchronizedHelper
    extends AtomicHelper
  {
    private SynchronizedHelper() {}

    
    void putThread(AbstractFuture.Waiter waiter, Thread newValue) {
      waiter.thread = newValue;
    }

    
    void putNext(AbstractFuture.Waiter waiter, AbstractFuture.Waiter newValue) {
      waiter.next = newValue;
    }

    
    boolean casWaiters(AbstractFuture<?> future, AbstractFuture.Waiter expect, AbstractFuture.Waiter update) {
      synchronized (future) {
        if (future.waiters == expect) {
          future.waiters = update;
          return true;
        } 
        return false;
      } 
    }

    
    boolean casListeners(AbstractFuture<?> future, AbstractFuture.Listener expect, AbstractFuture.Listener update) {
      synchronized (future) {
        if (future.listeners == expect) {
          future.listeners = update;
          return true;
        } 
        return false;
      } 
    }

    
    boolean casValue(AbstractFuture<?> future, Object expect, Object update) {
      synchronized (future) {
        if (future.value == expect) {
          future.value = update;
          return true;
        } 
        return false;
      } 
    }
  }

  
  private static CancellationException cancellationExceptionWithCause(String message, Throwable cause) {
    CancellationException exception = new CancellationException(message);
    exception.initCause(cause);
    return exception;
  }
}
