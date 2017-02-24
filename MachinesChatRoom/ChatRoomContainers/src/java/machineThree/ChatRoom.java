package machineThree;

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
    
    public String getidChatRoom(){
        return this.idChatRoom;
    }
    
    public ArrayList<String> getAllUsers(){
        return pseudos;
    }
    
    public boolean subscribe(String pseudo){
        boolean result;
        if (!pseudos.contains(pseudo)){
            pseudos.add(pseudo);
            result = true;
        }
        else result = false;
        return result;
    }
    
    public void unsubscribe(String pseudo){
        pseudos.remove(pseudo);
    }
    
}
