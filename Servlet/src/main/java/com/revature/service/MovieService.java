package com.revature.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.model.MovieRating;
import com.revature.model.Movies;
import com.revature.model.TvShows;
import com.revature.orm.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 * Handles functions from servlet class
 */
public class MovieService {
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    /**
     * used to connect to the database and database functions
     */
    private Configuration cfg;
    private ObjectMapper mapper;

    public MovieService(Configuration cfg, ObjectMapper mapper){
        this.cfg = cfg;
        this.mapper = mapper;

    }
    /**
     * Accesses database and returns all information to print to website in JSON format
     * @param req
     * @param res html status
     * @throws ServletException
     * @throws IOException
     */
    public void getAllMovies(HttpServletRequest req, HttpServletResponse res){
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(getMovies());
            res.getOutputStream().print(json);
            res.setStatus(HttpServletResponse.SC_OK);

        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }
    /**
     * insert into database with the input given
     * @param req info from user
     * @param resp returns html status
     * @throws ServletException
     * @throws IOException
     */
    public void insertMovies(HttpServletRequest req, HttpServletResponse resp) {
        try {

            StringBuilder builder = new StringBuilder();
            req.getReader().lines()
                    .collect(Collectors.toList())
                    .forEach(builder::append);

            Movies movie = mapper.readValue(builder.toString(), Movies.class);
            int result = insert(movie);

            if(result != 0){
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else{

                // TODO: refactor with exception propagation to set better status codes
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn(e.getMessage());
        }
    }
    /**
     * update table with current input given
     * @param req
     * @param resp html status
     * @throws ServletException
     * @throws IOException
     */
    public void updateMovie(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder builder = new StringBuilder();
        try {

            req.getReader().lines()
                    .collect(Collectors.toList())
                    .forEach(builder::append);

            Movies movie = mapper.readValue(builder.toString(), Movies.class);

            if(movie.getMovieId() != 0){
                boolean result = update(movie);

                if(result){
                    resp.setStatus(HttpServletResponse.SC_OK);

                    String JSON = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cfg.getDbUsername());
                    resp.getWriter().print(JSON);
                }

            } else{
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * delete row from table based on ID
     * @param req info from user
     * @param resp html status
     * @throws ServletException
     * @throws IOException
     */
    public void deleteMovie(HttpServletRequest req, HttpServletResponse resp) {
        boolean result = delete(Integer.parseInt(req.getParameter("movieId")));

        if(result){
            resp.setStatus(HttpServletResponse.SC_OK);
        } else{
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
    /**
     * Deletes current row
     * @param id id of current row to delete
     * @return true if row is deleted, false if is not deleted
     */
    protected boolean delete(int id){
        List<Movies> movies = getMovies();
        for(Movies movie: movies){
            if(movie.getMovieId() == id){
                cfg.deleteByID(Movies.class, id);
                return true;
            }
        }
        return false;
    }
    /**
     * changes value in current table. Table identified id from current object
     * @param movie current row user wants to update
     * @return true if item exists and can be updated and false if item id does not exist
     */
    protected boolean update(Movies movie){
        cfg.update(movie.getClass(), movie.getMovieId(), movie.getMovieName(), movie.getGenre(), movie.getMovieLength(), movie.getMovieRating());
        List<Movies> allMovies = getMovies();

        for(Movies m: allMovies){
            if(m.compareWithoutMovieId(movie)){
                return true;
            }
        }
        return false;
    }

    /**
     * connect to database and gets info from tvShow table
     * @return list of movie objects
     */
    protected List<Movies> getMovies(){
        List<Movies> answer = new ArrayList<>();
        List<Object> result = cfg.getAll(Movies.class);

        for(int i =0; i < result.size(); i++){
            Object o = result.get(i);
            if(o instanceof Movies){
                Movies toAdd = new Movies(((Movies) o).getMovieId(),
                        ((Movies) o).getMovieName(),
                        ((Movies) o).getGenre(),
                        ((Movies) o).getMovieLength(),
                        ((Movies) o).getMovieRating());
                answer.add(toAdd);
            }

        }

        return answer;
    }
    /**
     * updates table
     * @param movie values to update and helps get the table
     * @return 0 = https status conflict --> returns tvId
     */
    protected int insert(Movies movie){
        cfg.insertIntoTable(movie.getClass(), movie.getMovieName(), movie.getGenre(), movie.getMovieLength(), movie.getMovieRating());

        List<Movies> allMovies = getMovies();

        for(Movies m: allMovies){
            if(m.compareWithoutMovieId(movie)){
                return m.getMovieId();
            }
        }

        return 0;
    }


}
