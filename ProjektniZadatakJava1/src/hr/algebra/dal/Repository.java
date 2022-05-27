/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal;

import hr.algebra.model.Actor;
import hr.algebra.model.Director;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.model.Person;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author User
 */
public interface Repository {

    String checkIfUserExists(String username, String password) throws Exception;

    int createUser(String username, String password) throws Exception;

    List<Genre> selectGenresByMovieID(int id) throws Exception;

    List<Person> selectActorsByMovieID(int id) throws Exception;

    List<Person> selectDirectorsByMovieID(int id) throws Exception;

    void createMovies(List<Movie> movies) throws Exception;

    void deleteMovies() throws Exception;

    int createMovie(Movie movie) throws Exception;

    List<Movie> selectMovies() throws Exception;

    Optional<Movie> selectMovie(int selectMovieID) throws Exception;

    void updateMovie(Movie selectedMovie) throws Exception;
    
    List<Person> selectPersons() throws Exception;

    int updatePerson(Person selectedPerson, String firstName, String lastName) throws Exception;

    Optional<Person> selectPerson(int selectedPersonID) throws Exception;

    List<Actor> selectActors() throws Exception;

    int createActor(Actor actor) throws Exception;

    List<Director> selectDirectors() throws Exception;

    int createDirector(Director director) throws Exception;

    List<Genre> selectGenres() throws Exception;

    int createGenre(Genre genre) throws Exception;

    Optional<Genre> selectGenre(int selectedGenreID) throws Exception;

    int deleteGenre(int id) throws Exception;

    int updateGenre(Genre selectedGenre) throws Exception;

    int deletePerson(int id) throws Exception;

    int deleteMovie(int id) throws Exception;
}
