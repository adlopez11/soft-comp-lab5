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
import com.losalpes.excepciones.OperacionInvalidaException;
import java.util.Properties;
import javax.naming.InitialContext;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

/**
 * Clase encargada de realizar pruebas unitarias
 *
 */
public class PersistenciaCMTTest {
    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------

    /**
     * Interface con referencia al servicio de vendedores en el sistema
     */
    private IPersistenciaCMTRemote servicio;

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
            servicio = (IPersistenciaCMTRemote) contexto.lookup("com.losalpes.servicios.IPersistenciaCMTRemote");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Test
    public void testInsertRemoteDatabase1() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdentificacion(1l);
        vendedor.setNombres("Andres");
        vendedor.setApellidos("Castro");
        servicio.insertRemoteDatabase(vendedor);
    }

    @Test
    public void testInsertRemoteDatabase2() throws Exception {
        Vendedor vendedor = new Vendedor();
        vendedor.setIdentificacion(2l);
        vendedor.setNombres("Andres");
        vendedor.setApellidos("Castro");
        servicio.insertRemoteDatabase(vendedor);

        boolean registroRepetido;
        try {
            // Intenta nuevamente insertar y debería generar una excepción
            servicio.insertRemoteDatabase(vendedor);
            registroRepetido = true;
        } catch (OperacionInvalidaException ex) {
            registroRepetido = false;
        }
        assertFalse(registroRepetido);
    }

    @Test
    public void deleteRemoteDatabase() throws Exception {
        Vendedor vendedor1 = new Vendedor();
        vendedor1.setIdentificacion(1l);
        vendedor1.setNombres("Andres");
        vendedor1.setApellidos("Castro");
        servicio.deleteRemoteDatabase(vendedor1);

        Vendedor vendedor2 = new Vendedor();
        vendedor2.setIdentificacion(2l);
        vendedor2.setNombres("Andres");
        vendedor2.setApellidos("Castro");
        servicio.deleteRemoteDatabase(vendedor2);
    }

}
