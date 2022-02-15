package javassist.runtime;






















public class Cflow
  extends ThreadLocal<Cflow.Depth>
{
  protected static class Depth
  {
    private int depth = 0;
    int value() { return this.depth; }
    void inc() { this.depth++; } void dec() {
      this.depth--;
    }
  }
  
  protected synchronized Depth initialValue() {
    return new Depth();
  }


  
  public void enter() {
    get().inc();
  }

  
  public void exit() {
    get().dec();
  }

  
  public int value() {
    return get().value();
  }
}
