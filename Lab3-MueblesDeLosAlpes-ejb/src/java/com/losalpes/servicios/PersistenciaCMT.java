/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
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

    public void insertRemoteDatabase(Vendedor vendedor) throws OperacionInvalidaException {
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
                throw new OperacionInvalidaException("Ya existe el vendedor con identificacion " + vendedor.getIdentificacion());
            }
            pstmtInsert = dataSource.getConnection().prepareStatement(queryInsert);
            pstmtInsert.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtInsert.setString(2, vendedor.getNombres());
            pstmtInsert.setString(3, vendedor.getApellidos());
            pstmtInsert.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(System.out);
            sctx.setRollbackOnly();
        } finally {
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }

    public void deleteRemoteDatabase(Vendedor vendedor) {
        PreparedStatement pstmtDelete = null;
        String queryDelete = "DELETE FROM VENDEDORES WHERE identificacion = ?";

        try {

            pstmtDelete = dataSource.getConnection().prepareStatement(queryDelete);
            pstmtDelete.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace(System.out);
            sctx.setRollbackOnly();
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
