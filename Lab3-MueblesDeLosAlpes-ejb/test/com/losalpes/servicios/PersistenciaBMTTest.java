/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ $Id$
 * ServicioVendedoresMockTest.java Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación Licenciado bajo el
 * esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.VendedorException;
import java.util.Properties;
import javax.naming.InitialContext;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 * Clase encargada de realizar pruebas unitarias
 *
 */
public class PersistenciaBMTTest {
    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------

    /**
     * Interface con referencia al servicio de vendedores en el sistema
     */
    private IPersistenciaBMTRemote servicio;

    //-----------------------------------------------------------
    // Métodos de inicialización y terminación
    //-----------------------------------------------------------
    /**
     * Método que se ejecuta antes de comenzar la prueba unitaria Se encarga de
     * inicializar todo lo necesario para la prueba
     */
    @Before
    public void setUp() throws Exception {
        try {
            Properties env = new Properties();
            env.put("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
            env.put("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
            env.put("org.omg.CORBA.ORBInitialPort", "3700");
            InitialContext contexto;
            contexto = new InitialContext(env);
            servicio = (IPersistenciaBMTRemote) contexto.lookup("com.losalpes.servicios.IPersistenciaBMTRemote");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Prueba para insertar y borrar un registro en la base de datos derby
     *
     * @throws Exception
     */
    @Test
    public void testInsertRemoteDatabase1() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdentificacion(1l);
        vendedor.setNombres("Andres");
        vendedor.setApellidos("Castro");

        servicio.insertRemoteDatabase(vendedor);

        servicio.deleteRemoteDatabase(vendedor);
    }

    /**
     * Prueba para verificar que no inserte en la base de datos derby si ya
     * existe el vendedor
     *
     * @throws Exception
     */
    @Test
    public void testInsertRemoteDatabase2() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdentificacion(1l);
        vendedor.setNombres("Andres");
        vendedor.setApellidos("Castro");
        vendedor.setComisionVentas(100l);
        vendedor.setPerfil("Profesionakl");
        vendedor.setSalario(1000000l);
        servicio.insertRemoteDatabase(vendedor);

        boolean registroRepetido;
        try {
            // Intenta nuevamente insertar y debería generar una excepción
            servicio.insertRemoteDatabase(vendedor);
            registroRepetido = true;
        } catch (VendedorException ex) {
            registroRepetido = false;
        }

        servicio.deleteRemoteDatabase(vendedor);

        assertFalse(registroRepetido);
    }

    /**
     * Prueba para insertar y borrar en ambas bases de datos el vendedor
     *
     * @throws Exception
     */
    @Test
    public void testInsertLocalRemoteDatabase1() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdentificacion(1l);
        vendedor.setNombres("Andres");
        vendedor.setApellidos("Castro");
        vendedor.setComisionVentas(100l);
        vendedor.setPerfil("Profesional");
        vendedor.setSalario(1000000l);
        servicio.insertLocalRemoteDatabase(vendedor);

        servicio.deleteLocalRemoteDatabase(vendedor);
    }

    /**
     * Prueba para verificar que no inserte en la base de datos derby si ya
     * existe el vendedor
     *
     * @throws Exception
     */
    @Test
    public void testInsertLocalRemoteDatabase2() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdentificacion(1l);
        vendedor.setNombres("Andres");
        vendedor.setApellidos("Castro");
        vendedor.setComisionVentas(100l);
        vendedor.setPerfil("Profesionakl");
        vendedor.setSalario(1000000l);
        servicio.insertLocalRemoteDatabase(vendedor);

        boolean registroRepetido;
        try {
            // Intenta nuevamente insertar y debería generar una excepción
            servicio.insertLocalRemoteDatabase(vendedor);
            registroRepetido = true;
        } catch (VendedorException ex) {
            registroRepetido = false;
        }

        servicio.deleteLocalRemoteDatabase(vendedor);

        assertFalse(registroRepetido);
    }

}
