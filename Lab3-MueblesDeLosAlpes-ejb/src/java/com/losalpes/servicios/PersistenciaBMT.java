/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.DataBaseException;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.EJB;
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

    @EJB
    private IServicioVendedoresMockLocal servicioVendedores;

    public void initTransaction() throws DataBaseException {
        try {
            ut.begin();
        } catch (NotSupportedException | SystemException ex) {
            throw new DataBaseException(ex);
        }
    }

    public void commitTransaction() throws DataBaseException {
        try {
            ut.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
            throw new DataBaseException(ex);
        }
    }

    public void rollBackTransaction() throws DataBaseException {
        try {
            ut.rollback();
        } catch (IllegalStateException | SecurityException | SystemException ex) {
            throw new DataBaseException(ex);
        }
    }

    @Override
    public void insertRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        String querySelect = "SELECT nombres FROM VENDEDORES WHERE identificacion = ?";
        String queryInsert = "INSERT INTO VENDEDORES (identificacion, nombres, apellidos) VALUES (?,?,?)";
        try {

            initTransaction();

            pstmtSelect = dataSource.getConnection().prepareStatement(querySelect);
            pstmtSelect.setString(1, String.valueOf(vendedor.getIdentificacion()));
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                rollBackTransaction();
                cerrarStatement(pstmtSelect);
                throw new OperacionInvalidaException("Ya existe el vendedor con identificacion " + vendedor.getIdentificacion());
            }
            pstmtInsert = dataSource.getConnection().prepareStatement(queryInsert);
            pstmtInsert.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtInsert.setString(2, vendedor.getNombres());
            pstmtInsert.setString(3, vendedor.getApellidos());
            pstmtInsert.executeUpdate();

            commitTransaction();

        } catch (DataBaseException | SQLException e) {
            e.printStackTrace(System.out);
            try {
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
        } finally {
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }

    @Override
    public void deleteRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException {

        PreparedStatement pstmtDelete = null;
        String queryDelete = "DELETE FROM VENDEDORES WHERE identificacion = ?";

        try {

            initTransaction();

            pstmtDelete = dataSource.getConnection().prepareStatement(queryDelete);
            pstmtDelete.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtDelete.executeUpdate();

            commitTransaction();

        } catch (DataBaseException | SQLException e) {
            e.printStackTrace(System.out);
            try {
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
        } finally {
            cerrarStatement(pstmtDelete);
        }
    }

    private void cerrarStatement(final PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
}
