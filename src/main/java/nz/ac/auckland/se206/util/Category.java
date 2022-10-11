package nz.ac.auckland.se206.util;

public class Category {

  public String name;
  public String description;
  public CategoryType categoryType;

  Category(String name, String description, CategoryType categoryType) {
    this.name = name;
    this.categoryType = categoryType;
    this.description = description;
  }
}
