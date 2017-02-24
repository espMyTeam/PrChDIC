package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import static javax.ws.rs.client.Entity.entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

public class ChatUserImpl {
    private String title = "Logiciel de discussion en ligne";
    private String pseudo = null;
    private String idChatRoom = null;

    private JFrame window = new JFrame(this.title);
    private JMenuBar menuBar = new JMenuBar();
    private JMenu menuChatRoom = new JMenu("Gérer");
    private JMenu menuChoix = new JMenu("Choisir");
    private JMenu menuMembres = new JMenu("Statut");
    private JMenuItem menuItemCreerChatRoom = new JMenuItem("Créer ChatRoom");
    private JMenuItem menuItemSupprimerChatRoom = new JMenuItem("Supprimer ChatRoom");
    private JMenuItem menuItemListerChatRoom = new JMenuItem("Lister ChatRoom");
    private JMenuItem menuItemChoisirChatRoom =new JMenuItem("Choisir ChatRoom");
    private JMenuItem menuItemQuitterChatRoom = new JMenuItem("Quitter ChatRoom");
    private JMenuItem menuItemMembresChatRoom = new JMenuItem("Lister les Membres en Ligne");
    private JList<String> list;
    private JLabel label = new JLabel("Veuillez choisir votre ChatRoom",SwingConstants.CENTER);
    private JLabel progressBarLabel = new JLabel("Recherche Aiguilleur", SwingConstants.CENTER);
    private JTextArea txtOutput = new JTextArea();
    private JTextField txtMessage = new JTextField();
    private JButton btnSend = new JButton("Envoyer");
    
    private ArrayList<String> URI_AIGUILLEUR;
    
    EventSource eventSource;
    private WebTarget webTarget;
    private Client client;
    
    public ChatUserImpl() {
        this.URI_AIGUILLEUR = new ArrayList();
        this.URI_AIGUILLEUR.add("http://localhost:8080/ChatRoomAiguilleurOne/aiguillage/AiguilleurOne");
        this.URI_AIGUILLEUR.add("http://localhost:8080/ChatRoomAiguilleurTwo/aiguillage/AiguilleurTwo");
        this.client = ClientBuilder.newBuilder().register(SseFeature.class).build();
        this.createIHM();
        this.test();
    }
    
     /* methode principale */
    public static void main(String[] args){
	new ChatUserImpl();
    }
    
