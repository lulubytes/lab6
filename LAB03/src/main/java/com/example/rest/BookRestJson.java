package com.example.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Path("bookjson")
public class BookRestJson {

    private static ArrayList<Book> books = new ArrayList<>();

    /**
     * meant for returning the list of books
     * @return a concatenation of the toStirng method for all books
     */

    @GET
    @Produces("application/json")
    public ArrayList<Book> getBook(){
        return books;
    }

    /**
     * meant for getting a book with id
     * @param title of book
     * @return toSring method of book
     */
    @GET
    @Produces("application/json")
    @Path("{title}")
    public Book getBookList(@PathParam("title") String title){
        return books.stream().filter(book1 -> book1.getTitle().equals(title))
                .findFirst()
                .orElse(null);

    }

    /**
     * meant for creating books using the post method
     *
     *
     */

    @POST
    @Consumes("application/json")
    public void createBook(Book book) {
        books.add(new Book(book));
    }

    /**
     * meant for replacing book
     *
     * @param title of the book
     *
     */


    @PUT
    @Path("{title}")
    @Consumes("application/json")
    public void modifyBook(@PathParam("title") String title, Book book) {
        deleteBook(title);
        books.add(new Book(book));
    }

    /**
     * meant for deleting book with specific id
     * @param title of book
     */

    @DELETE
    @Path("{title}")
    public void deleteBook(@PathParam("title") String title) {
        books = books.stream().filter(book -> !book.getTitle().equals(title))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * debugging statement that prints the current state of the list of books
     */
    private void printBooks(){
        for (Book book: books) {
            System.out.println(books);
        }
    }


}
