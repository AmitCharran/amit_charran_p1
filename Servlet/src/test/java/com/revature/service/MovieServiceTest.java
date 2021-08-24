package com.revature.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.model.Movies;
import com.revature.model.TvShows;
import com.revature.orm.util.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MovieServiceTest {
    MovieService service;
    Configuration cfg;
    ObjectMapper mapperMock;
    ObjectWriter writerMock;
    HttpServletRequest requestMock;
    HttpServletResponse responseMock;
    List<Movies> moviesList;
    List<Object> objectList;
    Movies movieMock;
    ServletOutputStream outputStreamMock;
    String json;

    @Before
    public void setUp(){
        cfg = Mockito.mock(Configuration.class);
        mapperMock = Mockito.mock(ObjectMapper.class);
        writerMock = Mockito.mock(ObjectWriter.class);
        requestMock = Mockito.mock(HttpServletRequest.class);
        responseMock = Mockito.mock(HttpServletResponse.class);
        outputStreamMock = Mockito.mock(ServletOutputStream.class);

        service = new MovieService(cfg, mapperMock);

        // Empty the list before each
        moviesList = new ArrayList<>();
        objectList = new ArrayList<>();
        movieMock = mock(Movies.class);
        json = "test json";
    }

    @After
    public void tearDown(){}

    @Test
    public void getAllMovies() throws IOException {
        moviesList.add(new Movies());
        when(cfg.getAll(Movies.class)).thenReturn(objectList);
        when(mapperMock.writerWithDefaultPrettyPrinter()).thenReturn(writerMock);
        when(mapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objectList)).thenReturn(json);
        when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

        service.getAllMovies(requestMock, responseMock);

        // verify acts as an assertion confirming that the response code was added successfully
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(outputStreamMock).print(json);
    }

    @Test
    public void insertMovies() throws IOException {
        BufferedReader buff = mock(BufferedReader.class);
        Object objMock = new Movies(1,"name", "genre", 1.0, "R");
        Movies movies = new Movies(1,"name", "genre", 1.0, "R");

        objectList.add(objMock);
        moviesList.add(movies);
        when(requestMock.getReader()).thenReturn(buff);
        when(mapperMock.readValue("anyString()", Movies.class)).thenReturn(movieMock);
        doNothing().when(cfg).insertIntoTable(Movies.class, "name","gener",1,"R");
//        when(cfg.getAll(Movies.class)).thenReturn(objectList);
        when(service.getMovies()).thenReturn(moviesList);
        //when(objectList.get(0)).thenReturn(objMock);

        service.insertMovies(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);

    }

    @Test
    public void updateMovie() throws IOException {
        BufferedReader buff = mock(BufferedReader.class);
        when(requestMock.getReader()).thenReturn(buff);

        when(mapperMock.readValue("any", Movies.class)).thenReturn(movieMock);
        doNothing().when(cfg).update(Movies.class);
        when(service.getMovies()).thenReturn(moviesList);

        service.insertMovies(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);


    }

    @Test
    public void deleteMovie() {
        moviesList.add(movieMock);
        when(service.getMovies()).thenReturn(moviesList);
        when(requestMock.getParameter("movieId")).thenReturn("1");
        when(movieMock.getMovieId()).thenReturn(1);
        doNothing().when(cfg).deleteByID(Movies.class, 1);

        service.deleteMovie(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void deleteBasicTestReturnFalse(){
        Movies movies = new Movies(1,"name", "genre", 1.0, "R");
        when(service.getMovies()).thenReturn(moviesList);
        moviesList.add(movies);

        assertFalse(service.delete(2));

    }

    @Test
    public void updateTestWhenPassingMovieParameterReturnTrue(){
        Movies movies = new Movies(1,"name", "genre", 1.0, "R");
        doNothing().when(cfg).update(movies.getClass(),movies.getMovieId(),movies.getMovieName(),movies.getGenre(),movies.getMovieLength(),movies.getMovieRating());
        when(service.getMovies()).thenReturn(moviesList);
        moviesList.add(movies);

        assertTrue(service.update(movies));
    }

    @Test
    public void insertWhenPassingMoviesParameterReturnsNonZero(){
        Movies movies = new Movies(1,"name", "genre", 1.0, "R");
        doNothing().when(cfg).insertIntoTable(movies.getClass(),movies.getMovieId(),movies.getMovieName(),movies.getGenre(),movies.getMovieLength(),movies.getMovieRating());
        when(service.getMovies()).thenReturn(moviesList);
        moviesList.add(movies);

        assertEquals(service.insert(movies), 1);
    }


}