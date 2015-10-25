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

import com.losalpes.entities.Ciudad;
import com.losalpes.entities.Mueble;
import com.losalpes.entities.Pais;
import com.losalpes.entities.Profesion;
import com.losalpes.entities.RegistroVenta;
import com.losalpes.entities.TipoDocumento;
import com.losalpes.entities.TipoMueble;
import com.losalpes.entities.TipoUsuario;
import com.losalpes.entities.Usuario;
import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.VendedorException;
import java.util.ArrayList;
import java.util.Date;
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
    private Mueble mueble1,mueble2,mueble3;
    private Usuario usuario;

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

            mueble1 = new Mueble(1L, "Silla clásica", "Una confortable silla con estilo del siglo XIX.", TipoMueble.Interior, 45, "", 123);
            mueble2 = new Mueble(2L, "Silla moderna", "Lo último en la moda de interiores. Esta silla le brindará la comodidad e innovación que busca", TipoMueble.Interior, 50, "", 5464);
            mueble3 = new Mueble(3L, "Mesa de jardín", "Una bella mesa para comidas y reuniones al aire libre.", TipoMueble.Exterior, 100, "", 4568);
            ServicioCatalogoMock instance = new ServicioCatalogoMock();
            instance.agregarMueble(mueble1);
            instance.agregarMueble(mueble2);
            instance.agregarMueble(mueble3);

            usuario = new Usuario();
            Ciudad ciudad = new Ciudad();
            ciudad.setNombre("Bogota");
            ArrayList ciudades = new ArrayList();
            ciudades.add(ciudad);
            Pais pais = new Pais();
            pais.setCiudades(ciudades);
            ciudad.setPais(pais);
            usuario.setCiudad(ciudad);
            usuario.setContraseña("1234");
            usuario.setCorreo("Correo");
            usuario.setDireccion("Calle 1");
            usuario.setDocumento(11111);
            usuario.setLogin("12456789");
            usuario.setNombreCompleto("Nombre");
            usuario.setProfesion(Profesion.Abogado);
            usuario.setSeleccion(true);
            usuario.setTelefonoCelular(32434324);
            usuario.setTelefonoLocal(675755);
            usuario.setTipoDocumento(TipoDocumento.CC);
            usuario.setTipoUsuario(TipoUsuario.Cliente);
            
            ServicioRegistroMock servicioRM = new ServicioRegistroMock();
            servicioRM.registrar(usuario);
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

    /**
     * Metodo para validar la compra
     *
     * @throws Exception
     */
    @Test
    public void testComprar1() throws Exception {
        
        RegistroVenta rv = new RegistroVenta();
        rv.setCantidad(1);
        rv.setCiudad("Bogota");
        rv.setComprador(usuario);
        rv.setFechaVenta(new Date());
        rv.setRegistro(11);
        rv.setProducto(mueble1);
        
    }

    /**
     * Metodo para comprar, probando la excepcion
     *
     * @throws Exception
     */
    @Test(expected = com.losalpes.excepciones.CupoInsuficienteException.class)
    public void testComprar2() throws Exception {
        RegistroVenta rv = new RegistroVenta();
        rv.setCantidad(1);
        rv.setCiudad("Bogota");
        rv.setComprador(usuario);
        rv.setFechaVenta(new Date());
        rv.setRegistro(11);
        rv.setProducto(mueble1);
        
        rv = new RegistroVenta();
        rv.setCantidad(10);
        rv.setCiudad("Bogota");
        rv.setComprador(usuario);
        rv.setFechaVenta(new Date());
        rv.setRegistro(11);
        rv.setProducto(mueble2);
        
        rv = new RegistroVenta();
        rv.setCantidad(10);
        rv.setCiudad("Bogota");
        rv.setComprador(usuario);
        rv.setFechaVenta(new Date());
        rv.setRegistro(11);
        rv.setProducto(mueble3);
        
    }

}
