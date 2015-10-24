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
import java.sql.Date;
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

    @EJB
    private IServicioRegistroMockLocal servicioUsuario;

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
            //Ejecucion del rollback
            sctx.setRollbackOnly();
            e.printStackTrace(System.out);
        } finally {
            //Libera los recursos
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
            //Ejecucion del rollback
            sctx.setRollbackOnly();
            throw new VendedorException("No se puede insertar el vendedor");
        } finally {
            //Libera los recursos
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

            //Inserta el vendedor en la BD Oracle
            servicioVendedores.agregarVendedor(vendedor);

            //Inserta el vendedor en la BD Derby
            insertRemoteDatabase(vendedor);

        } catch (OperacionInvalidaException ex) {
            ex.printStackTrace(System.out);
            //Ejecucion del rollback
            sctx.setRollbackOnly();
        } catch (VendedorException ex) {
            //Ejecucion del rollback
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

            //Borra el vendedor de la BD Oracle
            servicioVendedores.eliminarVendedor(vendedor.getIdentificacion());

            //Borra el vendedor de la BD Derby
            deleteRemoteDatabase(vendedor);

        } catch (VendedorException | OperacionInvalidaException ex) {
            //Ejecucion del rollback
            sctx.setRollbackOnly();
            ex.printStackTrace(System.out);
            throw new VendedorException("No se puede insertar el vendedor");
        }
    }

    /**
     * Metodo para crear un cliente con su tarjeta
     *
     * @param usuario
     * @param tarjeta
     * @throws com.losalpes.excepciones.OperacionInvalidaException
     */
    @Override
    public void registrarUsarioTarjeta(final Usuario usuario, final TarjetaCreditoAlpes tarjeta) throws OperacionInvalidaException {
        try {

            //Crea un cliente de la BD Oracle
            servicioUsuario.registrar(usuario);

            //Crea la tarjeta en la BD Derby
            registarTarjetaCreditoAlpes(tarjeta);

        } catch (Exception ex) {
            //Ejecucion del rollback
            sctx.setRollbackOnly();
            ex.printStackTrace(System.out);
            throw new OperacionInvalidaException(ex.getMessage());
        }
    }

    /**
     * Metodo para crear una tarjeta en la base de datos derby
     *
     * @param usuario
     */
    private void registarTarjetaCreditoAlpes(final TarjetaCreditoAlpes tarjeta) throws OperacionInvalidaException {
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        String querySelect = "SELECT nombre_titular FROM TarjetaCreditoAlpes WHERE id = ?";
        String queryInsert = "INSERT INTO TarjetaCreditoAlpes (id, numero, nombre_titular, documento_titular, nombre_banco, cupo, saldo, fecha_expedicion, fecha_vencimiento) VALUES (?,?,?,?,?,?,?,?,?)";

        try {

            pstmtSelect = dataSource.getConnection().prepareStatement(querySelect);
            pstmtSelect.setString(1, tarjeta.getId());
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                sctx.setRollbackOnly();
                cerrarStatement(pstmtSelect);
                throw new OperacionInvalidaException("Ya existe la tarjeta con identificacion " + tarjeta.getId());
            }
            pstmtInsert = dataSource.getConnection().prepareStatement(queryInsert);
            pstmtInsert.setString(1, tarjeta.getId());
            pstmtInsert.setString(2, tarjeta.getNumero());
            pstmtInsert.setString(3, tarjeta.getNombreTitular());
            pstmtInsert.setLong(4, tarjeta.getDocumentoTitular());
            pstmtInsert.setString(5, tarjeta.getNombreBanco());
            pstmtInsert.setLong(6, tarjeta.getCupo());
            pstmtInsert.setLong(7, tarjeta.getSaldo());
            pstmtInsert.setDate(8, new Date(tarjeta.getFechaExpedicion().getTime()));
            pstmtInsert.setDate(9, new Date(tarjeta.getFechaVencimiento().getTime()));
            pstmtInsert.executeUpdate();

        } catch (SQLException e) {
            //Ejecucion del rollback
            sctx.setRollbackOnly();
            e.printStackTrace(System.out);
            throw new OperacionInvalidaException("Error al crear la tarjeta de credito");
        } finally {
            //Libera los recursos
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }

    /**
     * Metodo que cierra el statement
     *
     * @param pstmt
     */
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