    public void createIHM(){
        
        // Assemblage des composants
        JPanel panel = (JPanel)this.window.getContentPane();
	JScrollPane sclPane = new JScrollPane(txtOutput);
	panel.add(sclPane, BorderLayout.CENTER);
        // paneau nord
        menuChatRoom.add(menuItemCreerChatRoom);
        menuChatRoom.add(menuItemListerChatRoom);
        menuChatRoom.add(menuItemSupprimerChatRoom);
        menuChoix.add(menuItemChoisirChatRoom);
        menuChoix.add(menuItemQuitterChatRoom);
        menuMembres.add(menuItemMembresChatRoom);
        menuBar.add(menuChatRoom);
        menuBar.add(menuChoix);
        menuBar.add(menuMembres);
        menuItemQuitterChatRoom.setEnabled(false);
        JPanel northPanel = new JPanel(new BorderLayout());
        
        northPanel.add(this.menuBar, BorderLayout.CENTER);
        northPanel.add(this.label, BorderLayout.SOUTH);
        // panneau sud
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(this.txtMessage, BorderLayout.CENTER);
        southPanel.add(this.btnSend, BorderLayout.EAST);
        
        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(southPanel, BorderLayout.SOUTH);
        
        UIManager UI=new UIManager();
        UI.put("OptionPane.background", Color.white);
        UI.put("Panel.background", Color.white);
        northPanel.setBackground(Color.WHITE);
        this.label.setForeground(Color.red);
        menuBar.setBackground(Color.WHITE);
        menuItemCreerChatRoom.setBackground(Color.WHITE);
        menuItemListerChatRoom.setBackground(Color.WHITE);
        menuItemSupprimerChatRoom.setBackground(Color.WHITE);
        menuItemChoisirChatRoom.setBackground(Color.WHITE);
        menuItemQuitterChatRoom.setBackground(Color.WHITE);
        menuItemMembresChatRoom.setBackground(Color.WHITE);
        
        // Gestion des évènements
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                window_windowClosing(e);
            }
        });
        menuItemChoisirChatRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do{
                    idChatRoom = JOptionPane.showInputDialog(window, "Entrez le nom du ChatRoom : ","Nouveau ChatRoom",JOptionPane.INFORMATION_MESSAGE);
                    if (idChatRoom == null) break;
                }while(idChatRoom.equals(""));
                if (idChatRoom != null) {
                    String result = webTarget.path("subscribe").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom)
                            .request(MediaType.TEXT_PLAIN).put(entity(pseudo, MediaType.TEXT_PLAIN),String.class);
                    if(result.startsWith("OK")){
                        menuItemQuitterChatRoom.setEnabled(true);
                        label.setText(result.split(":")[1]);
                        eventSource = new EventSource(webTarget) {
                            @Override
                            public void onEvent(InboundEvent inboundEvent) {
                                if(inboundEvent.getName().equals(idChatRoom)){
                                     displayMessage(inboundEvent.readData(String.class));
                                }                           
                            }
                        };
                    }
                    
                    else {
                        JOptionPane.showMessageDialog(window, result.split(":")[1],"Echec",JOptionPane.ERROR_MESSAGE);
                        idChatRoom=null;
                    }
                }
            }
        });
        menuItemQuitterChatRoom.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = webTarget.path("unsubscribe").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom)
                            .request(MediaType.TEXT_PLAIN).put(entity(pseudo, MediaType.TEXT_PLAIN),String.class);
                if (result.startsWith("OK")) JOptionPane.showMessageDialog(window,result.split(":")[1],"Deconnexion", JOptionPane.INFORMATION_MESSAGE);
                idChatRoom = null;
                txtOutput.setText(null);
                label.setText("Veuillez choisir votre ChatRoom");
                eventSource.close();
            }
            
        });
        menuItemCreerChatRoom.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String ChatRoom;
                do{
                    ChatRoom = JOptionPane.showInputDialog(window, "Entrez le nom du ChatRoom : ","Nouveau ChatRoom",JOptionPane.INFORMATION_MESSAGE);
                    if (ChatRoom == null) break;
                }while(ChatRoom.equals(""));
                if (ChatRoom != null) {
                    String result = webTarget.path("create").path("{idChatRoom}").resolveTemplate("idChatRoom", ChatRoom)
                            .request(MediaType.TEXT_PLAIN).put(entity(pseudo, MediaType.TEXT_PLAIN),String.class);
                    if(result.startsWith("OK")) {
                        JOptionPane.showMessageDialog(window, result.split(":")[1],"Succes",JOptionPane.INFORMATION_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(window, result.split(":")[1],"Echec",JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            }
        });
        menuItemListerChatRoom.addActionListener(new ActionListener(){      
            @Override
            public void actionPerformed(ActionEvent e){
                String[] data;
                String result = webTarget.path("getallchatroom").request(MediaType.TEXT_PLAIN).get(String.class);
                if(result.startsWith("NO"))JOptionPane.showMessageDialog(window, result.split(":")[1]);
                else {
                    String fragments = result.substring(1, result.length()-1);
                    data = fragments.split(",");
                    list = new JList(data); 
                    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    list.setLayoutOrientation(JList.VERTICAL);
                    list.setVisibleRowCount(-1);
                    DefaultListCellRenderer renderer =  (DefaultListCellRenderer)list.getCellRenderer();  
                    renderer.setHorizontalAlignment(JLabel.CENTER);
                    JScrollPane listScroller = new JScrollPane(list);
                    listScroller.setPreferredSize(new Dimension(100, 150));
                    JPanel listPane = new JPanel(new BorderLayout());
                    listPane.add(listScroller, BorderLayout.CENTER);
                    JFrame f = new JFrame();
                    f.add(listPane);
                    f.pack();
                    f.setLocationRelativeTo(txtOutput);
                    f.setSize(100, 150);
                    f.setVisible(true);
                }
            }
        });
        
        menuItemSupprimerChatRoom.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String ChatRoom = JOptionPane.showInputDialog(window, "Entrez le nom du ChatRoom : ","Supprimer ChatRoom",JOptionPane.QUESTION_MESSAGE);
                String result = webTarget.path("delete").path("{idChatRoom}").resolveTemplate("idChatRoom", ChatRoom).request(MediaType.TEXT_PLAIN)
                        .put(entity(pseudo, MediaType.TEXT_PLAIN),String.class);
                if(result.startsWith("NO"))JOptionPane.showMessageDialog(window, result.split(":")[1]);
                else JOptionPane.showMessageDialog(window, result.split(":")[1]);
            }
        });
        
        menuItemMembresChatRoom.addActionListener(new ActionListener(){      
            @Override
            public void actionPerformed(ActionEvent e){
                String[] data;
                if (idChatRoom == null) JOptionPane.showMessageDialog(window,"Veuillez choisir d'abord un ChatRoom","echec", JOptionPane.INFORMATION_MESSAGE);
                else {
                    String result = webTarget.path("getusers").path("{idChatRoom}").resolveTemplate("idChatRoom", idChatRoom)
                        .request(MediaType.TEXT_PLAIN).get(String.class);
                    String fragments = result.substring(1, result.length()-1);
                    data = fragments.split(",");
                    list = new JList(data); 
                    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    list.setLayoutOrientation(JList.VERTICAL);
                    list.setVisibleRowCount(-1);
                    DefaultListCellRenderer renderer =  (DefaultListCellRenderer)list.getCellRenderer();  
                    renderer.setHorizontalAlignment(JLabel.CENTER);
                    JScrollPane listScroller = new JScrollPane(list);
                    listScroller.setPreferredSize(new Dimension(100, 150));
                    JPanel listPane = new JPanel(new BorderLayout());
                    listPane.add(listScroller, BorderLayout.CENTER);
                    JFrame f = new JFrame();
                    f.add(listPane);
                    f.pack();
                    f.setLocationRelativeTo(txtOutput);
                    f.setSize(100, 150);
                    f.setVisible(true);
                    
                }
            }
        });
        
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSend_actionPerformed(e);
            }
        });
        
	txtMessage.addKeyListener(new KeyAdapter() {
            @Override
	    public void keyReleased(KeyEvent event) {
		if (event.getKeyChar() == '\n')
		    btnSend_actionPerformed(null);
	    }
	});

        // Initialisation des attributs
        this.txtOutput.setBackground(Color.cyan);
        this.txtOutput.setForeground(Color.blue);
        this.txtOutput.setFont(new Font("Monospaced",1,15));
	this.txtOutput.setEditable(false);
        this.txtOutput.setLineWrap(true);
        this.txtOutput.setWrapStyleWord(true);
        this.window.setSize(500,500);
        this.window.setLocationRelativeTo(null); //affichage au centre
        this.window.setVisible(true);
        this.txtMessage.requestFocus();
    }
    
    /* tester la disponibilité d'un aiguilleur */
    private void test()  {
        Response result;
        boolean test_OK = false;
        JProgressBar pb= new JProgressBar(0, 100);
        pb.setStringPainted(true);
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(pb);
        panel.add(this.progressBarLabel);
        panel.setBackground(Color.WHITE);
        JFrame f = new JFrame("Connexion");
        f.add(panel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        Iterator it = this.URI_AIGUILLEUR.iterator();
        
        int i = 0;
        while(it.hasNext()){
            this.webTarget = client.target((String) it.next());
            result = this.webTarget.path("test").request(MediaType.TEXT_PLAIN).get();
            i+=50;
            pb.setValue(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
            if ((result.getStatus() == 200) && (result.readEntity(String.class).equals("UP:OK")) ) {
                test_OK = true;
                break;
            }
            
        }
        f.dispose();
        if (!test_OK){
            JOptionPane.showMessageDialog(this.window,"Aucun Serveur disponible", "Echec", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        else this.requestPseudo();
    }
    
    /* demander le pseudo du client */
    public void requestPseudo(){
        boolean register = false;
        String result="";
        do{
	    this.pseudo = JOptionPane.showInputDialog(
                this.window, "Entrez votre pseudo : ",
                this.title,  JOptionPane.INFORMATION_MESSAGE
		);
            if ((this.pseudo == null) || (this.pseudo.equals(""))) System.exit(0);
            else {
                result = this.webTarget.path("register").request(MediaType.TEXT_PLAIN).put(entity(this.pseudo,MediaType.TEXT_PLAIN),String.class);
                if (result.startsWith("NO")){
                    JOptionPane.showMessageDialog(this.window, result.split(":")[1], "Echec", JOptionPane.ERROR_MESSAGE);
                }
                else register = true;
            }
        }while(!register);
        JOptionPane.showMessageDialog(this.window, result.split(":")[1], "Succès", JOptionPane.INFORMATION_MESSAGE);
        this.txtOutput.setRequestFocusEnabled(true);        

    }	    

    /* instructions à executer lors de la fermeture de la fenetre */
    public void window_windowClosing(WindowEvent e){
        if (this.idChatRoom != null) this.webTarget.path("unregister").path("{pseudo}").resolveTemplate("pseudo", this.pseudo)
                .request(MediaType.TEXT_PLAIN).put(entity(this.idChatRoom,MediaType.TEXT_PLAIN),String.class);
        else  this.webTarget.path("unregister").path("{pseudo}").resolveTemplate("pseudo", this.pseudo)
                .request(MediaType.TEXT_PLAIN).put(entity("empty",MediaType.TEXT_PLAIN),String.class);
	System.exit(-1);
    }

    
    /* instructions à executer lors de l'evenement envoi de message*/
    public void btnSend_actionPerformed(ActionEvent e){
        if(this.idChatRoom !=null){
            this.webTarget.path("post").path("{idChatRoom}").resolveTemplate("idChatRoom", this.idChatRoom)
                .request(MediaType.TEXT_PLAIN).post(entity(this.pseudo+": "+this.txtMessage.getText(),MediaType.TEXT_PLAIN),String.class);
        }
            this.txtMessage.setText("");
            this.txtMessage.requestFocus();
    }
    
    /* lire les messages provenant du serveur */
    public void displayMessage( String message){
        System.out.println(message +"\n");
        String[] frags = message.split(":");
        if (frags[1].length()>1) this.txtOutput.append(message +"\n");
    }

    
}

