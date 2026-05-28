// PROG2 VT2026, Inlämningsuppgift, del 1 
// Grupp 002 
// Julia Liu juli0873
// Mashfi Saiyad masa1911
// Sizar Khalaf sikh1472

package se.su.inlupp;

import java.util.List;

public interface Path<T> extends Iterable<Edge<T>> {

  T getStart();

  T getEnd();

  int getTotalWeight();

  List<Edge<T>> getEdges();

  List<T> getNodes();
}

