package com.revature.servlet;

import com.revature.service.MovieService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MovieServlet extends HttpServlet {
    MovieService service;

    /**
     * This class interacts with the website
     * urlPattern give the website link its name
     */
    public MovieServlet(MovieService service){
        this.service = service;
    }
    /**
     * Accesses database and returns all information to print to website in JSON format
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.getAllMovies(req, resp);
    }
    /**
     * insert into database with the input given
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.insertMovies(req, resp);
    }
    /**
     * update table with current input given
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.updateMovie(req, resp);
    }
    /**
     * delete row from table based on ID
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.deleteMovie(req, resp);
    }

}
