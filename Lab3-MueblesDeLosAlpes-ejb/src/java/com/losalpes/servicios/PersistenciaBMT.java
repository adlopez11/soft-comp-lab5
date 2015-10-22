/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
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

    public void insertRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException {
        Statement stmt = null;
        String query = "SELECT nombres FROM VENDEDORES WHERE identificacion = '" + vendedor.getIdentificacion() + "'";
        try {
            initTransaction();
            stmt = dataSource.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                rollBackTransaction();
                throw new OperacionInvalidaException("Ya existe el vendedor con identificacion " + vendedor.getIdentificacion());
            } else {
                query = "INSERT INTO VENDEDORES VALUES ('" + vendedor.getIdentificacion() + "','" + vendedor.getNombres() + "','" + vendedor.getApellidos() + "')";
                stmt.executeUpdate(query);
                commitTransaction();
            }

        } catch (SQLException | NotSupportedException | SystemException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException |
                SecurityException | IllegalStateException ex) {
            try {
                rollBackTransaction();
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                ex.printStackTrace(System.out);
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace(System.out);
                }
            }
        }
    }

    public void deleteRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException {
        Statement stmt = null;
        try {
            initTransaction();
            stmt = dataSource.getConnection().createStatement();

            String query = "DELETE FROM VENDEDORES WHERE identificacion = '" + vendedor.getIdentificacion() + "'";
            stmt.executeUpdate(query);
            commitTransaction();

        } catch (Exception ex) {
            try {
                rollBackTransaction();
                throw new OperacionInvalidaException("Error: No se puede eliminar vendedor con identificacion " + vendedor.getIdentificacion());
            } catch (IllegalStateException | SecurityException | SystemException ex1) {
                ex.printStackTrace(System.out);
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    ex.printStackTrace(System.out);
                }
            }
        }
    }
}
