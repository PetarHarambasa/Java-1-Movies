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
public class GenreTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Id", "Name"};
    private List<Genre> genres;

    public GenreTableModel(List<Genre> genres) {
        this.genres = genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return genres.size();
    }

    @Override
    public int getColumnCount() {
        return Genre.class.getDeclaredFields().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return genres.get(rowIndex).getId();
            case 1:
                return genres.get(rowIndex).getName();
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
