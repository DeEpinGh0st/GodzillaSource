package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;




























@GwtCompatible
public enum RemovalCause
{
  EXPLICIT
  {
    boolean wasEvicted() {
      return false;
    }
  },






  
  REPLACED
  {
    boolean wasEvicted() {
      return false;
    }
  },





  
  COLLECTED
  {
    boolean wasEvicted() {
      return true;
    }
  },




  
  EXPIRED
  {
    boolean wasEvicted() {
      return true;
    }
  },




  
  SIZE
  {
    boolean wasEvicted() {
      return true;
    }
  };
  
  abstract boolean wasEvicted();
}
