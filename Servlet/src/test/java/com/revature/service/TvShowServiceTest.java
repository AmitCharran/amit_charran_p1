package com.revature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.model.TvShows;
import com.revature.model.TvShows;
import com.revature.orm.util.Configuration;
import org.checkerframework.checker.units.qual.A;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class TvShowServiceTest {
    TvShowService service;
    Configuration cfg;
    ObjectMapper mapperMock;
    ObjectWriter writerMock;
    HttpServletRequest requestMock;
    HttpServletResponse responseMock;
    List<TvShows> TvShowsList;
    List<Object> objectList;
    TvShows TvShowMock;
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

        service = new TvShowService(cfg, mapperMock);

        // Empty the list before each
        TvShowsList = new ArrayList<>();
        objectList = new ArrayList<>();
        TvShowMock = mock(TvShows.class);
        json = "test json";
    }

    @After
    public void tearDown(){}

    @Test
    public void getAllTvShows() throws IOException {
        TvShowsList.add(new TvShows());
        when(cfg.getAll(TvShows.class)).thenReturn(objectList);
        when(mapperMock.writerWithDefaultPrettyPrinter()).thenReturn(writerMock);
        when(mapperMock.writerWithDefaultPrettyPrinter().writeValueAsString(objectList)).thenReturn(json);
        when(responseMock.getOutputStream()).thenReturn(outputStreamMock);

        service.getAllTvShows(requestMock, responseMock);

        // verify acts as an assertion confirming that the response code was added successfully
        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
        verify(outputStreamMock).print(json);
    }

    @Test
    public void insertTvShows() throws IOException {
        BufferedReader buff = mock(BufferedReader.class);
        Object objMock = new TvShows(1,"name", "genre",1.0);
        TvShows TvShows = new TvShows(1,"name", "genre", 1.0);

        objectList.add(objMock);
        TvShowsList.add(TvShows);
        when(requestMock.getReader()).thenReturn(buff);
//        when(mapperMock.readValue("anyString()", TvShows.class)).thenReturn(TvShowMock);
       // doNothing().when(cfg).insertIntoTable(TvShows.class, "name","gene",1);
//        when(cfg.getAll(TvShows.class)).thenReturn(objectList);
        when(service.getTvShows()).thenReturn(TvShowsList);
        //when(objectList.get(0)).thenReturn(objMock);

        service.insertTvShows(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);

    }

    @Test
    public void updateTvShow() throws IOException {
        BufferedReader buff = mock(BufferedReader.class);
        when(requestMock.getReader()).thenReturn(buff);

//        when(mapperMock.readValue("any", TvShows.class)).thenReturn(TvShowMock);
 //       doNothing().when(cfg).update(TvShows.class);
        when(service.getTvShows()).thenReturn(TvShowsList);

        service.insertTvShows(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_BAD_REQUEST);


    }

    @Test
    public void deleteTvShow() {
        TvShowsList.add(TvShowMock);
        when(service.getTvShows()).thenReturn(TvShowsList);
        when(requestMock.getParameter("tvId")).thenReturn("1");
        when(TvShowMock.getTvId()).thenReturn(1);
        doNothing().when(cfg).deleteByID(TvShows.class, 1);

        service.deleteTvShow(requestMock, responseMock);

        verify(responseMock).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    public void deleteBasicTestReturnFalse(){
        TvShows TvShows = new TvShows(1,"name", "genre", 1.0);
        when(service.getTvShows()).thenReturn(TvShowsList);
        TvShowsList.add(TvShows);

        assertFalse(service.delete(2));

    }

    @Test
    public void updateTestWhenPassingTvShowParameterReturnTrue(){
        TvShows TvShows = new TvShows(1,"name", "genre", 1.0);
        doNothing().when(cfg).update(TvShows.getClass(),TvShows.getTvId(),TvShows.getTvShowName(),TvShows.getGenre(),TvShows.getLength());
        when(service.getTvShows()).thenReturn(TvShowsList);
        TvShowsList.add(TvShows);

        assertTrue(service.update(TvShows));
    }

    @Test
    public void insertWhenPassingTvShowsParameterReturnsNonZero(){
        TvShows TvShows = new TvShows(1,"name", "genre", 1.0);
//        doNothing().when(cfg).insertIntoTable(TvShows.getClass(),TvShows.getTvId(),TvShows.getTvShowName(),TvShows.getGenre(),TvShows.getLength());
        when(service.getTvShows()).thenReturn(TvShowsList);
        TvShowsList.add(TvShows);

        assertEquals(service.insert(TvShows), 1);
    }
}

