import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


public class Client {

   public static void main(String[] args) throws Exception {
       String fileName;
       String fileContents, ack;
      
       BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
       System.out.println("Enter File name:");
       fileName = inFromUser.readLine();
      
       Socket clientSocket = new Socket("10.122.161.180",400);
       OutputStream outToServer = clientSocket.getOutputStream();
       BufferedReader inFromServer = new BufferedReader(
                                                new InputStreamReader(clientSocket.getInputStream()));
      
       outToServer.write((fileName+"\n").getBytes());      
       ack = inFromServer.readLine();
       if(ack.equals("NACK")){
           System.out.println("File not found in the Server!");
       }else{  
           long receivedChecksum = Long.valueOf(ack);
           fileContents = inFromServer.readLine();          
           long fileChecksum = calculateChecksum(fileContents.getBytes());
           if(receivedChecksum == fileChecksum){
               System.out.println("Received : "+fileContents);
               outToServer.write("File received successfully.\n".getBytes());
           }else{
               System.out.println("Received with Error");
               outToServer.write("Error : checksum does not match!.\n".getBytes());
           }
           Thread.sleep(1000);
           clientSocket.close();
       }

   }
  
   private static long calculateChecksum(byte[] contents){
       Checksum checksum = new CRC32();
        checksum.update(contents, 0, contents.length);
        return checksum.getValue();
   }

}