package nz.ac.auckland.se206.util;

public class Category {

  private String name;
  private String description;
  private CategoryType categoryType;

  /**
   * Constructor for Category class which stores the name and description of the category/word as
   * well as its category type
   *
   * @param name name of category
   * @param description description of category
   * @param categoryType type of the category
   */
  Category(String name, String description, CategoryType categoryType) {
    this.name = name;
    this.categoryType = categoryType;
    this.description = description;
  }

  /**
   * This method will get the name of the category
   *
   * @return category name
   */
  public String getName() {
    return this.name;
  }

  /**
   * This method will get the description of the category
   *
   * @return category description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * This method will get the category type of the category
   *
   * @return category type of the category
   */
  public CategoryType getCategoryType() {
    return this.categoryType;
  }
}
