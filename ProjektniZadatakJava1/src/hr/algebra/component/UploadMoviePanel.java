/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.component;

import hr.algebra.dal.Repository;
import hr.algebra.dal.RepositoryFactory;
import hr.algebra.model.Movie;
import hr.algebra.model.MovieCollection;
import hr.algebra.parsers.rss.MovieParser;
import hr.algebra.utils.MessageUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author CROma
 */
public class UploadMoviePanel extends javax.swing.JPanel {

    private DefaultListModel<Movie> movieModel;
    private static final String IMAGEFILE = "assets\\movies";
    private static final String FILENAME = "movie.xml";
    private Repository repository;

    /**
     * Creates new form UploadMoviePanel
     */
    public UploadMoviePanel() {
        initComponents();
        init();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        lsMovie = new javax.swing.JList<>();
        btnXMLDownload = new javax.swing.JButton();
        btnUploadMovie = new javax.swing.JButton();
        btnDeleteMovies = new javax.swing.JButton();

        jScrollPane1.setViewportView(lsMovie);

        btnXMLDownload.setText("XML download");
        btnXMLDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXMLDownloadActionPerformed(evt);
            }
        });

        btnUploadMovie.setText("Upload movies");
        btnUploadMovie.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadMovieActionPerformed(evt);
            }
        });

        btnDeleteMovies.setText("Delete movies");
        btnDeleteMovies.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteMoviesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(btnXMLDownload, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUploadMovie, javax.swing.GroupLayout.DEFAULT_SIZE, 971, Short.MAX_VALUE)
                    .addComponent(btnDeleteMovies, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnUploadMovie, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDeleteMovies, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnXMLDownload, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnUploadMovieActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadMovieActionPerformed
        try {
            List<Movie> movies = MovieParser.parse();
            if (0 != movies.size()) {
                repository.createMovies(movies);
                loadModel();
            }
        } catch (Exception ex) {
            Logger.getLogger(UploadMoviePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnUploadMovieActionPerformed

    private void btnDeleteMoviesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteMoviesActionPerformed
        try {
            repository.deleteMovies();
            loadModel();
            Files.walk(Paths.get(IMAGEFILE))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception ex) {
            Logger.getLogger(UploadMoviePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDeleteMoviesActionPerformed

    private void btnXMLDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXMLDownloadActionPerformed
        try {
            save(new MovieCollection(repository.selectMovie()), FILENAME);
        } catch (Exception ex) {
            Logger.getLogger(UploadMoviePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }//GEN-LAST:event_btnXMLDownloadActionPerformed

    private void init() {
        try {
            repository = RepositoryFactory.getRepository();
            movieModel = new DefaultListModel<>();
            loadModel();
        } catch (Exception e) {
            MessageUtils.showErrorMessage("Crazy error", "Cannot initiate form");
            System.exit(1);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDeleteMovies;
    private javax.swing.JButton btnUploadMovie;
    private javax.swing.JButton btnXMLDownload;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<Movie> lsMovie;
    // End of variables declaration//GEN-END:variables

    private void loadModel() {
        try {
            List<Movie> movies = repository.selectMovie();
            movieModel.clear();
            movies.forEach(movie -> movieModel.addElement(movie));
            lsMovie.setModel(movieModel);
        } catch (Exception ex) {
            Logger.getLogger(UploadMoviePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void save(MovieCollection movieCollection, String file) throws JAXBException {
        JAXBContext aXBContext = JAXBContext.newInstance(MovieCollection.class);
        Marshaller marshaller = aXBContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(movieCollection, new File(file));
    }
}
