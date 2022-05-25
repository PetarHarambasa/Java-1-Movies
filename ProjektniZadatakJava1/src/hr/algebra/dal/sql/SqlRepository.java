/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.dal.sql;

import hr.algebra.dal.Repository;
import hr.algebra.model.Actor;
import hr.algebra.model.Director;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.model.Person;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 *
 * @author User
 */
public class SqlRepository implements Repository {

    private static final String USER_ROLE = "RolaKorisnika";
    private static final String MOVIE_ID = "ID";
    private static final String TITLE = "Naslov";
    private static final String DESCRIPTION = "Opis";
    private static final String DURATION = "Trajanje";
    private static final String IMAGE = "Slika";

    private static final String ID = "IDDjelatnik";
    private static final String FIRST_NAME = "Ime";
    private static final String LAST_NAME = "Prezime";
    private static final String TYPE = "Tip";

    private static final String DIRECTOR_ID = "IDRedatelj";
    private static final String DIRECTOR_FIRSTNAME = "RedateljIme";
    private static final String DIRECTOR_LASTNAME = "RedateljPrezime";

    private static final String ACTOR_ID = "IDGlumac";
    private static final String ACTOR_FIRSTNAME = "GlumacIme";
    private static final String ACTOR_LASTNAME = "GlumacPrezime";

    private static final String GENRE_ID = "IDZanr";
    private static final String GENRE = "Zanr";

    private static final String CHECK_USER_EXISTS = "{CALL spCheckIfUserExists(?, ?) }";
    private static final String CREATE_USER = "{CALL spCreateUser(?, ?, ?) }";

    private static final String SELECT_MOVIES = "{CALL spSelectMovies }";
    private static final String CREATE_MOVIE = "{CALL spCreateMovie (?, ?, ?, ?, ?)}";
    private static final String SELECT_MOVIE = "{CALL spSelectMovie (?)}";
    private static final String DELETE_MOVIE = "{CALL spDeleteMovie (?, ?)}";
    private static final String UPDATE_MOVIE = "{CALL spUpdateMovie (?, ?, ?, ?, ?)}";

    private static final String SELECT_GENRES = "{CALL spSelectGenres}";
    private static final String CREATE_GENRE = "{CALL spCreateGenre (?, ?)}";
    private static final String CREATE_GENRES = "{CALL spCreateGenres (?, ?)}";
    private static final String SELECT_GENRE = "{CALL spSelectGenre (?)}";
    private static final String DELETE_GENRE = "{CALL spDeleteGenre (?, ?)}";
    private static final String UPDATE_GENRE = "{CALL spUpdateGenre (?, ?, ?)}";
    private static final String CREATE_MOVIE_GENRE = "{CALL spCreateMovieGenre (?, ?)}";

    private static final String CREATE_ACTOR = "{CALL spCreateActor (?, ?, ?)}";
    private static final String CREATE_ACTORS = "{CALL spCreateActors (?, ?, ?)}";
    private static final String CREATE_MOVIE_ACTOR = "{CALL spCreateMovieActor (?, ?)}";
    private static final String CREATE_DIRECTOR = "{CALL spCreateDirector (?, ?, ?)}";
    private static final String CREATE_DIRECTORS = "{CALL spCreateDirectors (?, ?, ?)}";
    private static final String CREATE_MOVIE_DIRECTOR = "{CALL spCreateMovieDirector (?, ?)}";

    private static final String SELECT_PERSONS = "{CALL spSelectPersons }";
    private static final String UPDATE_PERSON = "{CALL spUpdatePerson (?,?,?,?,?,?)}";
    private static final String SELECT_PERSON = "{CALL spSelectPerson (?)}";
    private static final String DELETE_PERSON = "{CALL spDeletePerson (?, ?)}";
    private static final String SELECT_GENRES_BY_MOVIE_ID = "{CALL spSelectGenresByMovieID (?)}";
    private static final String SELECT_ACTORS_BY_MOVIE_ID = "{CALL spSelectActorsByMovieID (?)}";
    private static final String SELECT_DIRECTORS_BY_MOVIE_ID = "{CALL spSelectDirectorsByMovieID (?)}";

