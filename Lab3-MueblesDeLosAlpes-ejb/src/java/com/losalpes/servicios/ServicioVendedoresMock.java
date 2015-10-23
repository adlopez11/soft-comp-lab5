/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id$ ServicioVendedoresMock.java
 * Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación
 * Licenciado bajo el esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 * 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package com.losalpes.servicios;

import com.losalpes.entities.Vendedor;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Implementación de los servicios de administración de un vendedor en el sistema
 * 
 */
@Stateless
public class ServicioVendedoresMock implements IServicioVendedoresMockRemote, IServicioVendedoresMockLocal {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    
    
    
    @EJB
    private IPersistenciaCMTLocal persistencia;
    
    /**
     * Interface con referencia al servicio de persistencia en el sistema
     *
    */
    @EJB
    private IServicioPersistenciaMockLocal persistencia2;

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------

    /**
     * Constructor de la clase sin argumentos
     */
    public ServicioVendedoresMock()
    {
    }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------

    /**
     * Agrega un vendedor al sistema
     * @param vendedor Nuevo vendedor
     * @throws com.losalpes.excepciones.OperacionInvalidaException
     */
    @Override
    public void agregarVendedor(Vendedor vendedor) throws OperacionInvalidaException 
    {
        
        try
        {
            persistencia2.create(vendedor);
        }
        catch (OperacionInvalidaException ex)
        {
            throw new OperacionInvalidaException(ex.getMessage());
        }
        /*
        try
        {
            persistencia.insertLocalRemoteDatabase(vendedor);
            
        } catch (VendedorException ex) {
          ex.printStackTrace(System.out);
        }*/
    }

    /**
     * Elimina un vendedor del sistema dado su ID
     * @param id Identificador único del vendedor
     * @throws OperacionInvalidaException Excepción lanzada en caso de error
     */
    @Override
    public void eliminarVendedor(long id) throws OperacionInvalidaException
    {
       Vendedor v=(Vendedor) persistencia2.findById(Vendedor.class, id);
        try
        {
            persistencia2.delete(v);
        } catch (OperacionInvalidaException ex)
        {
            throw new OperacionInvalidaException(ex.getMessage());
        }
        
        /* Vendedor v = new Vendedor();
        v.setIdentificacion(id);
        
        try {
            persistencia.deleteLocalRemoteDatabase(v);
        } catch (VendedorException ex) {
             ex.printStackTrace(System.out);
        }*/
        
    }

    /**
     * Devuelve todos los vendedores del sistema
     * @return vendedores Vendedores del sistema
     */
    @Override
    public List<Vendedor> getVendedores()
    {
        return persistencia2.findAll(Vendedor.class);
    }

}
