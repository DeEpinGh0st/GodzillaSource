package org.springframework.core.metrics.jfr;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;






























public class FlightRecorderApplicationStartup
  implements ApplicationStartup
{
  private final AtomicLong currentSequenceId = new AtomicLong(0L);
  
  private final Deque<Long> currentSteps;

  
  public FlightRecorderApplicationStartup() {
    this.currentSteps = new ConcurrentLinkedDeque<>();
    this.currentSteps.offerFirst(Long.valueOf(this.currentSequenceId.get()));
  }


  
  public StartupStep start(String name) {
    long sequenceId = this.currentSequenceId.incrementAndGet();
    this.currentSteps.offerFirst(Long.valueOf(sequenceId));
    return new FlightRecorderStartupStep(sequenceId, name, ((Long)this.currentSteps
        .getFirst()).longValue(), committedStep -> this.currentSteps.removeFirstOccurrence(Long.valueOf(sequenceId)));
  }
}
