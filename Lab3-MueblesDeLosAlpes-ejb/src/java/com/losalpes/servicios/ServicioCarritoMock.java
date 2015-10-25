/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ $Id$
 * ServicioCarritoMock.java Universidad de los Andes (Bogotá - Colombia)
 * Departamento de Ingeniería de Sistemas y Computación Licenciado bajo el
 * esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles de los Alpes
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.servicios;

import com.losalpes.entities.Mueble;
import com.losalpes.entities.RegistroVenta;
import com.losalpes.entities.Usuario;
import com.losalpes.excepciones.OperacionInvalidaException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;

/**
 * Implementacion de los servicios del carrito de compras en el sistema.
 *
 */
@Stateful
public class ServicioCarritoMock implements IServicioCarritoMockRemote, IServicioCarritoMockLocal {
    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------

    /**
     * Interface con referencia al servicio de persistencia en el sistema
     */
    @EJB
    private IServicioPersistenciaMockLocal persistencia;

    /**
     * Lista con los muebles del carrito
     */
    private ArrayList<RegistroVenta> inventario;

    /**
     * Precio total de todo el inventario
     */
    private double precioTotalInventario = 0.0;

    /**
     * Total de unidades en el inventario del carrito
     */
    private int totalUnidades = 0;

    //-----------------------------------------------------------
    // Constructor
    //-----------------------------------------------------------
    /**
     * Constructor sin argumentos de la clase
     */
    public ServicioCarritoMock() {
        inventario = new ArrayList<RegistroVenta>();
    }

    //-----------------------------------------------------------
    // Getters y setters
    //-----------------------------------------------------------
    /**
     * Devuelve el inventario de muebles que se encuentran en el carrito
     *
     * @return inventario Lista con los muebles que se encuentran en el carrito
     */
    @Override
    public ArrayList<RegistroVenta> getInventario() {
        return inventario;
    }

    /**
     * Modifica el inventario del carrito
     *
     * @param inventario Nueva lista de muebles
     */
    @Override
    public void setInventario(ArrayList<RegistroVenta> inventario) {
        this.inventario = inventario;
    }

    /**
     * Devuelve el precio total del inventario
     *
     * @return precioTotalInventario Precio total del inventario
     */
    @Override
    public double getPrecioTotalInventario() {
        return precioTotalInventario;
    }

    /**
     * Devuelve el cantidad total de unidades en el carrito
     *
     * @return totalUnidades Cantidad total de unidades en el carrito
     */
    @Override
    public int getTotalUnidades() {
        return totalUnidades;
    }

    //-----------------------------------------------------------
    // Métodos
    //-----------------------------------------------------------
    /**
     * Realiza la compra de los items que se encuentran en el carrito
     *
     * @param usuario Usuario que realiza la compra
     */
    @Override
    public void comprar(Usuario usuario) {
        comprar(inventario, usuario);
    }

    /**
     * Realiza la compra de los items que se envian como parametro
     *
     * @param ventas
     * @param usuario Usuario que realiza la compra
     */
    @Override
    public void comprar(List<RegistroVenta> ventas, Usuario usuario) {

        for (RegistroVenta item : ventas) {

            Mueble muebleEditar = (Mueble) persistencia.findById(Mueble.class, item.getProducto().getReferencia());
            muebleEditar.setCantidad(muebleEditar.getCantidad() - item.getCantidad());
            item.setComprador(usuario);
            item.setCiudad(usuario.getCiudad().getNombre());
            item.setFechaVenta(new Date());
            item.setProducto(muebleEditar);
            try {
                persistencia.create(item);
            } catch (OperacionInvalidaException ex) {
                ex.printStackTrace(System.out);
            }

            usuario.agregarRegistro(item);

            persistencia.update(usuario);
            persistencia.update(muebleEditar);
        }
        limpiarLista();
    }

    /**
     * Agrega un nuevo mueble al carro de compras
     *
     * @param mueble Mueble que se agrega al carrito
     */
    @Override
    public void agregarItem(Mueble mueble) {
        boolean found = false;
        RegistroVenta item;
        for (int i = 0, max = inventario.size(); i < max; i++) {
            item = (RegistroVenta) inventario.get(i);
            if (item.getProducto().getReferencia() == mueble.getReferencia()) {
                item.incrementarCantidad();
                found = true;
                break;
            }
        }

        // Si el item no se encuentra se agrega al inventario
        if (!found) {
            RegistroVenta compra = new RegistroVenta(null, mueble, 1, null, null);
            inventario.add(compra);
        }

        // Actualiza el inventario
        recalcularInventarioTotal();
    }

    /**
     * Remueve un mueble del carrito de compra
     *
     * @param mueble Mueble a remover
     * @param removerCero Indica si al ser cero se elimina de la lista
     */
    @Override
    public void removerItem(Mueble mueble, boolean removerCero) {

        RegistroVenta foundItem = null;
        RegistroVenta item;
        for (int i = 0, max = inventario.size(); i < max; i++) {
            item = (RegistroVenta) inventario.get(i);
            if (item.getProducto().getReferencia() == mueble.getReferencia()) {
                item.reducirCantidad();
                foundItem = item;
                break;
            }
        }

        // Remueve el item si la cantidad es menor o igual a cero
        if (removerCero && foundItem != null
                && foundItem.getCantidad() <= 0) {
            inventario.remove(foundItem);
        }

        // Actualiza el inventario
        recalcularInventarioTotal();
    }

    /**
     * Recalcula el costo y la cantidad de inventario
     */
    @Override
    public void recalcularInventarioTotal() {
        precioTotalInventario = 0;
        totalUnidades = 0;
        RegistroVenta item;
        for (int i = 0, max = inventario.size(); i < max; i++) {
            item = (RegistroVenta) inventario.get(i);
            precioTotalInventario += item.getProducto().getPrecio() * item.getCantidad();
            totalUnidades += item.getCantidad();
        }
    }

    /**
     * Limpia el carrito de compras
     */
    @Override
    public void limpiarLista() {
        inventario.clear();
    }

}
