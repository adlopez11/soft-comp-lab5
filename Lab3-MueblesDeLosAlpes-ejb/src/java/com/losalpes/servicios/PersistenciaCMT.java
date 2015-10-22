/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

/**
 *
 * @author ad.lopez11
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PersistenciaCMT implements IPersistenciaCMTLocal, IPersistenciaCMTRemote {

    @Resource
    private SessionContext sctx;

    @Resource(mappedName = "jdbc/derbyDatasource")
    private DataSource dataSource;

    public void insertRemoteDatabase(Vendedor vendedor) {
        // TODO:
    }

    public void deleteRemoteDatabase(Vendedor vendedor) {
        // TODO:
    }
}
