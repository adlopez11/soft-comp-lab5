/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.losalpes.servicios;

import com.losalpes.entities.TarjetaCreditoAlpes;
import com.losalpes.entities.Usuario;
import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.DataBaseException;
import com.losalpes.excepciones.OperacionInvalidaException;
import com.losalpes.excepciones.VendedorException;
import java.sql.Date;
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

    @EJB
    private IServicioRegistroMockLocal servicioUsuario;

    /**
     * Metodo que inicia la transaccion
     *
     * @throws DataBaseException
     */
    public void initTransaction() throws DataBaseException {
        try {
            ut.begin();
        } catch (NotSupportedException | SystemException ex) {
            throw new DataBaseException(ex);
        }
    }

    /**
     * Metodo que hace commit a la transaccion
     *
     * @throws DataBaseException
     */
    public void commitTransaction() throws DataBaseException {
        try {
            ut.commit();
        } catch (RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException | SystemException ex) {
            throw new DataBaseException(ex);
        }
    }

    /**
     * Metodo que hace rollback a la transaccion
     *
     * @throws DataBaseException
     */
    public void rollBackTransaction() throws DataBaseException {
        try {
            ut.rollback();
        } catch (IllegalStateException | SecurityException | SystemException ex) {
            throw new DataBaseException(ex);
        }
    }

    /**
     * Metodo para persisitir un vendedor en la base de datos Derby, incluye el
     * manejo de la transaccion
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void insertRemoteDatabase(Vendedor vendedor) throws VendedorException {

        try {

            //Inicia la transaccion
            initTransaction();

            //Agrega el vendedor a la BD Derby
            agregarVendedorDerby(vendedor);

            //Hace commit de la transaccion
            commitTransaction();

        } catch (SQLException | DataBaseException e) {
            e.printStackTrace(System.out);
            try {
                //Hace rollback de la transaccion
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
        } catch (VendedorException e) {
            try {
                //Hace rollback de la transaccion
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
            throw e;
        }
    }

    /**
     * Metodo para persisitir un vendedor en la base de datos Derby
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     * @throws java.sql.SQLException
     */
    private void agregarVendedorDerby(Vendedor vendedor) throws VendedorException, SQLException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;

        //Sentencias
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
            //Se liberan los recursos
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }

    /**
     * Cierra el statement
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

    /**
     * Metodo para eliminar un vendedor en la base de datos Derby, incluye el
     * manejo de la transaccion
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.VendedorException
     */
    @Override
    public void deleteRemoteDatabase(Vendedor vendedor) throws VendedorException {

        try {

            //Inicia la transaccion
            initTransaction();

            //Elimina el vendedor de la BD Derby
            deleteVendedorDerby(vendedor);

            //Hace commit de la transaccion
            commitTransaction();

        } catch (DataBaseException | SQLException e) {
            e.printStackTrace(System.out);
            try {
                //Hace rollback de la transaccion
                rollBackTransaction();
            } catch (DataBaseException ex) {
                ex.printStackTrace(System.out);
            }
            throw new VendedorException("Error al eliminar el vendedor");
        }

    }

    /**
     * Metodo para eliminar un vendedor en la base de datos Derby sin
     * transacciones
     *
     * @param vendedor
     * @throws java.sql.SQLException
     */
    private void deleteVendedorDerby(Vendedor vendedor) throws SQLException {

        PreparedStatement pstmtDelete = null;
        String queryDelete = "DELETE FROM VENDEDORES WHERE identificacion = ?";

        try {

            pstmtDelete = dataSource.getConnection().prepareStatement(queryDelete);
            pstmtDelete.setString(1, String.valueOf(vendedor.getIdentificacion()));
            pstmtDelete.executeUpdate();

        } catch (SQLException e) {
            throw e;
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

            //Inicia la transaccion
            initTransaction();

            //Agrega el vendedor en la BD Oracle
            servicioVendedores.agregarVendedor(vendedor);

            //Agrega el vendedor en la BD Derby
            agregarVendedorDerby(vendedor);

            //Termina la transaccion
            commitTransaction();

        } catch (SQLException | OperacionInvalidaException | DataBaseException ex) {
            ex.printStackTrace(System.out);
            try {
                //Se reversa la transaccion
                rollBackTransaction();
            } catch (DataBaseException ex1) {
                ex1.printStackTrace(System.out);
            }
        } catch (VendedorException ex) {
            try {
                //Se reversa la transaccion
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

            //Inicia la transaccion
            initTransaction();

            //Borra el vendedor de la BD Oracle
            servicioVendedores.eliminarVendedor(vendedor.getIdentificacion());

            //Borra el vendedor de la BD Derby
            deleteVendedorDerby(vendedor);

            //Termina la transaccion
            commitTransaction();

        } catch (SQLException | OperacionInvalidaException | DataBaseException ex) {
            ex.printStackTrace(System.out);
            try {
                //Hace rollback
                rollBackTransaction();
            } catch (DataBaseException ex1) {
                ex1.printStackTrace(System.out);
            }
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

            //Inicia la transaccion
            initTransaction();

            //Crea un cliente de la BD Oracle
            servicioUsuario.registrar(usuario);

            //Crea la tarjeta en la BD Derby
            registarTarjetaCreditoAlpes(tarjeta);

            //Termina la transaccion
            commitTransaction();

        } catch (DataBaseException | OperacionInvalidaException | SQLException ex) {
            //Ejecucion del rollback
            try {
                //Hace rollback
                rollBackTransaction();
            } catch (DataBaseException ex1) {
                ex1.printStackTrace(System.out);
            }
            ex.printStackTrace(System.out);
            throw new OperacionInvalidaException(ex.getMessage());
        }
    }

    /**
     * Metodo para crear una tarjeta en la base de datos derby
     *
     * @param usuario
     */
    private void registarTarjetaCreditoAlpes(final TarjetaCreditoAlpes tarjeta) throws OperacionInvalidaException, SQLException {
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        String querySelect = "SELECT nombre_titular FROM TarjetaCreditoAlpes WHERE id = ?";
        String queryInsert = "INSERT INTO TarjetaCreditoAlpes (id, numero, nombre_titular, documento_titular, nombre_banco, cupo, saldo, fecha_expedicion, fecha_vencimiento) VALUES (?,?,?,?,?,?,?,?,?)";

        try {

            pstmtSelect = dataSource.getConnection().prepareStatement(querySelect);
            pstmtSelect.setString(1, tarjeta.getId());
            ResultSet rs = pstmtSelect.executeQuery();
            if (rs.next()) {
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
            e.printStackTrace(System.out);
            throw e;
        } finally {
            //Libera los recursos
            cerrarStatement(pstmtSelect);
            cerrarStatement(pstmtInsert);
        }
    }
}
