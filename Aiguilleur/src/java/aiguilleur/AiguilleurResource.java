package aiguilleur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import static javax.ws.rs.client.Entity.entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author asus
 */
@Path("Aiguilleur")
public class AiguilleurResource {

    @Context
    private UriInfo context;
    private WebTarget webTarget;
    private Client client;
    private static int indice = 0;
    private ArrayList<String> machines;
    private ArrayList<User> users;
    private HashMap<String,Integer> dns;
    
    
    /**
     * Creates a new instance of AiguilleurResource
     */
    public AiguilleurResource() {
        machines = new ArrayList();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllChatRoom() {
        String listChatRoom = "";
        this.client = ClientBuilder.newClient();
        Iterator it = machines.iterator();
        while(it.hasNext()){
           String uriMachine = (String) it.next();
           this.webTarget = this.client.target(uriMachine); 
           
           listChatRoom += this.webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
        }
        // attention a l'exploitation de la liste
        return listChatRoom;
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
    }
    
    @PUT @Path("create")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String createChatRoom(String idChatRoom, String proprio){
        int x = roundRobin();
        String uriMachine = machines.get(x);
        this.client = ClientBuilder.newClient();
        this.webTarget = this.client.target(uriMachine).path("create");
        //comment ajouter deux parametres pour consommer le service
        String resultat =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity, responseType);
        
        if (resultat.equals("Succes : Chatroom cree")){
            dns.put(idChatRoom, x);
            return resultat;
        }
        else return resultat;
    }
    
    @PUT @Path("delete")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String deleteChatRoom(String idChatRoom, String proprio){
        this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("delete");
        
        //comment ajouter deux parametres pour consommer le service
        return this.webTarget.request(MediaType.TEXT_PLAIN).put(entity, responseType);
    }
    
    @PUT @Path("register")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String registerChatRoom(String pseudo, String uriClient){
        String result;
        Boolean trouve = false;
        Iterator it = users.iterator();
        while(it.hasNext()){
            User res = (User) it.next();
            if(res.getPseudo().equals(pseudo)) {
                trouve = true;
                break;
            }
        }
        if (trouve) result = "Erreur : pseudo existe deja";
        else {
            users.add(new User(pseudo,uriClient));
            result = "Succes : pseudo enregistre";
        }
        
        return result;
        
    }
    @PUT @Path("unregister")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public void unregisterChatRoom(String pseudo){
        Iterator it = users.iterator();
        while(it.hasNext()){
            User res = (User) it.next();
            if(res.getPseudo().equals(pseudo)) {
                users.remove(res);
                break;
            }
        }

    }
    
    @PUT @Path("subscribe")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String subscribeChatRoom(String idChatRoom, String pseudo){
        this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("subscribe");
        
        //comment ajouter deux parametres pour consommer le service
        return this.webTarget.request(MediaType.TEXT_PLAIN).put(entity, responseType);
    }
    
    @PUT @Path("unsubscribe")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String unsubscribeChatRoom(String idChatRoom, String pseudo){
        this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("unsubscribe");
        
        //comment ajouter deux parametres pour consommer le service
        return this.webTarget.request(MediaType.TEXT_PLAIN).put(entity, responseType);
    }
    
    @PUT @Path("postMessage")
    @Consumes(MediaType.TEXT_PLAIN)
    public void postMessage(String idChatRoom, String msg){
       this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("postMessage");
        
        String listUsers =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(idChatRoom,MediaType.TEXT_PLAIN),String.class);
        broadcastMessage(listUsers);
    }
    
    private void broadcastMessage(String listUsers) {
        // la fonction concomme le service displayMessage de chaque user
    }
    
    public int roundRobin(){
        if (indice+1> machines.size()){
            indice = 0;
            return indice;
        }
        else{
            int c = indice;
            indice++;
            return c;
        }
    }

    
}
