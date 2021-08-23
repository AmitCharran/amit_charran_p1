package com.revature.model;

import com.revature.orm.annotations.Column;
import com.revature.orm.annotations.Entity;
import com.revature.orm.annotations.Id;

/**
 * Class created with annotations to work with my ORM
 */
@Entity(tableName = "movies")
public class Movies {
    /**
     * This will identify primary key
     */
    @Id(columnName = "id")
    private int movieId;

    /**
     * This will be the first column
     */
    @Column(columnName = "movieName")
    private String movieName;

    /**
     * This will be the second column
     */
    @Column(columnName = "genre")
    private String genre;

    /**
     * Another column in SQL tbale
     */
    @Column(columnName = "movieLength")
    private double movieLength;

    /**
     * the final column in SQL table
     */
    @Column(columnName = "movieRating")
    private String movieRating;

    public Movies() {}

    public Movies(int movieId, String movieName, String genre, double movieLength, String movieRating) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.genre = genre;
        this.movieLength = movieLength;
        this.movieRating = movieRating;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(double movieLength) {
        this.movieLength = movieLength;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    /**
     * Used to compare table values are eqaul
     * This is used to grab primary key when none are provided
     * @param movie Object to compare to
     * @return if value exists in table return true
     */
    public boolean compareWithoutMovieId(Movies movie){
        if(movie.getMovieName().equals(movieName)
                && movie.getMovieRating().equals(movieRating)
                && movie.getGenre().equals(genre)
                && movieLength == movie.getMovieLength()){
            return true;
        }
        return false;
    }

    public String toString(){
        return "Movie id: " + movieId
                + "\nMovie name: " + movieName
                + "\nMovie genre: " + genre
                + "\nMovie length: " + movieLength
                + "\nMovie Rating: " + movieRating;
    }

}
