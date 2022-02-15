package com.jgoodies.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


























































public final class LinkedListModel<E>
  extends LinkedList<E>
  implements ObservableList2<E>
{
  private static final long serialVersionUID = 5753378113505707237L;
  private EventListenerList listenerList;
  
  public LinkedListModel() {}
  
  public LinkedListModel(Collection<? extends E> c) {
    super(c);
  }




  
  public final void add(int index, E element) {
    super.add(index, element);
    fireIntervalAdded(index, index);
  }


  
  public final boolean add(E e) {
    int newIndex = size();
    super.add(e);
    fireIntervalAdded(newIndex, newIndex);
    return true;
  }


  
  public final boolean addAll(int index, Collection<? extends E> c) {
    boolean changed = super.addAll(index, c);
    if (changed) {
      int lastIndex = index + c.size() - 1;
      fireIntervalAdded(index, lastIndex);
    } 
    return changed;
  }



























  
  public boolean removeAll(Collection<?> c) {
    boolean modified = false;
    Iterator<?> e = iterator();
    while (e.hasNext()) {
      if (c.contains(e.next())) {
        e.remove();
        modified = true;
      } 
    } 
    return modified;
  }





























  
  public boolean retainAll(Collection<?> c) {
    boolean modified = false;
    Iterator<E> e = iterator();
    while (e.hasNext()) {
      if (!c.contains(e.next())) {
        e.remove();
        modified = true;
      } 
    } 
    return modified;
  }


  
  public final void addFirst(E e) {
    super.addFirst(e);
    fireIntervalAdded(0, 0);
  }


  
  public final void addLast(E e) {
    int newIndex = size();
    super.addLast(e);
    fireIntervalAdded(newIndex, newIndex);
  }


  
  public final void clear() {
    if (isEmpty()) {
      return;
    }
    
    int oldLastIndex = size() - 1;
    super.clear();
    fireIntervalRemoved(0, oldLastIndex);
  }


  
  public final E remove(int index) {
    E removedElement = super.remove(index);
    fireIntervalRemoved(index, index);
    return removedElement;
  }


  
  public final boolean remove(Object o) {
    int index = indexOf(o);
    if (index == -1) {
      return false;
    }
    remove(index);
    return true;
  }


  
  public final E removeFirst() {
    E first = super.removeFirst();
    fireIntervalRemoved(0, 0);
    return first;
  }


  
  public final E removeLast() {
    int lastIndex = size() - 1;
    E last = super.removeLast();
    fireIntervalRemoved(lastIndex, lastIndex);
    return last;
  }


  
  protected final void removeRange(int fromIndex, int toIndex) {
    super.removeRange(fromIndex, toIndex);
    fireIntervalRemoved(fromIndex, toIndex - 1);
  }


  
  public final E set(int index, E element) {
    E previousElement = super.set(index, element);
    fireContentsChanged(index, index);
    return previousElement;
  }


  
  public final ListIterator<E> listIterator(int index) {
    return new ReportingListIterator(super.listIterator(index));
  }
















  
  public final void addListDataListener(ListDataListener l) {
    getEventListenerList().add(ListDataListener.class, l);
  }


  
  public final void removeListDataListener(ListDataListener l) {
    getEventListenerList().remove(ListDataListener.class, l);
  }


  
  public final E getElementAt(int index) {
    return get(index);
  }


  
  public final int getSize() {
    return size();
  }




  
  public final void fireContentsChanged(int index) {
    fireContentsChanged(index, index);
  }







  
  public final void fireContentsChanged(int index0, int index1) {
    Object[] listeners = getEventListenerList().getListenerList();
    ListDataEvent e = null;
    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ListDataListener.class) {
        if (e == null) {
          e = new ListDataEvent(this, 0, index0, index1);
        }
        
        ((ListDataListener)listeners[i + 1]).contentsChanged(e);
      } 
    } 
  }














  
  public final ListDataListener[] getListDataListeners() {
    return getEventListenerList().<ListDataListener>getListeners(ListDataListener.class);
  }











  
  private void fireIntervalAdded(int index0, int index1) {
    Object[] listeners = getEventListenerList().getListenerList();
    ListDataEvent e = null;
    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ListDataListener.class) {
        if (e == null) {
          e = new ListDataEvent(this, 1, index0, index1);
        }
        ((ListDataListener)listeners[i + 1]).intervalAdded(e);
      } 
    } 
  }














  
  private void fireIntervalRemoved(int index0, int index1) {
    Object[] listeners = getEventListenerList().getListenerList();
    ListDataEvent e = null;
    
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ListDataListener.class) {
        if (e == null) {
          e = new ListDataEvent(this, 2, index0, index1);
        }
        ((ListDataListener)listeners[i + 1]).intervalRemoved(e);
      } 
    } 
  }







  
  private EventListenerList getEventListenerList() {
    if (this.listenerList == null) {
      this.listenerList = new EventListenerList();
    }
    return this.listenerList;
  }





  
  private final class ReportingListIterator
    implements ListIterator<E>
  {
    private final ListIterator<E> delegate;




    
    private int lastReturnedIndex;




    
    ReportingListIterator(ListIterator<E> delegate) {
      this.delegate = delegate;
      this.lastReturnedIndex = -1;
    }

    
    public boolean hasNext() {
      return this.delegate.hasNext();
    }

    
    public E next() {
      this.lastReturnedIndex = nextIndex();
      return this.delegate.next();
    }

    
    public boolean hasPrevious() {
      return this.delegate.hasPrevious();
    }

    
    public E previous() {
      this.lastReturnedIndex = previousIndex();
      return this.delegate.previous();
    }

    
    public int nextIndex() {
      return this.delegate.nextIndex();
    }

    
    public int previousIndex() {
      return this.delegate.previousIndex();
    }

    
    public void remove() {
      int oldSize = LinkedListModel.this.size();
      this.delegate.remove();
      int newSize = LinkedListModel.this.size();
      if (newSize < oldSize) {
        LinkedListModel.this.fireIntervalRemoved(this.lastReturnedIndex, this.lastReturnedIndex);
      }
    }

    
    public void set(E e) {
      this.delegate.set(e);
      LinkedListModel.this.fireContentsChanged(this.lastReturnedIndex);
    }

    
    public void add(E e) {
      this.delegate.add(e);
      int newIndex = previousIndex();
      LinkedListModel.this.fireIntervalAdded(newIndex, newIndex);
      this.lastReturnedIndex = -1;
    }
  }
}
