/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

/**
 *
 * @author ad.lopez11
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PersistenciaBMT implements IPersistenciaBMTLocal, IPersistenciaBMTRemote {

    @Resource
    private UserTransaction ut;

    @Resource(mappedName = "jdbc/derbyDatasource")
    private DataSource dataSource;

    public void initTransaction() throws NotSupportedException, SystemException {
        ut.begin();
    }

    public void commitTransaction() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
        ut.commit();
    }

    public void rollBackTransaction() throws IllegalStateException, SecurityException, SystemException {
        ut.rollback();
    }

    public void insertRemoteDatabase(Vendedor vendedor) {
        // TODO vendedor
    }

    public void deleteRemoteDatabase(Vendedor vendedor) {
        // TODO vendedor
    }
}
