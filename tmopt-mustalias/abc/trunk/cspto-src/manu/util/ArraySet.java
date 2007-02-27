/****************************************
 * 
 * Copyright (c) 2006, University of California, Berkeley.
 * All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *
 * - Redistributions of source code must retain the above copyright 
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright 
 *   notice, this list of conditions and the following disclaimer in the 
 *   documentation and/or other materials provided with the 
 *   distribution.
 * - Neither the name of the University of California, Berkeley nor the 
 *   names of its contributors may be used to endorse or promote 
 *   products derived from this software without specific prior written 
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS 
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED 
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 ***************************************/ 

package manu.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author manu_s
 * 
 */
public class ArraySet<T> extends AbstractSet<T> {

  private static final ArraySet EMPTY = new ArraySet<Object>(0, true) {
    public boolean add(Object obj_) {
      throw new RuntimeException();
    }
  };

  @SuppressWarnings("all")
  public static final <T> ArraySet<T> empty() {
    return (ArraySet<T>) EMPTY;
  }

  private T[] _elems;

  private int _curIndex = 0;

  private final boolean checkDupes;

  @SuppressWarnings("all")
  public ArraySet(int numElems_, boolean checkDupes) {
    _elems = (T[]) new Object[numElems_];
    this.checkDupes = checkDupes;
  }

  public ArraySet() {
    this(1, true);
  }

  @SuppressWarnings("all")
  public ArraySet(ArraySet<T> other) {
    int size = other._curIndex;
    this._elems = (T[]) new Object[size];
    this.checkDupes = other.checkDupes;
    this._curIndex = size;
    System.arraycopy(other._elems, 0, _elems, 0, size);
  }

  public ArraySet(Collection<T> other) {
    this(other.size(), true);
    addAll(other);
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.AAASet#add(java.lang.Object)
   */
  @SuppressWarnings("all")
  public boolean add(T obj_) {
    assert obj_ != null;
    if (checkDupes && this.contains(obj_))
      return false;
    if (_curIndex == _elems.length) {
      // lengthen array
      Object[] tmp = _elems;
      _elems = (T[]) new Object[tmp.length * 2];
      System.arraycopy(tmp, 0, _elems, 0, tmp.length);
    }
    _elems[_curIndex] = obj_;
    _curIndex++;
    return true;
  }

  public boolean addAll(ArraySet<T> other) {
    boolean ret = false;
    for (int i = 0; i < other.size(); i++) {
      boolean added = add(other.get(i));
      ret = ret || added;
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.AAASet#contains(java.lang.Object)
   */
  public boolean contains(Object obj_) {
    for (int i = 0; i < _curIndex; i++) {
      if (_elems[i].equals(obj_))
        return true;
    }
    return false;
  }

  public boolean intersects(ArraySet<T> other) {
    for (int i = 0; i < other.size(); i++) {
      if (contains(other.get(i)))
        return true;
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see AAA.util.AAASet#forall(AAA.util.ObjectVisitor)
   */
  public void forall(ObjectVisitor<T> visitor_) {
    for (int i = 0; i < _curIndex; i++) {
      visitor_.visit(_elems[i]);
    }
  }

  public int size() {
    return _curIndex;
  }

  public T get(int i) {
    return _elems[i];
  }

  public boolean remove(Object obj_) {
    int ind;
    for (ind = 0; ind < _curIndex && !_elems[ind].equals(obj_); ind++) {
    }
    // check if object was never there
    if (ind == _curIndex)
      return false;
    return remove(ind);
  }

  /**
   * @param ind
   * @return
   */
  public boolean remove(int ind) {
    // hope i got this right...
    System.arraycopy(_elems, ind + 1, _elems, ind, _curIndex - (ind + 1));
    _curIndex--;
    return true;
  }

  public void clear() {
    _curIndex = 0;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public String toString() {
    StringBuffer ret = new StringBuffer();
    ret.append('[');
    for (int i = 0; i < size(); i++) {
      ret.append(get(i).toString());
      if (i + 1 < size()) {
        ret.append(", ");
      }
    }
    ret.append(']');
    return ret.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Set#toArray()
   */
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Set#addAll(java.util.Collection)
   */
  public boolean addAll(Collection<? extends T> c) {
    boolean ret = false;
    for (T element : c) {
      boolean added = add(element);
      ret = ret || added;
    }
    return ret;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Set#iterator()
   */
  public Iterator<T> iterator() {
    return new ArraySetIterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Set#toArray(java.lang.Object[])
   */
  @SuppressWarnings("unchecked")
  public <U> U[] toArray(U[] a) {
    for (int i = 0; i < _curIndex; i++) {
      T t = _elems[i];
      a[i] = (U) t;
    }
    return a;
  }

  /**
   * @author manu
   */
  public class ArraySetIterator implements Iterator<T> {

    int ind = 0;

    final int setSize = size();

    /**
     * 
     */
    public ArraySetIterator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
      return ind < setSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    public T next() {
      if (ind >= setSize) {
        throw new NoSuchElementException();
      }
      return get(ind++);
    }

  }

}
