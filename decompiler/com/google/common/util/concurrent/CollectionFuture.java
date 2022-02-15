package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;


















@GwtCompatible(emulated = true)
abstract class CollectionFuture<V, C>
  extends AggregateFuture<V, C>
{
  abstract class CollectionFutureRunningState
    extends AggregateFuture<V, C>.RunningState
  {
    private List<Optional<V>> values;
    
    CollectionFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
      super(futures, allMustSucceed, true);
      
      this

        
        .values = futures.isEmpty() ? (List<Optional<V>>)ImmutableList.of() : Lists.newArrayListWithCapacity(futures.size());

      
      for (int i = 0; i < futures.size(); i++) {
        this.values.add(null);
      }
    }

    
    final void collectOneValue(boolean allMustSucceed, int index, V returnValue) {
      List<Optional<V>> localValues = this.values;
      
      if (localValues != null) {
        localValues.set(index, Optional.fromNullable(returnValue));
      
      }
      else {
        
        Preconditions.checkState((allMustSucceed || CollectionFuture.this
            .isCancelled()), "Future was done before all dependencies completed");
      } 
    }

    
    final void handleAllCompleted() {
      List<Optional<V>> localValues = this.values;
      if (localValues != null) {
        CollectionFuture.this.set(combine(localValues));
      } else {
        Preconditions.checkState(CollectionFuture.this.isDone());
      } 
    }

    
    void releaseResourcesAfterFailure() {
      super.releaseResourcesAfterFailure();
      this.values = null;
    }

    
    abstract C combine(List<Optional<V>> param1List);
  }
  
  static final class ListFuture<V>
    extends CollectionFuture<V, List<V>>
  {
    ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
      init(new ListFutureRunningState(futures, allMustSucceed));
    }
    
    private final class ListFutureRunningState
      extends CollectionFuture<V, List<V>>.CollectionFutureRunningState
    {
      ListFutureRunningState(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
        super(futures, allMustSucceed);
      }

      
      public List<V> combine(List<Optional<V>> values) {
        List<V> result = Lists.newArrayListWithCapacity(values.size());
        for (Optional<V> element : values) {
          result.add((element != null) ? (V)element.orNull() : null);
        }
        return Collections.unmodifiableList(result);
      }
    }
  }
}
