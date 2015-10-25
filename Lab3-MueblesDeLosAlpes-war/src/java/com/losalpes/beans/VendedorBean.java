/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ $Id$
 * VendedorBean.java Universidad de los Andes (Bogotá - Colombia) Departamento
 * de Ingeniería de Sistemas y Computación Licenciado bajo el esquema Academic
 * Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.beans;

import com.losalpes.entities.ExperienciaVendedor;
import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import com.losalpes.excepciones.VendedorException;
import com.losalpes.servicios.IPersistenciaCMTLocal;
import com.losalpes.servicios.IServicioVendedoresMockLocal;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 * Managed Bean encargado de la administración de vendedores en el sistema
 *
 */
public class VendedorBean implements Serializable {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    /**
     * Relación con la interfaz que provee los servicios necesarios del vendedor
     */
    @EJB
    private IServicioVendedoresMockLocal servicio;

    /**
     * Relación con la interfaz que provee los servicios necesarios para
     * administrar ambas bases de datos
     */
    @EJB
    private IPersistenciaCMTLocal persistenciaCMTService;

    /**
     * Representa un nuevo vendedor a ingresar
     */
    private Vendedor vendedor;

    /**
     * Representa una nueva experiencia de vendedor
     */
    private ExperienciaVendedor experiencia;

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor sin argumentos de la clase
     */
    public VendedorBean() {
        vendedor = new Vendedor();
        experiencia = new ExperienciaVendedor();
    }

    //-----------------------------------------------------------
    // Getters y setters
    //-----------------------------------------------------------
    /**
     * Devuelve el objeto de vendedor actual
     *
     * @return vendedor Vendedor actual
     */
    public Vendedor getVendedor() {
        return vendedor;
    }

    /**
     * Devuelve todos los vendedores del sistema
     *
     * @return vendedores Lista con todos los vendedores del sistema
     */
    public List<Vendedor> getVendedores() {
        return servicio.getVendedores();
    }

    /**
     * Devuelve el objeto actual de experiencia vendedor
     *
     * @return experiencia Objeto de la experiencia del vendedor actual
     */
    public ExperienciaVendedor getExperiencia() {
        return experiencia;
    }

    /**
     * Modifica la experiencia del vendedor actual
     *
     * @param experiencia Nueva experiencia del vendedor
     */
    public void setExperiencia(ExperienciaVendedor experiencia) {
        this.experiencia = experiencia;
    }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------
    /**
     * Agrega un nuevo vendedor al sistema
     *
     * @throws com.losalpes.excepciones.OperacionInvalidaException
     */
    public void agregarVendedor() throws OperacionInvalidaException {
        try {
            persistenciaCMTService.insertLocalRemoteDatabase(vendedor);
            vendedor = new Vendedor();
            experiencia = new ExperienciaVendedor();
        } catch (VendedorException ex) {
            throw new OperacionInvalidaException(ex.getMessage());
        }
    }

    /**
     * Elimina una experiecia del vendedor si no se ha persistido
     *
     * @param experiencia
     * @throws com.losalpes.excepciones.OperacionInvalidaException
     */
    public void eliminarExsperiencia(ExperienciaVendedor experiencia) throws OperacionInvalidaException {
        vendedor.getExperiencia().remove(experiencia);
    }

    /**
     * Elimina un vendedor del sistema
     *
     * @param vendedor
     * @throws com.losalpes.excepciones.OperacionInvalidaException
     */
    public void eliminarVendedor(Vendedor vendedor) throws OperacionInvalidaException {
        try {
            persistenciaCMTService.deleteLocalRemoteDatabase(vendedor);
        } catch (VendedorException ex) {
            throw new OperacionInvalidaException(ex.getMessage());
        }
    }

    /**
     * Agrega un item de experiencia a lista de experiencia del vendedor
     */
    public void agregarItemExperiencia() {
        experiencia.setVendedor(vendedor);
        vendedor.setItemExperiencia(experiencia);
        experiencia = new ExperienciaVendedor();
    }
}
