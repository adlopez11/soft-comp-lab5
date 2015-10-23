/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ $Id$
 * CupoInsuficienteException.java Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación Licenciado bajo el
 * esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.excepciones;

import javax.ejb.ApplicationException;

/**
 * Clase de excepción que se presenta cuando no hay cupo suficiente en la 
 * tarjeta de credito
 *
 */
@ApplicationException(rollback=true)
public class CupoInsuficienteException extends Exception {

    // -----------------------------------------------
    // Constructor
    // -----------------------------------------------
    /**
     * Constructor de la clase.
     *
     * @param mensaje Mensaje de la excepción
     */
    public CupoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
