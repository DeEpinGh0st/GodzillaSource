package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ObjIntConsumer;

















@GwtCompatible(serializable = true, emulated = true)
public final class HashMultiset<E>
  extends AbstractMapBasedMultiset<E>
{
  @GwtIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <E> HashMultiset<E> create() {
    return new HashMultiset<>();
  }







  
  public static <E> HashMultiset<E> create(int distinctElements) {
    return new HashMultiset<>(distinctElements);
  }







  
  public static <E> HashMultiset<E> create(Iterable<? extends E> elements) {
    HashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
    Iterables.addAll(multiset, elements);
    return multiset;
  }
  
  private HashMultiset() {
    super(new HashMap<>());
  }
  
  private HashMultiset(int distinctElements) {
    super(Maps.newHashMapWithExpectedSize(distinctElements));
  }




  
  @GwtIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMultiset(this, stream);
  }
  
  @GwtIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int distinctElements = Serialization.readCount(stream);
    setBackingMap(Maps.newHashMap());
    Serialization.populateMultiset(this, stream, distinctElements);
  }
}