    @Override
    public String checkIfUserExists(String username, String password) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CHECK_USER_EXISTS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(USER_ROLE);
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public int createUser(String username, String password) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_USER)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.executeUpdate();

            return stmt.getInt(3);
        }
    }

    @Override
    public List<Movie> selectMovies() throws Exception {
        List<Movie> movies = new ArrayList<>();

        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_MOVIES);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt(MOVIE_ID),
                        rs.getString(TITLE),
                        rs.getString(DESCRIPTION),
                        rs.getString(IMAGE),
                        rs.getInt(DURATION),
                        selectGenresByMovieID(rs.getInt(MOVIE_ID)),
                        selectDirectorsByMovieID(rs.getInt(MOVIE_ID)),
                        selectActorsByMovieID(rs.getInt(MOVIE_ID))));
            }
        }
        return movies;
    }

    @Override
    public void createMovies(List<Movie> movies) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int createMovie(Movie movie) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE)) {

            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getDescription());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getPicutrePath());
            stmt.registerOutParameter(5, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(5);
        }
    }

    @Override
    public Optional<Movie> selectMovie(int selectMovieID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_MOVIE)) {
            stmt.setInt(1, selectMovieID);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Movie(
                            rs.getInt(MOVIE_ID),
                            rs.getString(TITLE),
                            rs.getString(DESCRIPTION),
                            rs.getString(IMAGE),
                            rs.getInt(DURATION),
                            selectGenresByMovieID(selectMovieID),
                            (List<Person>) (List<?>) selectDirectorsByMovieID(selectMovieID),
                            (List<Person>) (List<?>) selectActorsByMovieID(selectMovieID)
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteMovies() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int deleteMovie(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_MOVIE)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.executeUpdate();
            return stmt.getInt(2);
        }
    }

    @Override
    public void updateMovie(Movie selectedMovie) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_MOVIE)) {
            stmt.setInt(1, selectedMovie.getId());
            stmt.setString(2, selectedMovie.getTitle());
            stmt.setString(3, selectedMovie.getDescription());
            stmt.setInt(4, selectedMovie.getDuration());
            stmt.setString(5, selectedMovie.getPicutrePath());
            stmt.executeUpdate();

            if (!selectedMovie.getActors().isEmpty()) {
                createActors(selectedMovie.getId(), selectedMovie.getActors());
            }

            if (!selectedMovie.getDirector().isEmpty()) {
                createDirectors(selectedMovie.getId(), selectedMovie.getDirector());
            }

            if (!selectedMovie.getActors().isEmpty()) {
                createGenres(selectedMovie.getId(), selectedMovie.getGenre());
            }
        }
    }

    @Override
    public List<Genre> selectGenresByMovieID(int id) throws Exception {
        List<Genre> genres = new ArrayList<>();

        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_GENRES_BY_MOVIE_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genres.add(new Genre(rs.getInt(GENRE_ID), rs.getString(GENRE)));

                }
            }
        }

        return genres;
    }

    @Override
    public List<Person> selectActorsByMovieID(int id) throws Exception {
        List<Person> actors = new ArrayList<>();

        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_ACTORS_BY_MOVIE_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    actors.add(new Person(rs.getInt(ACTOR_ID), rs.getString(ACTOR_FIRSTNAME), rs.getString(ACTOR_LASTNAME)));

                }
            }
        }

        return actors;
    }

    @Override
    public List<Person> selectDirectorsByMovieID(int id) throws Exception {
        List<Person> directors = new ArrayList<>();

        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_DIRECTORS_BY_MOVIE_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    directors.add(new Person(rs.getInt(DIRECTOR_ID), rs.getString(DIRECTOR_FIRSTNAME), rs.getString(DIRECTOR_LASTNAME)));

                }
            }
        }

        return directors;
    }

    @Override
    public List<Person> selectPersons() throws Exception {
        List<Person> persons = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_PERSONS);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                persons.add(new Person(
                        rs.getInt(ID),
                        rs.getString(FIRST_NAME),
                        rs.getString(LAST_NAME),
                        rs.getString(TYPE)
                ));
            }
        }
        return persons;
    }

    @Override
    public int updatePerson(Person selectedPerson, String firstName, String lastName) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_PERSON)) {
            stmt.setInt(1, selectedPerson.id);
            stmt.setString(2, selectedPerson.firstName);
            stmt.setString(3, selectedPerson.lastName);
            stmt.setString(4, firstName);
            stmt.setString(5, lastName);
            stmt.registerOutParameter(6, Types.INTEGER);
            stmt.executeUpdate();
            return stmt.getInt(6);
        }
    }

    @Override
    public Optional<Person> selectPerson(int selectedPersonID) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_PERSON)) {
            stmt.setInt(1, selectedPersonID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Person(
                            rs.getInt(ID),
                            rs.getString(FIRST_NAME),
                            rs.getString(LAST_NAME),
                            rs.getString(TYPE)
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public int deletePerson(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_PERSON)) {
            {
                stmt.setInt(1, id);
                stmt.registerOutParameter(2, Types.INTEGER);
                stmt.executeUpdate();
                return stmt.getInt(2);
            }
        }
    }

    @Override
    public List<Actor> selectActors() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int createActor(Actor actor) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_ACTOR)) {
            stmt.setString(1, actor.getFirstName());
            stmt.setString(2, actor.getLastName());
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.executeUpdate();

            return stmt.getInt(3);
        }
    }

    @Override
    public List<Director> selectDirectors() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int createDirector(Director director) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_DIRECTOR)) {
            stmt.setString(1, director.getFirstName());
            stmt.setString(2, director.getLastName());
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.executeUpdate();

            return stmt.getInt(3);
        }
    }

    @Override
    public List<Genre> selectGenres() throws Exception {
        List<Genre> genres = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_GENRES);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                genres.add(new Genre(
                        rs.getInt(GENRE_ID),
                        rs.getString(GENRE)
                ));
            }
        }

        return genres;
    }

    @Override
    public int createGenre(Genre genre) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_GENRE)) {
            stmt.setString(1, genre.getName());
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.executeUpdate();
            return stmt.getInt(2);
        }
    }

    @Override
    public Optional<Genre> selectGenre(int selectedGenreID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(SELECT_GENRE)) {
            stmt.setInt(1, selectedGenreID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Genre(
                            rs.getInt(GENRE_ID),
                            rs.getString(GENRE)));
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public int deleteGenre(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(DELETE_GENRE)) {
            stmt.setInt(1, id);
            stmt.registerOutParameter(2, Types.INTEGER);
            stmt.executeUpdate();
            return stmt.getInt(2);
        }
    }

    @Override
    public int updateGenre(Genre selectedGenre) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(UPDATE_GENRE)) {
            stmt.setInt(1, selectedGenre.getId());
            stmt.setString(2, selectedGenre.getName());
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.executeUpdate();
            return stmt.getInt(3);
        }
    }

    private void createActors(int movieID, List<Person> actors) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_ACTORS)) {
            for (Person actor : actors) {
                stmt.setString(1, actor.firstName);
                stmt.setString(2, actor.lastName);
                stmt.registerOutParameter(3, Types.INTEGER);
                stmt.executeUpdate();

                int actorID = stmt.getInt(3);
                createMovieActors(movieID, actorID);
            }
        }
    }

    private void createDirectors(int movieID, List<Person> directors) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_DIRECTORS)) {
            for (Person director : directors) {
                stmt.setString(1, director.firstName);
                stmt.setString(2, director.lastName);
                stmt.registerOutParameter(3, Types.INTEGER);
                stmt.executeUpdate();

                int directorID = stmt.getInt(3);
                createMovieDirectors(movieID, directorID);
            }
        }
    }

    private void createGenres(int movieID, List<Genre> genres) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_GENRES)) {
            for (Genre genre : genres) {
                stmt.setString(1, genre.getName());
                stmt.registerOutParameter(2, Types.INTEGER);
                stmt.executeUpdate();

                int genreID = stmt.getInt(2);
                createMovieGenres(movieID, genreID);
            }
        }
    }

    private void createMovieActors(int movieID, int actorID) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE_ACTOR)) {
            stmt.setInt(1, movieID);
            stmt.setInt(2, actorID);

            stmt.executeUpdate();
        }
    }

    private void createMovieDirectors(int movieID, int directorID) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE_DIRECTOR)) {
            stmt.setInt(1, movieID);
            stmt.setInt(2, directorID);

            stmt.executeUpdate();
        }
    }
        
    private void createMovieGenres(int movieID, int genreID) throws SQLException {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection();
                CallableStatement stmt = con.prepareCall(CREATE_MOVIE_GENRE)) {
            stmt.setInt(1, movieID);
            stmt.setInt(2, genreID);

            stmt.executeUpdate();

        }
    }
}