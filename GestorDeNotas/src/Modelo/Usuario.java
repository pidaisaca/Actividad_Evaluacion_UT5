package Modelo;

public class Usuario {

    private String usuario;
    private String contrasenna;
    private String email;

    public Usuario(String usuario, String contrasenna, String email) {
        this.usuario = usuario;
        this.email = email;
        this.contrasenna = contrasenna;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasenna() {
        return contrasenna;
    }

    public String getEmail() {
        return email;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
