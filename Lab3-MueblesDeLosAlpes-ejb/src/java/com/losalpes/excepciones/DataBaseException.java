/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ $Id$
 * OperacionInvalidaException.java Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación Licenciado bajo el
 * esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.excepciones;

/**
 * Clase de excepción que se presenta cuando se viola alguna restricción de base
 * de datos
 *
 */
public class DataBaseException extends Exception {

    // -----------------------------------------------
    // Constructor
    // -----------------------------------------------
    /**
     * Constructor de la clase.
     *
     * @param mensaje Mensaje de la excepción
     */
    public DataBaseException(final String mensaje) {
        super(mensaje);
    }

    public DataBaseException(final Exception exception) {
        super(exception);
    }
}
