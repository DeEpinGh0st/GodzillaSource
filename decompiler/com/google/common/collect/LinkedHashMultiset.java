package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.function.ObjIntConsumer;

























@GwtCompatible(serializable = true, emulated = true)
public final class LinkedHashMultiset<E>
  extends AbstractMapBasedMultiset<E>
{
  @GwtIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <E> LinkedHashMultiset<E> create() {
    return new LinkedHashMultiset<>();
  }







  
  public static <E> LinkedHashMultiset<E> create(int distinctElements) {
    return new LinkedHashMultiset<>(distinctElements);
  }







  
  public static <E> LinkedHashMultiset<E> create(Iterable<? extends E> elements) {
    LinkedHashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
    Iterables.addAll(multiset, elements);
    return multiset;
  }
  
  private LinkedHashMultiset() {
    super(new LinkedHashMap<>());
  }
  
  private LinkedHashMultiset(int distinctElements) {
    super(Maps.newLinkedHashMapWithExpectedSize(distinctElements));
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
    setBackingMap(new LinkedHashMap<>());
    Serialization.populateMultiset(this, stream, distinctElements);
  }
}
