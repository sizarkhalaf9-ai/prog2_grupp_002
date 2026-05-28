// PROG2 VT2026, Inlämningsuppgift, del 1 
// Grupp 002 
// Julia Liu juli0873
// Mashfi Saiyad masa1911
// Sizar Khalaf sikh1472

package se.su.inlupp;

public interface PathFinder<T> {

  Path<T> findPath(Graph<T> graph, T from, T to);
}

