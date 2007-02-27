package org.aspectbench.runtime.internal.cflowinternal;

public class StackLong { 
  public static class Cell { 
    public Cell prev;
    public long elem;
    public Cell(Cell prev, long elem) { this.prev = prev; this.elem = elem; }
    public int depth() {
        if(prev == null) return 1;
        return prev.depth()+1;
    }
  }
  
  public Cell top = null;
}
