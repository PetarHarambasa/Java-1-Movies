/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author User
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"Title", "Description", "Duration", "picturePath", "Genre", "Actors", "Director"})
public class Movie {

    @XmlAttribute
    private int id;
    private String Title;
    private String Description;
    private String picturePath;
    private int Duration;
    @XmlElementWrapper
    @XmlElement(name = "Genre")
    private List<Genre> Genre;
    @XmlElementWrapper
    @XmlElement(name = "Director")
    private List<Person> Director;
    @XmlElementWrapper
    @XmlElement(name = "Actors")
    private List<Person> Actors;

    public Movie() {
        Genre = new ArrayList<>();
        Actors = new ArrayList<>();
        Director = new ArrayList<>();
    }

    public Movie(int id, String Title, String Description, String picturePath, int Duration) {
        this(Title, Description, picturePath, Duration);
        this.id = id;
        Genre = new ArrayList<>();
        Actors = new ArrayList<>();
        Director = new ArrayList<>();
    }

    public Movie(String Title, String Description, String picturePath, int Duration) {
        this.Title = Title;
        this.Description = Description;
        this.picturePath = picturePath;
        this.Duration = Duration;
    }

    public Movie(int id, String Title, String Description, String picturePath, int Duration, List<Genre> Genre, List<Person> Director, List<Person> Actors) {
        this(id, Title, Description, picturePath, Duration);
        this.Genre = Genre;
        this.Director = Director;
        this.Actors = Actors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getDuration() {
        return Duration;
    }

    public void setDuration(int Duration) {
        this.Duration = Duration;
    }

    public List<Genre> getGenre() {
        return Genre;
    }

    public void setGenre(List<Genre> Genre) {
        this.Genre = Genre;
    }

    public List<Person> getDirector() {
        return Director;
    }

    public void setDirector(List<Person> Director) {
        this.Director = Director;
    }

    public List<Person> getActors() {
        return Actors;
    }

    public void setActors(List<Person> Actors) {
        this.Actors = Actors;
    }

    @Override
    public String toString() {
        return  Title + " (" + Duration  + "), " + Description;
    }

}
