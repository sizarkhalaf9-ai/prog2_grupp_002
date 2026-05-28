// PROG2 VT2026, Inlämningsuppgift, del 1 
// Grupp 002 
// Julia Liu juli0873
// Mashfi Saiyad masa1911
// Sizar Khalaf sikh1472

package se.su.inlupp;

public interface Edge<T> {

  int getWeight();

  void setWeight(int weight);

  T getDestination();

  String getName();
}
