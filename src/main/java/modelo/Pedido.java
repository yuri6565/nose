/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.util.Date;
import java.util.List;

/**
 *
 * @author Personal
 */
public class Pedido {
    private int id_pedido;
    private String num_pedido;
    private String nombre;
    private String estado;
    private Date fecha_inicio;
    private Date fecha_fin;
    private int idCliente;
    private List<PedidoDetalle> detalles;

    public Pedido() {
    }

    public Pedido(int id_pedido, String num_pedido, String nombre, String estado, Date fecha_inicio, Date fecha_fin, int idCliente) {
        this.id_pedido = id_pedido;
        this.num_pedido = num_pedido;
        this.nombre = nombre;
        this.estado = estado;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.idCliente = idCliente;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getNum_pedido() {
        return num_pedido;
    }

    public void setNum_pedido(String num_pedido) {
        this.num_pedido = num_pedido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }
    
    public List<PedidoDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<PedidoDetalle> detalles) {
        this.detalles = detalles;
    }

    
}

