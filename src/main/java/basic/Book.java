package basic;

public class Book {

    private String name;
    private String author;
    private String address;

    public Book() {
    }

    public Book(String name, String author, String address) {
        this.name = name;
        this.author = author;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Book book = new Book();
        book.setName("书的名字");
        book.setAuthor("书的作者");
        System.out.println(book);
    }

}
