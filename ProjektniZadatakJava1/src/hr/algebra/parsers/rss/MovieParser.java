/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.parsers.rss;

import hr.algebra.dal.Repository;
import hr.algebra.dal.RepositoryFactory;
import hr.algebra.factory.ParserFactory;
import hr.algebra.factory.UrlConnectionFactory;
import hr.algebra.model.Actor;
import hr.algebra.model.Director;
import hr.algebra.model.Genre;
import hr.algebra.model.Movie;
import hr.algebra.model.Person;
import hr.algebra.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author CROma
 */
public class MovieParser {

    private static final String RSS_URL = "https://www.blitz-cinestar.hr/rss.aspx?najava=1";
    private static final int TIMEOUT = 10000;
    private static final String DIR = "assets\\movies";
    private static final String REQUETS_METHOD = "GET";
    private static final String[] MOVIE_TYPE = {"3D", "4DX", "IMAX", "DI"};
    private static final String STRREGEX = "<[^>]*>";
    private static final String DELIMITER = "\\,";
    private static final String DELIMITER_SPACE = " ";
    private static final String EXT_JPEG = ".jpeg";
    private static final String EXT_JPG = ".jpg";

    private static final Repository REPOSITORY = RepositoryFactory.getRepository();
    private static final Random RANDOM = new Random();

    public static List<Movie> parse() throws Exception {
        Set<String> existsMoviesTitle = REPOSITORY.selectMoviesTitle();
        List<Movie> movies = new ArrayList<>();
        HttpURLConnection con = UrlConnectionFactory.getHttpUrlConnection(RSS_URL, TIMEOUT, REQUETS_METHOD);
        XMLEventReader reader = ParserFactory.createStaxParser(con.getInputStream());
        Optional<TagType> tagType = Optional.empty();

        Movie movie = null;
        StartElement startElement = null;
        List<Genre> genres = null;
        List<Actor> actors = null;
        List<Director> directors = null;
        Boolean movieTypeExists = true;
        Boolean movieExitst = true;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    startElement = event.asStartElement();
                    String qName = startElement.getName().getLocalPart();
                    tagType = TagType.from(qName);
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (tagType.isPresent()) {
                        Characters characters = event.asCharacters();
                        String data = characters.getData().trim();
                        switch (tagType.get()) {
                            case ITEM:
                                if (movieTypeExists && movieExitst) {
                                    genres = new ArrayList<>();
                                    actors = new ArrayList<>();
                                    directors = new ArrayList<>();
                                    movie = new Movie();
                                    movie.setGenre(genres);
                                    movie.setActors((List<Person>) (List< ?>) actors);
                                    movie.setDirector((List<Person>) (List< ?>) directors);
                                    movies.add(movie);
                                    break;
                                }
                                movieTypeExists = true;
                                movieExitst = true;
                            case TITLE:
                                if (movie != null && !data.isEmpty()) {
                                    if (!Arrays.stream(MOVIE_TYPE).parallel().anyMatch(data::contains) && !existsMoviesTitle.contains(data)) {
                                        movie.setTitle(data);
                                        break;
                                    }
                                    movieTypeExists = false;
                                    movieExitst = false;
                                }
                                break;
                            case DESCRIPTION:
                                if (movie != null && !data.isEmpty()) {
                                    movie.setDescription(data.replaceAll(STRREGEX, ""));
                                }
                                break;
                            case DIRECTOR:
                                if (movie != null && !data.isEmpty()) {
                                    String[] rssDirector = data.split(DELIMITER);
                                    for (String director : rssDirector) {
                                        String[] directorStrings = director.trim().split(DELIMITER_SPACE);
                                        if (directorStrings.length > 1) {
                                            directors.add(new Director(directorStrings[0], directorStrings[1]));
                                        } else {
                                            directors.add(new Director());
                                        }
                                    }
                                }
                                break;
                            case ACTORS:
                                if (movie != null && !data.isEmpty()) {
                                    String[] rssActor = data.split(DELIMITER);
                                    for (String actor : rssActor) {
                                        String[] actorStrings = actor.trim().split(DELIMITER_SPACE);
                                        if (actorStrings.length >= 2) {
                                            actors.add(new Actor(actorStrings[0], actorStrings[1]));
                                        } else if (actorStrings.length == 1) {
                                            actors.add(new Actor(actorStrings[0], ""));
                                        } else {
                                            actors.add(new Actor());
                                        }
                                    }
                                }
                                break;
                            case DURATION:
                                if (movie != null && !data.isEmpty()) {
                                    movie.setDuration(Integer.parseInt(data));
                                }
                                break;
                            case GENRE:
                                if (movie != null && !data.isEmpty()) {
                                    String[] rssGenres = data.split(DELIMITER);
                                    for (String genre : rssGenres) {
                                        genres.add(new Genre(genre.toLowerCase().trim()));
                                    }
                                }
                                break;
                            case IMAGE:
                                if (movie != null && !data.isEmpty()) {
                                    if (movieTypeExists && movieExitst) {
                                        handlePicture(movie, data);
                                    }
                                }
                                break;
                        }
                    }
                    break;
            }
        }
        return movies;
    }

    private static void handlePicture(Movie movie, String pictureURL) throws IOException {
        String ext = pictureURL.substring(pictureURL.lastIndexOf("."));
        if (ext.length() > 4 && ext.equals(EXT_JPEG)) {
            ext = EXT_JPEG;
        } else {
            ext = EXT_JPG;
        }
        String pictureName = Math.abs(RANDOM.nextInt()) + ext;
        String localPicturePath = DIR + File.separator + pictureName;
        FileUtils.copyFromUrl(pictureURL, localPicturePath);
        movie.setPicturePath(localPicturePath);
    }
}
