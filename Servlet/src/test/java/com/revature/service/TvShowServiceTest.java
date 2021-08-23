package com.revature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.revature.model.TvShows;
import com.revature.orm.util.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class TvShowServiceTest {

    private TvShowService tvService;
    private HttpServletRequest req;
    private HttpServletResponse res;
    Configuration cfg;
     ObjectMapper objMap;
    private ObjectWriter objWrite;

    private List<Object> objList;
    private ServletOutputStream outputStream;

    private String json;

    private TvShows tvShows;

    @Before
    public void setUp() throws Exception{
        cfg = mock(Configuration.class);
        req = mock(HttpServletRequest.class);
        res = mock(HttpServletResponse.class);
        objMap = mock(ObjectMapper.class);
        objWrite = mock(ObjectWriter.class);

        objList = new ArrayList<>();
//        when(new Configuration(anyString(), anyString(), anyString())).thenReturn(cfg);
//        when(new ObjectMapper()).thenReturn(objMap);


        tvService = new TvShowService(cfg, objMap);

        json = "test json";


    }

    @Test
    public void getAllTvShowsBasicTest() throws IOException {
        objList.add(TvShows.class);
        when(cfg.getAll(any(Class.class))).thenReturn(objList);
        when(objMap.writerWithDefaultPrettyPrinter()).thenReturn(objWrite);
        when(objMap.writerWithDefaultPrettyPrinter().writeValueAsString(objList)).thenReturn(json);

        tvService.getAllTvShows(req,res);

        verify(res).setStatus(HttpServletResponse.SC_OK);
        verify(outputStream).print(json);

    }
}

