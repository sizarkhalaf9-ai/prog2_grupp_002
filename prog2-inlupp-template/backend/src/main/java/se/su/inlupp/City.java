package se.su.inlupp;

import java.io.Serializable;

public class City implements Serializable {
  private String name;
  private double x;
  private double y;

  public City(String name, double x, double y) {
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public City(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

    public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof City city) {
      return name.equals(city.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return this.name + "; " + this.x + "; " + this.y;
  }
}

