public class Contact {
    String name;
    String phone;
    Contact left, right;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.left = this.right = null;
    }
}