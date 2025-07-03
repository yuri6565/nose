/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Date;

/**
 *
 * @author ZenBook
 */
public class Ingresos {
    private int idAbono;
    private int numAbono;
    private Date fechaPago;
    private double monto;
    private String metodoPago;
    private String referencia;
    private String observacion;
    private int idPedido;

    public Ingresos() {
    }

    public Ingresos(int idAbono, int numAbono, Date fechaPago, double monto, String metodoPago, String referencia, String observacion, int idPedido) {
        this.idAbono = idAbono;
        this.numAbono = numAbono;
        this.fechaPago = fechaPago;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
        this.observacion = observacion;
        this.idPedido = idPedido;
    }

    public int getIdAbono() {
        return idAbono;
    }

    public void setIdAbono(int idAbono) {
        this.idAbono = idAbono;
    }

    public int getNumAbono() {
        return numAbono;
    }

    public void setNumAbono(int numAbono) {
        this.numAbono = numAbono;
    }

    public Date getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }
    
    
}

