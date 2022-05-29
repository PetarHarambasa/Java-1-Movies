/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.parsers.rss;

import java.util.Optional;

/**
 *
 * @author CROma
 */
public enum TagType {
    ITEM("item"),
    TITLE("title"),
    DESCRIPTION("description"),
    DIRECTOR("redatelj"),
    ACTORS("glumci"),
    DURATION("trajanje"),
    GENRE("zanr"),
    IMAGE("plakat");

    
    private final String name;

    private TagType(String name) {
        this.name = name;
    }
    static Optional<TagType> from(String qName) {
        for (TagType value : values()) {
            if (value.name.equals(qName)) {
                return  Optional.of(value);
            }
        }
        return  Optional.empty();
    }
}
