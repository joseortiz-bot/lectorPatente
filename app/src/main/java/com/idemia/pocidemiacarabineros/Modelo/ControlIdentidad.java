package com.idemia.pocidemiacarabineros.Modelo;

public class ControlIdentidad {

    private String rut;
    private String nombre;
    private String fechaNacimiento;
    private String procedimiento;
    private String obscervacion;

    public ControlIdentidad(String rut, String nombre, String fechaNacimiento, String procedimiento, String obscervacion) {
        this.rut = rut;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.procedimiento = procedimiento;
        this.obscervacion = obscervacion;
    }

    public ControlIdentidad() {
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public String getObscervacion() {
        return obscervacion;
    }

    public void setObscervacion(String obscervacion) {
        this.obscervacion = obscervacion;
    }
}
