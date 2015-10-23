/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.DataBaseException;
import com.losalpes.excepciones.OperacionInvalidaException;
import com.losalpes.excepciones.VendedorException;
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

    /**
     * Metodo para persisitir un vendedor en la base de datos Derby con
     * transacciones
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void insertRemoteDatabase(Vendedor vendedor) throws VendedorException {

        try {

            initTransaction();

            insertRemoteDatabaseRaw(vendedor);

            commitTransaction();

        } catch (SQLException | DataBaseException e) {
            e.printStackTrace(System.out);
            try {
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
        } catch (VendedorException e) {
            try {
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
            throw e;
        }
    }

    /**
     * Metodo para eliminar un vendedor en la base de datos Derby, se debe
     * manejar la transaccion manualmente de forma externa para que metodo
     * funcione.
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void deleteRemoteDatabase(Vendedor vendedor) throws VendedorException {

        try {

            initTransaction();

            deleteRemoteDatabaseRaw(vendedor);

            commitTransaction();

        } catch (DataBaseException | SQLException e) {
            e.printStackTrace(System.out);
            try {
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
            throw new VendedorException("Error al eliminar el vendedor");
        }

    }

    /**
     * Metodo para persisitir un vendedor en ambas bases de datos
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void insertLocalRemoteDatabase(final Vendedor vendedor) throws VendedorException {
        try {

            initTransaction();

            servicioVendedores.agregarVendedor(vendedor);

            insertRemoteDatabaseRaw(vendedor);

            commitTransaction();

        } catch (SQLException | OperacionInvalidaException | DataBaseException ex) {
            ex.printStackTrace(System.out);
            try {
                rollBackTransaction();
            } catch (DataBaseException ex1) {
                ex1.printStackTrace(System.out);
            }
        } catch (VendedorException ex) {
            try {
                rollBackTransaction();
            } catch (DataBaseException ex1) {
                ex1.printStackTrace(System.out);
            }
            throw ex;
        }
    }

    /**
     * Metodo para eliminar un vendedor de ambas bases de datos
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void deleteLocalRemoteDatabase(final Vendedor vendedor) throws VendedorException {

        try {

            initTransaction();

            servicioVendedores.eliminarVendedor(vendedor.getIdentificacion());

            deleteRemoteDatabaseRaw(vendedor);

            commitTransaction();

        } catch (SQLException | OperacionInvalidaException | DataBaseException ex) {
            ex.printStackTrace(System.out);
            try {
                rollBackTransaction();
            } catch (DataBaseException ex1) {
                ex1.printStackTrace(System.out);
            }
            throw new VendedorException("No se puede insertar el vendedor");
        }
    }

    /**
     * Metodo para persisitir un vendedor en la base de datos Derby sin
     * transacciones
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     * @throws java.sql.SQLException
     */
    private void insertRemoteDatabaseRaw(Vendedor vendedor) throws VendedorException, SQLException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        String querySelect = "SELECT nombres FROM VENDEDORES WHERE identificacion = ?";
        String queryInsert = "INSERT INTO VENDEDORES (identificacion, nombres, apellidos) VALUES (?,?,?)";
        try {

            pstmtSelect = dataSource.getConnection().prepareStatement(querySelect);
            pstmtSelect.setString(1, String.valueOf(vendedor.getIdentificacion()));
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                cerrarStatement(pstmtSelect);
                throw new VendedorException("Ya existe el vendedor con identificacion " + vendedor.getIdentificacion());
            }
            pstmtInsert = dataSource.getConnection().prepareStatement(queryInsert);
            pstmtInsert.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtInsert.setString(2, vendedor.getNombres());
            pstmtInsert.setString(3, vendedor.getApellidos());
            pstmtInsert.executeUpdate();

        } catch (SQLException e) {
            throw e;
        } finally {
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }

    /**
     * Metodo para eliminar un vendedor en la base de datos Derby sin
     * transacciones
     *
     * @param vendedor
     * @throws java.sql.SQLException
     */
    private void deleteRemoteDatabaseRaw(Vendedor vendedor) throws SQLException {

        PreparedStatement pstmtDelete = null;
        String queryDelete = "DELETE FROM VENDEDORES WHERE identificacion = ?";

        try {

            pstmtDelete = dataSource.getConnection().prepareStatement(queryDelete);
            pstmtDelete.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            throw e;
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
