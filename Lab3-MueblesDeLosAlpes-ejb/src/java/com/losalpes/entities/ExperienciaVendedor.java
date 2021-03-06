/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ $Id$
 * ExperienciaVendedor.java Universidad de los Andes (Bogota - Colombia)
 * Departamento de Ingenieria de Sistemas y Computacion Licenciado bajo el
 * esquema Academic Free License version 3.0
 *
 * Ejercicio: Muebles los Alpes
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package com.losalpes.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 * Clase que modela un ìtem de experiencia de vendedor.
 */
@Entity
public class ExperienciaVendedor implements Serializable {

    //-----------------------------------------------------------
    // Atributos
    //-----------------------------------------------------------
    /**
     * Identificador del item de experiencia.
     */
    @Id
    @SequenceGenerator(name = "seq_experiencia_vendedor", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_experiencia_vendedor")
    private long id;

    /**
     * Nombre de la empresa que ceritifica la experiencia laboral.
     */
    @Column(nullable = false)
    private String nombreEmpesa;

    /**
     * Cargo que el empleado ocupó en la empresa.
     */
    @Column(nullable = false)
    private String cargo;

    /**
     * Descripción de las funciones del cargo.
     */
    @Column(nullable = false)
    private String descripcion;

    /**
     * Año de terminación del vínculo laboral.
     */
    @Column(nullable = false)
    private int ano;

    @ManyToOne
    @JoinColumn(name = "id_vendedor", referencedColumnName = "id")
    private Vendedor vendedor;

    //-----------------------------------------------------------
    // Constructores
    //-----------------------------------------------------------
    /**
     * Constructor de la clase sin argumentos
     */
    public ExperienciaVendedor() {

    }

    /**
     * Constructor de la clase con argumentos
     *
     * @param id Identificador único de la experiencia
     * @param nombreEmpesa Nombre de la empresa
     * @param cargo Cargo ocupado
     * @param descripcion Descripción de las funcionaes del cargo
     * @param ano Año de terminación del vínculo laboral
     */
    public ExperienciaVendedor(long id, String nombreEmpesa, String cargo, String descripcion, int ano) {
        this.id = id;
        this.nombreEmpesa = nombreEmpesa;
        this.cargo = cargo;
        this.descripcion = descripcion;
        this.ano = ano;
    }

    //-----------------------------------------------------------
    // Getters y setters
    //-----------------------------------------------------------
    /**
     * Devuelve el identificador único del vendedor
     *
     * @return id Identificador del vendedor
     */
    public long getId() {
        return id;
    }

    /**
     * Modifica el identificador único del jugador
     *
     * @param id Nuevo identificador del vendedor
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Devuelve el año de terminación del vínculo laboral
     *
     * @return ano Año de terminación del vínculo laboral
     */
    public int getAno() {
        return ano;
    }

    /**
     * Modifica el año de terminación del vínculo laboral
     *
     * @param ano Nuevo año de terminación de vínculo
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * Devuelve el cargo del vendedor
     *
     * @return cargo Cargo del vendedor
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Modifica el cargo de un vendedor
     *
     * @param cargo Nuevo cargo
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Devuelve la descripción del vendedor
     *
     * @return descripcion Descripción del vendedor
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Modifica la descripción del vendedor
     *
     * @param descripcion Nueva descripción del vendedor
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Devuelve el nombre de la empresa que certifica la experiencia laboral del
     * vendedor
     *
     * @return nombreEmpresa Nombre de la empresa certificadora
     */
    public String getNombreEmpesa() {
        return nombreEmpesa;
    }

    /**
     * Modifica el nombre de la empresa que certifica la experiencia laboral del
     * vendedor
     *
     * @param nombreEmpesa Nuevo nombre de la empresa certificadora
     */
    public void setNombreEmpesa(String nombreEmpesa) {
        this.nombreEmpesa = nombreEmpesa;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public void setVendedor(Vendedor vendedor) {
        this.vendedor = vendedor;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 59 * hash + Objects.hashCode(this.nombreEmpesa);
        hash = 59 * hash + Objects.hashCode(this.cargo);
        hash = 59 * hash + Objects.hashCode(this.descripcion);
        hash = 59 * hash + this.ano;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExperienciaVendedor other = (ExperienciaVendedor) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.nombreEmpesa, other.nombreEmpesa)) {
            return false;
        }
        if (!Objects.equals(this.cargo, other.cargo)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        if (this.ano != other.ano) {
            return false;
        }
        return true;
    }

}
