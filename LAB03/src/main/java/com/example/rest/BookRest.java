package com.example.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@Path("library")
public class BookRest {

    private static ArrayList<Book> books = new ArrayList<>();

    /**
     * meant for returning the list of books
     * @return a concatenation of the toStirng method for all books
     */

    @GET
    @Produces("application/xml")
    public ArrayList<Book> getBook(){
        return books;
    }

    /**
     * meant for getting a book with id
     * @param title of book
     * @return toSring method of book
     */
    @GET
    @Produces("application/xml")
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

//    @POST
//    @Consumes("application/xml")
//    public void createBook(Book book) {
//        books.add(new Book(book));
//    }


//    @POST
//    @Path("{title}/{author}/{isbn}")
//    public void createBook(@PathParam("title") String title, @PathParam("author") String author, @PathParam("isbn") String isbn) {
//        Book newBook = new Book(title, author, isbn);
//        books.add(newBook);
//    }


    @POST
    public void createBook(@FormParam("title") String title, @FormParam("author") String author, @FormParam("isbn") String isbn) {
        Book newB = new Book(title, author, isbn);
        books.add(newB);
    }







    /**
     * meant for replacing book
     *
     * @param title of the book
     *
     */


//    @PUT
//    @Path("{title}")
//    @Consumes("application/xml")
//    public void modifyBook(@PathParam("title") String title, Book book) {
//        deleteBook(title);
//        books.add(new Book(book));
//    }

    @PUT
    @Path("{title}/{author}/{isbn}")
    @Consumes("application/xml")
    public void modifyBook(@PathParam("title") String title, @PathParam("author") String author, @PathParam("isbn") String isbn) {
        deleteBook(title);
        createBook(title, author, isbn);
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
