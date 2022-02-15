package com.jediterm.terminal;

import com.google.common.base.Predicate;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;

public class TtyConnectorWaitFor
{
  private static final Logger LOG = Logger.getLogger(TtyConnectorWaitFor.class);
  
  private final Future<?> myWaitForThreadFuture;
  private final BlockingQueue<Predicate<Integer>> myTerminationCallback = new ArrayBlockingQueue<>(1);
  
  public void detach() {
    this.myWaitForThreadFuture.cancel(true);
  }

  
  public TtyConnectorWaitFor(final TtyConnector ttyConnector, ExecutorService executor) {
    this.myWaitForThreadFuture = executor.submit(new Runnable()
        {
          public void run() {
            int exitCode = 0;
            try {
              while (true) {
                try {
                  exitCode = ttyConnector.waitFor();
                  
                  break;
                } catch (InterruptedException e) {
                  TtyConnectorWaitFor.LOG.debug(e);
                } 
              } 
            } finally {
              
              try {
                if (!TtyConnectorWaitFor.this.myWaitForThreadFuture.isCancelled()) {
                  ((Predicate)TtyConnectorWaitFor.this.myTerminationCallback.take()).apply(Integer.valueOf(exitCode));
                }
              }
              catch (InterruptedException e) {
                TtyConnectorWaitFor.LOG.info(e);
              } 
            } 
          }
        });
  }
  
  public void setTerminationCallback(Predicate<Integer> r) {
    this.myTerminationCallback.offer(r);
  }
}
