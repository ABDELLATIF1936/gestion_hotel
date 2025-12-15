package com.hotel.util;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Classe utilitaire pour configurer les tableaux de manière flexible.
 */
public class TableViewHelper {
    
    /**
     * Configure une colonne avec des largeurs flexibles.
     * 
     * @param column la colonne à configurer
     * @param minWidth largeur minimale
     * @param prefWidth largeur préférée
     * @param maxWidth largeur maximale (Double.MAX_VALUE pour illimité)
     * @param resizable si la colonne peut être redimensionnée par l'utilisateur
     */
    public static <T, S> void configureColumn(TableColumn<T, S> column, 
                                            double minWidth, 
                                            double prefWidth, 
                                            double maxWidth, 
                                            boolean resizable) {
        column.setMinWidth(minWidth);
        column.setPrefWidth(prefWidth);
        column.setMaxWidth(maxWidth);
        column.setResizable(resizable);
    }
    
    /**
     * Configure une colonne fixe (non redimensionnable, largeur fixe).
     * 
     * @param column la colonne à configurer
     * @param width largeur fixe
     */
    public static <T, S> void configureFixedColumn(TableColumn<T, S> column, double width) {
        configureColumn(column, width, width, width, false);
    }
    
    /**
     * Configure une colonne flexible (redimensionnable, peut s'étendre).
     * 
     * @param column la colonne à configurer
     * @param minWidth largeur minimale
     * @param prefWidth largeur préférée
     */
    public static <T, S> void configureFlexibleColumn(TableColumn<T, S> column, 
                                                      double minWidth, 
                                                      double prefWidth) {
        configureColumn(column, minWidth, prefWidth, Double.MAX_VALUE, true);
    }
    
    /**
     * Configure une colonne avec largeur préférée mais redimensionnable.
     * 
     * @param column la colonne à configurer
     * @param prefWidth largeur préférée
     */
    public static <T, S> void configureResizableColumn(TableColumn<T, S> column, double prefWidth) {
        configureColumn(column, 50, prefWidth, Double.MAX_VALUE, true);
    }
    
    /**
     * Configure le tableau pour qu'il s'adapte à la taille disponible.
     * 
     * @param tableView le tableau à configurer
     */
    public static <T> void configureFlexibleTableView(TableView<T> tableView) {
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    /**
     * Configure le tableau avec redimensionnement automatique des colonnes.
     * 
     * @param tableView le tableau à configurer
     */
    public static <T> void configureAutoResizeTableView(TableView<T> tableView) {
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    }
}


