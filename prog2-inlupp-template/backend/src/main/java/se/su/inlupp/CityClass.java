package se.su.inlupp;

public class CityClass {
  private String name;
  private double x;
  private double y;

  public CityClass(String name, double x, double y) {
    this.name = name;
    this.x = x;
    this.y = y;
  }

  public CityClass(String name) {
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

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CityClass city) {
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
    return this.name;
  }
}

