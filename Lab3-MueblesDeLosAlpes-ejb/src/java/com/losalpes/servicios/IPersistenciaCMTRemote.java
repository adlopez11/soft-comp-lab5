/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.TarjetaCreditoAlpes;
import com.losalpes.entities.Usuario;
import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import com.losalpes.excepciones.VendedorException;
import javax.ejb.Remote;

/**
 *
 * @author ad.lopez11
 */
@Remote
public interface IPersistenciaCMTRemote {

    void insertRemoteDatabase(Vendedor vendedor) throws VendedorException;

    void deleteRemoteDatabase(Vendedor vendedor) throws VendedorException;

    void insertLocalRemoteDatabase(Vendedor vendedor) throws VendedorException;

    void deleteLocalRemoteDatabase(Vendedor vendedor) throws VendedorException;

    public void registrarUsarioTarjeta(Usuario usuario, TarjetaCreditoAlpes tarjeta) throws OperacionInvalidaException;
}
