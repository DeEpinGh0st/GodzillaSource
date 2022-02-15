package org.springframework.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.springframework.lang.Nullable;
















































public class AutoPopulatingList<E>
  implements List<E>, Serializable
{
  private final List<E> backingList;
  private final ElementFactory<E> elementFactory;
  
  public AutoPopulatingList(Class<? extends E> elementClass) {
    this(new ArrayList<>(), elementClass);
  }





  
  public AutoPopulatingList(List<E> backingList, Class<? extends E> elementClass) {
    this(backingList, new ReflectiveElementFactory<>(elementClass));
  }




  
  public AutoPopulatingList(ElementFactory<E> elementFactory) {
    this(new ArrayList<>(), elementFactory);
  }




  
  public AutoPopulatingList(List<E> backingList, ElementFactory<E> elementFactory) {
    Assert.notNull(backingList, "Backing List must not be null");
    Assert.notNull(elementFactory, "Element factory must not be null");
    this.backingList = backingList;
    this.elementFactory = elementFactory;
  }


  
  public void add(int index, E element) {
    this.backingList.add(index, element);
  }

  
  public boolean add(E o) {
    return this.backingList.add(o);
  }

  
  public boolean addAll(Collection<? extends E> c) {
    return this.backingList.addAll(c);
  }

  
  public boolean addAll(int index, Collection<? extends E> c) {
    return this.backingList.addAll(index, c);
  }

  
  public void clear() {
    this.backingList.clear();
  }

  
  public boolean contains(Object o) {
    return this.backingList.contains(o);
  }

  
  public boolean containsAll(Collection<?> c) {
    return this.backingList.containsAll(c);
  }





  
  public E get(int index) {
    int backingListSize = this.backingList.size();
    E element = null;
    if (index < backingListSize) {
      element = this.backingList.get(index);
      if (element == null) {
        element = this.elementFactory.createElement(index);
        this.backingList.set(index, element);
      } 
    } else {
      
      for (int x = backingListSize; x < index; x++) {
        this.backingList.add(null);
      }
      element = this.elementFactory.createElement(index);
      this.backingList.add(element);
    } 
    return element;
  }

  
  public int indexOf(Object o) {
    return this.backingList.indexOf(o);
  }

  
  public boolean isEmpty() {
    return this.backingList.isEmpty();
  }

  
  public Iterator<E> iterator() {
    return this.backingList.iterator();
  }

  
  public int lastIndexOf(Object o) {
    return this.backingList.lastIndexOf(o);
  }

  
  public ListIterator<E> listIterator() {
    return this.backingList.listIterator();
  }

  
  public ListIterator<E> listIterator(int index) {
    return this.backingList.listIterator(index);
  }

  
  public E remove(int index) {
    return this.backingList.remove(index);
  }

  
  public boolean remove(Object o) {
    return this.backingList.remove(o);
  }

  
  public boolean removeAll(Collection<?> c) {
    return this.backingList.removeAll(c);
  }

  
  public boolean retainAll(Collection<?> c) {
    return this.backingList.retainAll(c);
  }

  
  public E set(int index, E element) {
    return this.backingList.set(index, element);
  }

  
  public int size() {
    return this.backingList.size();
  }

  
  public List<E> subList(int fromIndex, int toIndex) {
    return this.backingList.subList(fromIndex, toIndex);
  }

  
  public Object[] toArray() {
    return this.backingList.toArray();
  }

  
  public <T> T[] toArray(T[] a) {
    return this.backingList.toArray(a);
  }


  
  public boolean equals(@Nullable Object other) {
    return this.backingList.equals(other);
  }

  
  public int hashCode() {
    return this.backingList.hashCode();
  }








  
  @FunctionalInterface
  public static interface ElementFactory<E>
  {
    E createElement(int param1Int) throws AutoPopulatingList.ElementInstantiationException;
  }








  
  public static class ElementInstantiationException
    extends RuntimeException
  {
    public ElementInstantiationException(String msg) {
      super(msg);
    }
    
    public ElementInstantiationException(String message, Throwable cause) {
      super(message, cause);
    }
  }


  
  private static class ReflectiveElementFactory<E>
    implements ElementFactory<E>, Serializable
  {
    private final Class<? extends E> elementClass;


    
    public ReflectiveElementFactory(Class<? extends E> elementClass) {
      Assert.notNull(elementClass, "Element class must not be null");
      Assert.isTrue(!elementClass.isInterface(), "Element class must not be an interface type");
      Assert.isTrue(!Modifier.isAbstract(elementClass.getModifiers()), "Element class cannot be an abstract class");
      this.elementClass = elementClass;
    }

    
    public E createElement(int index) {
      try {
        return ReflectionUtils.<E>accessibleConstructor((Class)this.elementClass, new Class[0]).newInstance(new Object[0]);
      }
      catch (NoSuchMethodException ex) {
        throw new AutoPopulatingList.ElementInstantiationException("No default constructor on element class: " + this.elementClass
            .getName(), ex);
      }
      catch (InstantiationException ex) {
        throw new AutoPopulatingList.ElementInstantiationException("Unable to instantiate element class: " + this.elementClass
            .getName(), ex);
      }
      catch (IllegalAccessException ex) {
        throw new AutoPopulatingList.ElementInstantiationException("Could not access element constructor: " + this.elementClass
            .getName(), ex);
      }
      catch (InvocationTargetException ex) {
        throw new AutoPopulatingList.ElementInstantiationException("Failed to invoke element constructor: " + this.elementClass
            .getName(), ex.getTargetException());
      } 
    }
  }
}
