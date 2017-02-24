package aiguillage;

import java.util.ArrayList;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import static javax.ws.rs.client.Entity.entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;
import org.glassfish.jersey.media.sse.SseBroadcaster;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * REST Web Service
 *
 * @author asus
 */
@Singleton
@Path("AiguilleurOne")
public class AiguilleurOneResource {

    @Context
    private UriInfo context;
    private WebTarget webTarget;
    private WebTarget TwoTarget;
    private Client client;
    private String AiguilleurTwoURI = "http://localhost:8080/ChatRoomAiguilleurTwo/aiguillage/AiguilleurTwo";
    private String OneURI = "http://localhost:8080/ChatRoomContainers/machineOne/One";
    private String TwoURI = "http://localhost:8080/ChatRoomContainers/machineTwo/Two";
    private String ThreeURI = "http://localhost:8080/ChatRoomContainers/machineThree/Three";
    private ArrayList<String> machinesURI;
    private ArrayList<String> OneChatRooms, TwoChatRooms, ThreeChatRooms;
    private ArrayList<String> users;
    private static final SseBroadcaster BROADCASTER = new SseBroadcaster();

    /**
     * Creates a new instance of AiguilleurResource
     */
    public AiguilleurOneResource() {
        this.machinesURI = new ArrayList();
        this.machinesURI.add("One;"+OneURI);
        this.machinesURI.add("Two;"+TwoURI);
        this.machinesURI.add("Three;"+ThreeURI);
        this.OneChatRooms = new ArrayList();
        this.TwoChatRooms = new ArrayList();
        this.ThreeChatRooms = new ArrayList();
        this.users = new ArrayList();
        
    }
    
    @GET @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test(){
        // 2 users max
        if (this.users.size() <2 ) return "UP:OK";
        else return "UP:FULL";
    }
   
    
    @GET @Path("getallchatroom")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllChatRoom() {
        String result;
        ArrayList<String> list = new ArrayList();
        this.client = ClientBuilder.newClient();
        String resultOne = this.client.target(OneURI).request(MediaType.TEXT_PLAIN).get(String.class); 
        String resultTwo = this.client.target(TwoURI).request(MediaType.TEXT_PLAIN).get(String.class); 
        String resultThree = this.client.target(ThreeURI).request(MediaType.TEXT_PLAIN).get(String.class);
        if (resultOne.equals("empty") && resultTwo.equals("empty") && resultThree.equals("empty"))  result = "NO:Aucun ChatRoom créé";
        else {
            if (!resultOne.equals("empty")) {
                 String[] fragments = resultOne.substring(1, resultOne.length()-1).split(",");
                 for(int i=0; i < fragments.length; i++) {
                     if(!this.OneChatRooms.contains(fragments[i])) this.OneChatRooms.add(fragments[i]);
                     list.add(fragments[i]);
                 }
            }
            if (!resultTwo.equals("empty")) {
                 String[] fragments = resultTwo.substring(1, resultTwo.length()-1).split(",");
                 for(int i=0; i < fragments.length; i++) {
                     if(!this.TwoChatRooms.contains(fragments[i])) this.TwoChatRooms.add(fragments[i]);
                     list.add(fragments[i]);
                 }

            }
            if (!resultThree.equals("empty")) {
                 String[] fragments = resultThree.substring(1, resultThree.length()-1).split(",");
                 for(int i=0; i < fragments.length; i++) {
                     if(!this.ThreeChatRooms.contains(fragments[i])) this.ThreeChatRooms.add(fragments[i]);
                     list.add(fragments[i]);
                 }
            }
            result = list.toString();         
        }
        return result;
    }
    
    @GET @Path("getallusers")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllUsers(){
       if (this.users.isEmpty()) return "empty";
       else return this.users.toString();
    }
    
    @GET @Path("getusers/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getChatRoomUsers(@PathParam("idChatRoom") String idChatRoom){
        String result;
        String URI = this.findChatRoomLocation(idChatRoom);
        this.client = ClientBuilder.newClient();
        this.webTarget = this.client.target(URI).path("getusers").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
        result =  this.webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
        return result;
    }
    
    @PUT @Path("register")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String register(String pseudo){
        String result;
        if (this.users.contains(pseudo)) {
            result = "NO:Pseudo existe deja";
        }
        else {
            /* envoie du pseudo a AiguilleurOne s'il est UP */
            this.client = ClientBuilder.newClient();
            Response test = this.client.target(this.AiguilleurTwoURI).path("test").request(MediaType.TEXT_PLAIN).get();
            if (test.getStatus() == 200){
                String response = client.target(this.AiguilleurTwoURI).path("getallusers").request(MediaType.TEXT_PLAIN).get(String.class);
                if (response.contains(pseudo)) {
                    result = "NO:Pseudo existe deja";
                }
                else {
                    this.users.add(pseudo);
                    result = "OK:Pseudo enregistré";
                }
            }
            else{
                this.users.add(pseudo);
                result = "OK:Pseudo enregistré";
            }
        }
        return result;
    }
    
