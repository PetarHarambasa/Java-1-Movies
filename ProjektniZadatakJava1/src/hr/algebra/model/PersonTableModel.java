/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.model;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author User
 */
public class PersonTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Id", "FirstName", "LastName", "Type"};
    private List<Person> persons;

    public PersonTableModel(List<Person> persons) {
        this.persons = persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @Override
    public int getRowCount() {
        return persons.size();
    }

    @Override
    public int getColumnCount() {
        return Person.class.getDeclaredFields().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return persons.get(rowIndex).id;
            case 1:
                return persons.get(rowIndex).firstName;
            case 2:
                return persons.get(rowIndex).lastName;
            case 3:
                return persons.get(rowIndex).type;
            default:
                throw new RuntimeException("No such column exists!");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
}
