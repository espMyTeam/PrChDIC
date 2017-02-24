/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package machineOne;

import java.util.HashMap;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author asus
 */
@Singleton
@Path("One")
public class OneResource {

    @Context
    private UriInfo context;
    private HashMap<String,ChatRoom> allChatRoom;
    /**
     * Creates a new instance of OneResource
     */
    public OneResource() {
        allChatRoom = new HashMap();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllChatRoom() {
        if(allChatRoom.keySet().isEmpty()) return "empty";
        else return allChatRoom.keySet().toString();
    }

    @GET @Path("getusers/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllUsers(@PathParam("idChatRoom") String idChatRoom) {
        ChatRoom objetChatRoom = allChatRoom.get(idChatRoom);
        return objetChatRoom.getAllUsers().toString();
    }
    
    @PUT @Path("subscribe/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String subscribeChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        ChatRoom objetChatRoom = allChatRoom.get(idChatRoom);
        if (objetChatRoom.subscribe(pseudo)) return "OK:vous etes connecté au ChatRoom "+idChatRoom;
        else return "NO:echec, veuillez choisir un autre pseudo";
    }
    
    @PUT @Path("unsubscribe/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String unsubscribeChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        ChatRoom objetChatRoom = allChatRoom.get(idChatRoom);
        objetChatRoom.unsubscribe(pseudo);
        return "OK:vous venez de quitter le ChatRoom "+idChatRoom;
    }
    
    @PUT @Path("create/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String createChatRoom(@PathParam("idChatRoom") String idChatRoom,String proprio){
        String result;
        if (allChatRoom.keySet().toString().contains(idChatRoom)){
            result = "NO:Chatroom existe deja";
        }
        else {
            ChatRoom newChatRoom = new ChatRoom(idChatRoom, proprio);
            allChatRoom.put(idChatRoom, newChatRoom);
            result = "OK:Chatroom créé";
        }
        return result;
    }
    @PUT @Path("delete/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteChatRoom(@PathParam("idChatRoom")String idChatRoom, String pseudo){
        String result;
        ChatRoom objetChatRoom = allChatRoom.get(idChatRoom);
        if (objetChatRoom.getProprio().equals(pseudo)){
            if (objetChatRoom.getAllUsers().isEmpty()) {
                    allChatRoom.remove(idChatRoom);
                    result = "OK:Chatroom supprimé";
            }
            else{
                if (objetChatRoom.getAllUsers().size()==1 && objetChatRoom.getAllUsers().contains(pseudo)) result ="NO:Déconnectez-vous d'abord !" ;
                else result = "NO:Il y'a encore des membres connectés";
            }
        }
        else {
            result = "NO:vous n'etes pas proprio du ChatRoom";
        }
        return result;
    }
   
}
