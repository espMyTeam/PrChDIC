package aiguilleur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
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
@Singleton
@Path("Aiguilleur")
public class AiguilleurResource {

    @Context
    private UriInfo context;
    private WebTarget webTarget;
    private Client client;
    private static int indice = 0;
    private ArrayList<String> machines;
    private HashMap<String,String> users;
    private HashMap<String,Integer> dns;
    
    
    /**
     * Creates a new instance of AiguilleurResource
     */
    public AiguilleurResource() {
        this.machines = new ArrayList();
        this.machines.add("http://localhost:8080/MachinesChatRoom/one/machineOne");
        this.users = new HashMap();
        this.dns = new HashMap();
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
           
           listChatRoom+=this.webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
        }
        // attention a l'exploitation de la liste
        return listChatRoom;
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
    }
    
    @PUT @Path("create/{idChatRoom}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String createChatRoom(@PathParam("idChatRoom") String idChatRoom, String proprio){
        int x = roundRobin();
        String uriMachine = machines.get(x);
        this.client = ClientBuilder.newClient();
        this.webTarget = this.client.target(uriMachine).path("create").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
        String resultat =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(proprio,MediaType.TEXT_PLAIN), String.class);
        
        if (resultat.equals("Succes : Chatroom cree")){
            dns.put(idChatRoom, x);
            return resultat;
        }
        else return resultat;
    }
    
    @PUT @Path("delete/{idChatRoom}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String deleteChatRoom(@PathParam("idChatRoom")String idChatRoom, String proprio){
        this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("delete").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
        return this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(proprio,MediaType.TEXT_PLAIN), String.class);
    }
    
    @PUT @Path("register/{pseudo}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String registerChatRoom(@PathParam("pseudo")String pseudo, String uriClient){
        String result;
        if(this.users.containsKey(pseudo)) 
            result = "Erreur : pseudo existe deja";
        else {
            users.put(pseudo,uriClient);
            result = "Succes : pseudo enregistre";
        }
        
        return result;
        
    }
    @PUT @Path("unregister/{pseudo}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void unregisterChatRoom(@PathParam("pseudo") String pseudo){
        this.users.remove(pseudo);

    }
    
    @PUT @Path("subscribe/{idChatRoom}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String subscribeChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("subscribe").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
        return this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(pseudo,MediaType.TEXT_PLAIN), String.class);
    }
    
    @PUT @Path("unsubscribe/{idChatRoom}")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String unsubscribeChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("unsubscribe").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
        return this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(pseudo,MediaType.TEXT_PLAIN), String.class);
    }
    
    @PUT @Path("postMessage/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void postMessage(@PathParam("idChatRoom")String idChatRoom, String msg){
       this.client = ClientBuilder.newClient();
        int indiceMachine = dns.get(idChatRoom);
        String uriMachine = machines.get(indiceMachine);
        this.webTarget = this.client.target(uriMachine).path("postMessage");      
        String listUsers =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(idChatRoom,MediaType.TEXT_PLAIN),String.class);
        broadcastMessage(listUsers);
    }
    
    private void broadcastMessage(String listUsers) {
        String first = listUsers.split("[")[0];
        String second = first.split("]")[0];
        String[] pseudos = second.split(",");
        for (int i=0;i<pseudos.length;i++){
            String clientURI = this.users.get(pseudos[i]);
            // la fonction concomme le service displayMessage de chaque user
        }
        
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
