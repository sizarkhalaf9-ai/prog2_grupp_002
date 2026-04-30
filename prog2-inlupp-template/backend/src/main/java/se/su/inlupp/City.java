package se.su.inlupp;

public class City {
  private String name;

  public City(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
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
    return this.name;
  }
}

