package one;

import java.util.ArrayList;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author asus
 */
@Path("machineOne")
public class MachineOneResource {

    @Context
    private UriInfo context;
    private ArrayList<ChatRoom> allChatRoom;
    /**
     * Creates a new instance of MachineOneResource
     */
    public MachineOneResource() {
        allChatRoom = new ArrayList();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllChatRoom() {
        return allChatRoom.toString();
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
    }
    
    @PUT @Path("subcribe")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String subscribeChatRoom(String idChatRoom, String pseudo){
        String result;
        if (!allChatRoom.contains(idChatRoom)){
            result = "Erreur : chatroom inexistant";
        }
        else {
            ChatRoom objetChatRoom = allChatRoom.get(allChatRoom.indexOf(idChatRoom));
            result = objetChatRoom.add(pseudo);
        }
        return result;
    }
    
    @PUT @Path("unsubcribe")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String unsubscribeChatRoom(String idChatRoom, String pseudo){
        String result;
        if (!allChatRoom.contains(idChatRoom)){
            result = "Erreur : chatroom inexistant";
        }
        else {
            ChatRoom objetChatRoom = allChatRoom.get(allChatRoom.indexOf(idChatRoom));
            result = objetChatRoom.delete(pseudo);
        }
        return result;
    }
    
    @PUT @Path("postMessage")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String postMessage(String idChatRoom){
        ChatRoom objetChatRoom = allChatRoom.get(allChatRoom.indexOf(idChatRoom));
        ArrayList<String> ChatRoomUsers = objetChatRoom.getAllUsers();
        return  ChatRoomUsers.toString();
    }
    
    @PUT @Path("create")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String createChatRoom(String idChatRoom, String proprio){
        String result;
        if (allChatRoom.contains(idChatRoom)){
            result = "Erreur : chatroom existe deja";
        }
        else {
            ChatRoom newChatRoom = new ChatRoom(idChatRoom, proprio);
            allChatRoom.add(newChatRoom);
            result = "Succes : Chatroom cree";
        }
        return result;
    }
    @PUT @Path("delete")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteChatRoom(String idChatRoom, String pseudo){
        String result;
        if (!allChatRoom.contains(idChatRoom)){
            result = "Erreur : chatroom n'existe pas";
        }
        else {
            ChatRoom objetChatRoom = allChatRoom.get(allChatRoom.indexOf(idChatRoom));
            if (objetChatRoom.getProprio().equals(pseudo)){
                if (objetChatRoom.getAllUsers().size() == 1) {
                    //supprimer objetChatRoom
                    result = "Succes : chatroom supprime";
                }
                else{
                    result = "Erreur : y'a des gens dans le groupe";
                }
            }
            else {
                result = "Erreur: vous n'etes pas proprio du groupe";
            }
        }
        return result;
    }
}
