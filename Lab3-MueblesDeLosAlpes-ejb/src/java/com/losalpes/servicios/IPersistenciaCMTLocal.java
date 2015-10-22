/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import javax.ejb.Local;

/**
 *
 * @author ad.lopez11
 */
@Local
public interface IPersistenciaCMTLocal {

    void insertRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException;

    void deleteRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException;
}
