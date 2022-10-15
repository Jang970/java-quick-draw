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
   * @return the name of the category
   */
  public String getName() {
    return this.name;
  }

  /**
   * This method will get the description of the category
   *
   * @return description/definition of category
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * This method will get the category type of the category
   *
   * @return category type of the word
   */
  public CategoryType getCategoryType() {
    return this.categoryType;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return name.hashCode();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object arg0) {
    if (arg0 != null && arg0 instanceof Category) {
      return name.equals(((Category) arg0).name);
    } else {
      return false;
    }
  }
}
