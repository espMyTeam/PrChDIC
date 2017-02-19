package one;

import java.util.ArrayList;

/**
 *
 * @author asus
 */
public class ChatRoom {
    private String idChatRoom;
    private ArrayList<String> pseudos;
    private String proprio;
    
    public ChatRoom (String idChatRoom, String proprio){
        this.idChatRoom = idChatRoom;
        this.pseudos = new ArrayList();
        this.proprio = proprio;
    }

    public String getProprio() {
        return proprio;
    }
    
    public String getidRoom(){
        return this.idChatRoom;
    }
    
    public ArrayList<String> getAllUsers(){
        return pseudos;
    }
    
    public String add(String pseudo){
        String result;
        if (!pseudos.contains(pseudo)){
            pseudos.add(pseudo);
            result = "Succes : pseudo ajoute";
        }
        else result = "Erreur : pseudo non ajoute";
        return result;
    }
    
    public String delete(String pseudo){
        String result;
        if (pseudos.contains(pseudo)) {
            pseudos.remove(pseudo);
            result = "Succes : pseudo supprime";
        }
        else result = "Erreur : pseudo non supprime";
        return result;
    }
    
}
