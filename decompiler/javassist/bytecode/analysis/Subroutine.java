package javassist.bytecode.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;





















public class Subroutine
{
  private List<Integer> callers = new ArrayList<>();
  private Set<Integer> access = new HashSet<>();
  private int start;
  
  public Subroutine(int start, int caller) {
    this.start = start;
    this.callers.add(Integer.valueOf(caller));
  }
  
  public void addCaller(int caller) {
    this.callers.add(Integer.valueOf(caller));
  }
  
  public int start() {
    return this.start;
  }
  
  public void access(int index) {
    this.access.add(Integer.valueOf(index));
  }
  
  public boolean isAccessed(int index) {
    return this.access.contains(Integer.valueOf(index));
  }
  
  public Collection<Integer> accessed() {
    return this.access;
  }
  
  public Collection<Integer> callers() {
    return this.callers;
  }

  
  public String toString() {
    return "start = " + this.start + " callers = " + this.callers.toString();
  }
}
