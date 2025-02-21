package main.java.com.SistemadePagamentoeAluguel.models;

public class Administrador {
    private final String email;
    private final String senhaHash;

    public Administrador(String email, String senha) {
        this.email = email;
        this.senhaHash = gerarHash(senha); 
    }

    public String getEmail() { return email; }
    
    public boolean validarSenha(String senha) {
        return senhaHash.equals(gerarHash(senha));
    }

    private String gerarHash(String input) {
        return Integer.toHexString(input.hashCode());
    }
}