package com.revature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.revature.model.Movies;
import com.revature.orm.util.Configuration;
import com.revature.service.MovieService;
import com.revature.service.TvShowService;
import com.revature.servlet.MovieServlet;
import com.revature.servlet.TvShowServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@WebListener
public class Driver implements ServletContextListener {
    private String url;
    private String user;
    private String pass;
    private static Properties properties;
    private static InputStream input;
    private static final Logger logger = LoggerFactory.getLogger(TvShowService.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        properties = new Properties();
        try {
            Class.forName("org.postgresql.Driver");
            input = TvShowServlet.class.getResourceAsStream("/credentials.properties");
            properties.load(input);

        }catch (IOException | ClassNotFoundException e){
            logger.warn("Cannot find credentials for database");
            return;
        }
        String url = properties.getProperty("endpoint");
        String user = properties.getProperty("username");
        String pass = properties.getProperty("password");

        Configuration cfg = new Configuration(url,user,pass);

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        TvShowService tvService = new TvShowService(cfg, objectMapper);
        MovieService movieService = new MovieService(cfg, objectMapper);

        ServletContext context = sce.getServletContext();
        context.addServlet("TvShow Servlet", new TvShowServlet(tvService)).addMapping("/tvShows");
        context.addServlet("Movie Servlet", new MovieServlet(movieService)).addMapping("/movies");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }

    public static void main(String[] args) {
        properties = new Properties();
        try {
            Class.forName("org.postgresql.Driver");
            input = TvShowServlet.class.getResourceAsStream("/credentials.properties");
            properties.load(input);

        }catch (IOException | ClassNotFoundException e){
            logger.warn("Cannot find credentials for database");
            return;
        }
        String url = properties.getProperty("endpoint");
        String user = properties.getProperty("username");
        String pass = properties.getProperty("password");

        Configuration cfg = new Configuration(url,user,pass);
        cfg.addAnnotatedClass(Movies.class);
    }
}
