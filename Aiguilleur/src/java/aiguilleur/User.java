package aiguilleur;

public class User {
    private String pseudo;
    private String URI;
    
    public User (String pseudo, String URI){
        this.pseudo = pseudo;
        this.URI = URI;
    }
    
    public String getPseudo() {
        return pseudo;
    }

    public String getURI() {
        return URI;
    }
}
