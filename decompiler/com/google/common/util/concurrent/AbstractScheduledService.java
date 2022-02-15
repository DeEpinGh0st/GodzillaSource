package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;












































































@Beta
@GwtIncompatible
public abstract class AbstractScheduledService
  implements Service
{
  private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());






















  
  public static abstract class Scheduler
  {
    public static Scheduler newFixedDelaySchedule(final long initialDelay, final long delay, final TimeUnit unit) {
      Preconditions.checkNotNull(unit);
      Preconditions.checkArgument((delay > 0L), "delay must be > 0, found %s", delay);
      return new Scheduler()
        {
          public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
          {
            return executor.scheduleWithFixedDelay(task, initialDelay, delay, unit);
          }
        };
    }










    
    public static Scheduler newFixedRateSchedule(final long initialDelay, final long period, final TimeUnit unit) {
      Preconditions.checkNotNull(unit);
      Preconditions.checkArgument((period > 0L), "period must be > 0, found %s", period);
      return new Scheduler()
        {
          public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
          {
            return executor.scheduleAtFixedRate(task, initialDelay, period, unit);
          }
        };
    }

    
    private Scheduler() {}

    
    abstract Future<?> schedule(AbstractService param1AbstractService, ScheduledExecutorService param1ScheduledExecutorService, Runnable param1Runnable);
  }

  
  private final AbstractService delegate = new ServiceDelegate();



  
  private final class ServiceDelegate
    extends AbstractService
  {
    private volatile Future<?> runningTask;

    
    private volatile ScheduledExecutorService executorService;

    
    private final ReentrantLock lock = new ReentrantLock();
    
    class Task
      implements Runnable
    {
      public void run() {
        AbstractScheduledService.ServiceDelegate.this.lock.lock();
        try {
          if (AbstractScheduledService.ServiceDelegate.this.runningTask.isCancelled()) {
            return;
          }
          
          AbstractScheduledService.this.runOneIteration();
        } catch (Throwable t) {
          try {
            AbstractScheduledService.this.shutDown();
          } catch (Exception ignored) {
            AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
          } 


          
          AbstractScheduledService.ServiceDelegate.this.notifyFailed(t);
          AbstractScheduledService.ServiceDelegate.this.runningTask.cancel(false);
        } finally {
          AbstractScheduledService.ServiceDelegate.this.lock.unlock();
        } 
      }
    }
    
    private final Runnable task = new Task();

    
    protected final void doStart() {
      this
        .executorService = MoreExecutors.renamingDecorator(AbstractScheduledService.this
          .executor(), new Supplier<String>()
          {
            public String get()
            {
              return AbstractScheduledService.this.serviceName() + " " + AbstractScheduledService.ServiceDelegate.this.state();
            }
          });
      this.executorService.execute(new Runnable()
          {
            public void run()
            {
              AbstractScheduledService.ServiceDelegate.this.lock.lock();
              try {
                AbstractScheduledService.this.startUp();
                AbstractScheduledService.ServiceDelegate.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, AbstractScheduledService.ServiceDelegate.this.executorService, AbstractScheduledService.ServiceDelegate.this.task);
                AbstractScheduledService.ServiceDelegate.this.notifyStarted();
              } catch (Throwable t) {
                AbstractScheduledService.ServiceDelegate.this.notifyFailed(t);
                if (AbstractScheduledService.ServiceDelegate.this.runningTask != null)
                {
                  AbstractScheduledService.ServiceDelegate.this.runningTask.cancel(false);
                }
              } finally {
                AbstractScheduledService.ServiceDelegate.this.lock.unlock();
              } 
            }
          });
    }

    
    protected final void doStop() {
      this.runningTask.cancel(false);
      this.executorService.execute(new Runnable()
          {
            public void run()
            {
              try {
                AbstractScheduledService.ServiceDelegate.this.lock.lock();
                try {
                  if (AbstractScheduledService.ServiceDelegate.this.state() != Service.State.STOPPING) {
                    return;
                  }



                  
                  AbstractScheduledService.this.shutDown();
                } finally {
                  AbstractScheduledService.ServiceDelegate.this.lock.unlock();
                } 
                AbstractScheduledService.ServiceDelegate.this.notifyStopped();
              } catch (Throwable t) {
                AbstractScheduledService.ServiceDelegate.this.notifyFailed(t);
              } 
            }
          });
    }

    
    public String toString() {
      return AbstractScheduledService.this.toString();
    }










    
    private ServiceDelegate() {}
  }










  
  protected void startUp() throws Exception {}










  
  protected void shutDown() throws Exception {}









  
  protected ScheduledExecutorService executor() {
    class ThreadFactoryImpl
      implements ThreadFactory
    {
      public Thread newThread(Runnable runnable) {
        return MoreExecutors.newThread(AbstractScheduledService.this.serviceName(), runnable);
      }
    };
    
    final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl());




    
    addListener(new Service.Listener()
        {
          public void terminated(Service.State from)
          {
            executor.shutdown();
          }

          
          public void failed(Service.State from, Throwable failure) {
            executor.shutdown();
          }
        }, 
        MoreExecutors.directExecutor());
    return executor;
  }






  
  protected String serviceName() {
    return getClass().getSimpleName();
  }

  
  public String toString() {
    return serviceName() + " [" + state() + "]";
  }

  
  public final boolean isRunning() {
    return this.delegate.isRunning();
  }

  
  public final Service.State state() {
    return this.delegate.state();
  }


  
  public final void addListener(Service.Listener listener, Executor executor) {
    this.delegate.addListener(listener, executor);
  }


  
  public final Throwable failureCause() {
    return this.delegate.failureCause();
  }


  
  @CanIgnoreReturnValue
  public final Service startAsync() {
    this.delegate.startAsync();
    return this;
  }


  
  @CanIgnoreReturnValue
  public final Service stopAsync() {
    this.delegate.stopAsync();
    return this;
  }


  
  public final void awaitRunning() {
    this.delegate.awaitRunning();
  }


  
  public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitRunning(timeout, unit);
  }


  
  public final void awaitTerminated() {
    this.delegate.awaitTerminated();
  }


  
  public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitTerminated(timeout, unit);
  }



  
  protected abstract void runOneIteration() throws Exception;



  
  protected abstract Scheduler scheduler();



  
  @Beta
  public static abstract class CustomScheduler
    extends Scheduler
  {
    private class ReschedulableCallable
      extends ForwardingFuture<Void>
      implements Callable<Void>
    {
      private final Runnable wrappedRunnable;


      
      private final ScheduledExecutorService executor;

      
      private final AbstractService service;

      
      private final ReentrantLock lock = new ReentrantLock();

      
      @GuardedBy("lock")
      private Future<Void> currentFuture;

      
      ReschedulableCallable(AbstractService service, ScheduledExecutorService executor, Runnable runnable) {
        this.wrappedRunnable = runnable;
        this.executor = executor;
        this.service = service;
      }

      
      public Void call() throws Exception {
        this.wrappedRunnable.run();
        reschedule();
        return null;
      }


      
      public void reschedule() {
        AbstractScheduledService.CustomScheduler.Schedule schedule;
        try {
          schedule = AbstractScheduledService.CustomScheduler.this.getNextSchedule();
        } catch (Throwable t) {
          this.service.notifyFailed(t);

          
          return;
        } 

        
        Throwable scheduleFailure = null;
        this.lock.lock();
        try {
          if (this.currentFuture == null || !this.currentFuture.isCancelled()) {
            this.currentFuture = this.executor.schedule(this, schedule.delay, schedule.unit);
          }
        } catch (Throwable e) {







          
          scheduleFailure = e;
        } finally {
          this.lock.unlock();
        } 
        
        if (scheduleFailure != null) {
          this.service.notifyFailed(scheduleFailure);
        }
      }




      
      public boolean cancel(boolean mayInterruptIfRunning) {
        this.lock.lock();
        try {
          return this.currentFuture.cancel(mayInterruptIfRunning);
        } finally {
          this.lock.unlock();
        } 
      }

      
      public boolean isCancelled() {
        this.lock.lock();
        try {
          return this.currentFuture.isCancelled();
        } finally {
          this.lock.unlock();
        } 
      }

      
      protected Future<Void> delegate() {
        throw new UnsupportedOperationException("Only cancel and isCancelled is supported by this future");
      }
    }



    
    final Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable runnable) {
      ReschedulableCallable task = new ReschedulableCallable(service, executor, runnable);
      task.reschedule();
      return task;
    }


    
    protected abstract Schedule getNextSchedule() throws Exception;


    
    @Beta
    protected static final class Schedule
    {
      private final long delay;

      
      private final TimeUnit unit;


      
      public Schedule(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = (TimeUnit)Preconditions.checkNotNull(unit);
      }
    }
  }
}
