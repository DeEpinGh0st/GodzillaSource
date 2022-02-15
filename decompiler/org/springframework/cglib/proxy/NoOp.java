package org.springframework.cglib.proxy;






















public interface NoOp
  extends Callback
{
  public static final NoOp INSTANCE = new NoOp() {
    
    };
}
