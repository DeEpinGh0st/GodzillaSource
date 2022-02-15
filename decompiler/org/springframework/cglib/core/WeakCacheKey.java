package org.springframework.cglib.core;

import java.lang.ref.WeakReference;








public class WeakCacheKey<T>
  extends WeakReference<T>
{
  private final int hash;
  
  public WeakCacheKey(T referent) {
    super(referent);
    this.hash = referent.hashCode();
  }

  
  public boolean equals(Object obj) {
    if (!(obj instanceof WeakCacheKey)) {
      return false;
    }
    Object ours = get();
    Object theirs = ((WeakCacheKey)obj).get();
    return (ours != null && theirs != null && ours.equals(theirs));
  }

  
  public int hashCode() {
    return this.hash;
  }

  
  public String toString() {
    T t = get();
    return (t == null) ? ("Clean WeakIdentityKey, hash: " + this.hash) : t.toString();
  }
}
