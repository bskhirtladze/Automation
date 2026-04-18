package enums;

public enum NavigationItem {
    HOME(""),
    PRODUCTS("products"),
    CART("view_cart"),
    LOGIN("login"),
    CASES("test_cases"),
    TUTORIALS("https://www.youtube.com/c/AutomationExercise"),
    API("api_list"),
    CONTACT("contact_us"),;

    private final String item;


    NavigationItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }
}