package org.bouncycastle.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionStore<T> implements Store<T>, Iterable<T> {
  private Collection<T> _local;
  
  public CollectionStore(Collection<T> paramCollection) {
    this._local = new ArrayList<T>(paramCollection);
  }
  
  public Collection<T> getMatches(Selector<T> paramSelector) {
    // Byte code:
    //   0: aload_1
    //   1: ifnonnull -> 16
    //   4: new java/util/ArrayList
    //   7: dup
    //   8: aload_0
    //   9: getfield _local : Ljava/util/Collection;
    //   12: invokespecial <init> : (Ljava/util/Collection;)V
    //   15: areturn
    //   16: new java/util/ArrayList
    //   19: dup
    //   20: invokespecial <init> : ()V
    //   23: astore_2
    //   24: aload_0
    //   25: getfield _local : Ljava/util/Collection;
    //   28: invokeinterface iterator : ()Ljava/util/Iterator;
    //   33: astore_3
    //   34: aload_3
    //   35: invokeinterface hasNext : ()Z
    //   40: ifeq -> 74
    //   43: aload_3
    //   44: invokeinterface next : ()Ljava/lang/Object;
    //   49: astore #4
    //   51: aload_1
    //   52: aload #4
    //   54: invokeinterface match : (Ljava/lang/Object;)Z
    //   59: ifeq -> 71
    //   62: aload_2
    //   63: aload #4
    //   65: invokeinterface add : (Ljava/lang/Object;)Z
    //   70: pop
    //   71: goto -> 34
    //   74: aload_2
    //   75: areturn
  }
  
  public Iterator<T> iterator() {
    return getMatches(null).iterator();
  }
}
