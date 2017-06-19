package kinggeorgeiiiserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApplication {
   ServerSocket myServerSocket;
   boolean ServerOn = true;
   public ServerApplication() {
      try {
         myServerSocket = new ServerSocket(8080);
      } catch(IOException ioe) { 
         System.out.println("Could not create server socket on port 8080. Quitting.");
         System.exit(-1);
      } 
      
      while(ServerOn) { 
         try { 
            Socket clientSocket = myServerSocket.accept();
            ClientServiceThread cliThread = new ClientServiceThread(clientSocket);
            cliThread.start(); 
         } catch(IOException ioe) { 
            System.out.println("Exception found on accept. Ignoring. Stack Trace :"); 
            ioe.printStackTrace(); 
         }  
      } 
      try { 
         myServerSocket.close(); 
         System.out.println("Server Stopped"); 
      } catch(Exception ioe) { 
         System.out.println("Error Found stopping server socket"); 
         System.exit(-1); 
      } 
   }
	
   public static void main (String[] args) { 
      new ServerApplication();        
   } 
	
   class ClientServiceThread extends Thread { 
      Socket myClientSocket;
      boolean m_bRunThread = true; 
      public ClientServiceThread() { 
         super(); 
      } 
		
      ClientServiceThread(Socket s) { 
         myClientSocket = s; 
      }
      //DELETE FUCKING CARDS FROM FUCKING STACK BITCH
      //IMPORTANT
      //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      
      //проверка карты на валидность для данного хода
      //должна быть равной или же больше или меньше на один ранг
      //учитываются ситуации для туза и двойки
      private boolean checkCard(Card check,Card card){
          int checkValue = check.getValue();
          int cardValue = card.getValue();
          
          if(checkValue == 2 && cardValue == 14)
              return true;
          if(checkValue == 14 && cardValue == 2)
              return true;
          
          if(checkValue == cardValue)
              return true;
          if(checkValue+1 == cardValue)
              return true;
          if(checkValue-1 == cardValue)
              return true;
          
          return false;
      }
      
      private void newGame(){
          try {
              deck = new Deck();
              deck.shuffle();
              deck.isEmpty();
              userStack = new Stack();
              myStack = new Stack();
              //13 card to each at start of the game
              for(int i = 0; i<13; i++)
                  if(!deck.isEmpty()){
                      userStack.addCard(deck.getCard());
                      myStack.addCard(deck.getCard());
                  }
              
              //отправляем клиенту его карты
              output.writeObject(userStack);
              System.out.println(0);
          } catch (IOException ex) {
              Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
          }
      }
      
      Deck deck;
      Stack userStack;
      Stack myStack;
      
      ObjectOutputStream output; 
      ObjectInputStream input;
      
      private void emptyDeck(){
          try {
              int size = myStack.size();
              output.writeObject("Empty deck");
              int clientSize = (Integer) input.readObject();
              if(size>=clientSize)
                  output.writeObject("You lost");
              else
                  output.writeObject("You win");
          } catch (IOException | ClassNotFoundException ex) {
              Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
          }
          
      }
      
      @Override
      public void run() { 
         try{ 
             System.out.println(
                     "Подключился новый клиент: " + myClientSocket.getInetAddress().getHostName());
             output = new ObjectOutputStream(myClientSocket.getOutputStream());
             input = new ObjectInputStream(myClientSocket.getInputStream());
             
             /*if(checkcheck){
                newGame();
                checkcheck = false;
             }*/
             
             try{
                 //цикл отвечает за повторение одних и тех же паттернов игры
                 while(true){
                     System.out.println(1);
                     //получаем обьект, он может быть двух видов: картой и строкой
                     Object obj = input.readObject();
                                          System.out.println(2);
                     //System.out.println(obj.getClass().getName());
                     //карта
                     if (obj.getClass().getName().equals("kinggeorgeiiiserver.Card")){
                                              System.out.println(3);
                         //читаем карту
                         Card inCard = (Card)obj;
                                              System.out.println(4);
                         boolean check = true;
                         boolean doubleCheck = false;
                         while(check){
                                                  System.out.println(5);
                             //проходим по картам сервера и смотрим есть ли подходящие карты,
                             //если есть, то отправляем, если нет, то
                             //берём карты из колоды
                             for(Card stackCard: myStack){
                                 if (checkCard(stackCard, inCard) == true){
                                     output.writeObject(stackCard);
                                                          System.out.println(6);
                                     myStack.remove(stackCard);
                                     check = true;
                                     doubleCheck = true;
                                     break;
                                 } else check = false;
                             }
                         }
                         while(!doubleCheck){
                                                  System.out.println(7);
                             Card deckCard = null;
                             if(!deck.isEmpty()){
                                deckCard = deck.getCard();
                                if(checkCard(deckCard,inCard) == true){
                                    output.writeObject(deckCard);
                                    doubleCheck = true;
                                } else{
                                    myStack.addCard(deckCard);
                                }
                             } else{
                                 emptyDeck();
                             }
                         }
                         if(myStack.isEmpty())
                             output.writeObject(new String("You lost"));
                         else{
                             output.writeObject(new String("End move"));
                             System.out.println(8);
                         }
                     } else{
                                              System.out.println(8);
                         //если приняли строку
                         String str = obj.toString();
                         switch(str){
                             //клиент говорит что ему нужна карта из колоды,
                             //отправляем карту
                             case "Card" : if (!deck.isEmpty())output.writeObject(deck.getCard()); else emptyDeck(); break;
                             //сервер проиграл, придумать логику в этом случае
                             case "You lost" : break;
                             //client want to play again
                             case "New game": newGame(); break;
                         }
                     }
                 }
             } catch(IOException | ClassNotFoundException ex){
                 Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
             }
         }
         catch(IOException ex){
             Logger.getLogger(ServerApplication.class.getName()).log(Level.SEVERE, null, ex);
         }
         
      } 
   } 
}

