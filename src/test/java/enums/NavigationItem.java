package enums;

/**
 * Represents navigation bar items on automationexercise.com.
 *
 * Each constant holds the href fragment used to locate the link in the navbar:
 *   //ul[@class='nav navbar-nav']//a[contains(@href, '<item>')]
 *
 * Usage:
 *   navigationPage.navigateTo(NavigationItem.CASES);
 */
public enum NavigationItem {

    HOME("/"),
    PRODUCTS("/products"),
    CART("/view_cart"),
    LOGIN("/login"),
    CASES("/test_cases"),
    API_TESTING("/api_testing"),
    CONTACT("/contact_us");

    private final String item;

    NavigationItem(String item) {
        this.item = item;
    }

    public String getItem() {
        return item;
    }
}
