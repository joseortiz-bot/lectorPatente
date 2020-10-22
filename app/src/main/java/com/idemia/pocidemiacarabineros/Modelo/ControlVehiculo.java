package com.idemia.pocidemiacarabineros.Modelo;

public class ControlVehiculo {
    protected String patente,direccion,fecha,hora;
    public ControlVehiculo(String p,String d,String f, String h){
        this.patente = p;
        this.direccion=d;
        this.fecha=f;
        this.hora=h;
    }

    public ControlVehiculo ()
    {

    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }
}