    @PUT @Path("unregister/{pseudo}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void unregister(@PathParam("pseudo") String pseudo, String idChatRoom){
        if (this.users.contains(pseudo)) {
            this.users.remove(pseudo);   
            if(!idChatRoom.equals("empty")){
                unsubscribeChatRoom(idChatRoom, pseudo);
            }
        }
    }
    
    
    @PUT @Path("subscribe/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String subscribeChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        String result;
        if(!getAllChatRoom().contains(idChatRoom)) result = "NO:ChatRoom "+idChatRoom+" n'existe pas";
        else {
            String URI = this.findChatRoomLocation(idChatRoom);
            this.client = ClientBuilder.newClient();
            this.webTarget = this.client.target(URI).path("subscribe").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
            result =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(pseudo,MediaType.TEXT_PLAIN),String.class);
            if (result.startsWith("OK")) postMessage(idChatRoom, "serveur: connexion de "+pseudo);
        }
        return result;
    }
    
    @PUT @Path("unsubscribe/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String unsubscribeChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        String result;
        String URI = this.findChatRoomLocation(idChatRoom);
        this.client = ClientBuilder.newClient();
        this.webTarget = this.client.target(URI).path("unsubscribe").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
        result =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(pseudo,MediaType.TEXT_PLAIN),String.class);
        if (result.startsWith("OK")) postMessage(idChatRoom, "serveur: déconnexion de "+pseudo);
        return result;
    }
    
    @PUT @Path("create/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String createChatRoom(@PathParam("idChatRoom") String idChatRoom, String proprio){
        String result; String[] fragments;
        if (getAllChatRoom().contains(idChatRoom)){
            result = "NO:Chatroom existe deja";
        }
        else{
            fragments = this.roundRobin().split(";"); String nomMachine = fragments[0]; String URI = fragments[1]; 
            this.client = ClientBuilder.newClient();
            this.webTarget = this.client.target(URI).path("create").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
            result =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(proprio,MediaType.TEXT_PLAIN),String.class);
            if(result.startsWith("OK")){
                switch(nomMachine){
                    case "One" : {
                        if (!this.OneChatRooms.contains(idChatRoom)) this.OneChatRooms.add(idChatRoom);
                        break;
                    }
                    case "Two" : {
                        if (!this.TwoChatRooms.contains(idChatRoom)) this.TwoChatRooms.add(idChatRoom);
                        break;
                    }
                    case "Three" : {
                        if (!this.ThreeChatRooms.contains(idChatRoom)) this.ThreeChatRooms.add(idChatRoom);
                        break;
                    }
                }
            }
        }
        return result;
        
    }
    @PUT @Path("delete/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteChatRoom(@PathParam("idChatRoom") String idChatRoom, String pseudo){
        String result;
        if (!getAllChatRoom().contains(idChatRoom)){
            result = "NO:Chatroom n'existe pas";
        }else{
            String URI = this.findChatRoomLocation(idChatRoom);
            this.client = ClientBuilder.newClient();
            this.webTarget = this.client.target(URI).path("delete").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom);
            result =  this.webTarget.request(MediaType.TEXT_PLAIN).put(entity(pseudo,MediaType.TEXT_PLAIN),String.class);
            if(result.startsWith("OK")){
                if(this.OneChatRooms.contains(idChatRoom)) this.OneChatRooms.remove(idChatRoom);
                else if(this.TwoChatRooms.contains(idChatRoom)) this.TwoChatRooms.remove(idChatRoom);
                else this.ThreeChatRooms.remove(idChatRoom);
            }
        }
        return result;
    }
    
    @POST @Path("post/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void postMessage(@PathParam("idChatRoom") String idChatRoom, String message){
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        OutboundEvent event = eventBuilder.name(idChatRoom)
            .mediaType(MediaType.TEXT_PLAIN_TYPE)
            .data(String.class, message)
            .build();
        BROADCASTER.broadcast(event);
        
        /* envoie du msg a AiguilleurOne s'il est UP */
        Response result = client.target(this.AiguilleurTwoURI).path("test").request(MediaType.TEXT_PLAIN).get();
        if (result.getStatus() == 200){
            client.target(this.AiguilleurTwoURI).path("postbridge").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom)
                .request(MediaType.TEXT_PLAIN).post(entity(message,MediaType.TEXT_PLAIN),String.class);
        }
    }
    
    @POST @Path("postbridge/{idChatRoom}")
    @Consumes(MediaType.TEXT_PLAIN)
    public void postByTwo(@PathParam("idChatRoom") String idChatRoom, String message){
        OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
        OutboundEvent event = eventBuilder.name(idChatRoom)
            .mediaType(MediaType.TEXT_PLAIN_TYPE)
            .data(String.class, message)
            .build();        
        BROADCASTER.broadcast(event);
        
    }
    
    @GET
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput listenToBroadcast() {
        final EventOutput eventOutput = new EventOutput();
        BROADCASTER.add(eventOutput);
        return eventOutput;
    }
    
    private String roundRobin(){
        String result = this.machinesURI.remove(0);
        this.machinesURI.add(result);
        return result;
    }
    
    private String findChatRoomLocation(String idChatRoom){
        if (this.OneChatRooms.contains(idChatRoom)) return this.OneURI;
        else if (this.TwoChatRooms.contains(idChatRoom)) return this.TwoURI;
        else return this.ThreeURI;
    }
}
