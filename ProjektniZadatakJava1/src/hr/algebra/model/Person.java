/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

/**
 *
 * @author User
 */
public class Person implements Comparable<Person> {

    public int id;
    public String firstName;
    public String lastName;
    public String type;

    public Person() {
    }

    public Person(int id, String firstName, String lastName, String type) {
        this(id, firstName, lastName);
        this.type = type;
    }

    public Person(int id, String firstName, String lastName) {
        this(firstName, lastName);
        this.id = id;
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public int compareTo(Person o) {
        if (lastName.equals(o.lastName)) {
            return firstName.compareTo(o.firstName);
        }
        return lastName.compareTo(o.lastName);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
