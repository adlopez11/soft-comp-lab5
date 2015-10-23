/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import com.losalpes.excepciones.VendedorException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;

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

    @EJB
    private IServicioVendedoresMockLocal servicioVendedores;

    /**
     * Metodo para insertar un vendedor en la base de datos Derby.
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void insertRemoteDatabase(Vendedor vendedor) throws VendedorException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        String querySelect = "SELECT nombres FROM VENDEDORES WHERE identificacion = ?";
        String queryInsert = "INSERT INTO VENDEDORES (identificacion, nombres, apellidos) VALUES (?,?,?)";
        try {

            pstmtSelect = dataSource.getConnection().prepareStatement(querySelect);
            pstmtSelect.setString(1, String.valueOf(vendedor.getIdentificacion()));
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                sctx.setRollbackOnly();
                cerrarStatement(pstmtSelect);
                throw new VendedorException("Ya existe el vendedor con identificacion " + vendedor.getIdentificacion());
            }
            pstmtInsert = dataSource.getConnection().prepareStatement(queryInsert);
            pstmtInsert.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtInsert.setString(2, vendedor.getNombres());
            pstmtInsert.setString(3, vendedor.getApellidos());
            pstmtInsert.executeUpdate();

        } catch (SQLException e) {
            sctx.setRollbackOnly();
            e.printStackTrace(System.out);
        } finally {
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }

    /**
     * Metodo para eliminar un vendedor en la base de datos Derby
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void deleteRemoteDatabase(Vendedor vendedor) throws VendedorException {
        PreparedStatement pstmtDelete = null;
        String queryDelete = "DELETE FROM VENDEDORES WHERE identificacion = ?";

        try {

            pstmtDelete = dataSource.getConnection().prepareStatement(queryDelete);
            pstmtDelete.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            sctx.setRollbackOnly();
            throw new VendedorException("No se puede insertar el vendedor");
        } finally {
            cerrarStatement(pstmtDelete);
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

            servicioVendedores.agregarVendedor(vendedor);

            insertRemoteDatabase(vendedor);

        } catch (OperacionInvalidaException ex) {
            ex.printStackTrace(System.out);
            sctx.setRollbackOnly();
        } catch (VendedorException ex) {
            sctx.setRollbackOnly();
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

            servicioVendedores.eliminarVendedor(vendedor.getIdentificacion());

            deleteRemoteDatabase(vendedor);

        } catch (VendedorException | OperacionInvalidaException ex) {
            sctx.setRollbackOnly();
            ex.printStackTrace(System.out);
            throw new VendedorException("No se puede insertar el vendedor");
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
