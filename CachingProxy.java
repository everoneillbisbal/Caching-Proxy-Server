/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/**
 *
 * @author evero
 */

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;

public class CachingProxy {

    public static void main(String[] args) {

        if (args.length == 5)
        {   
            // EXAMPLE START java CachingProxy.java caching-proxy --port 3000 --origin https://api.nasa.gov
            String[] domainExtensions = {".com", ".net", ".org", ".edu", ".gov"};
            if (args[0].equalsIgnoreCase("caching-proxy") &
                args[1].equalsIgnoreCase("--port") &
                args[3].equals("--origin"))
                /* NEXT STEP IS TO process requests in order to make one
                   the user must type in somehting like this 
                   http://localhost:3000/ we like to ask the user to type something like this and
                   at the end of that / they put exactly where they want to go then we go in
                   a conditional and then we take the part of the slash and add it to the origin url               
                   then we go into it and make a request to that API.
                */ 
                
                // MY NASA API KEY:
                // SKLbddw5WxZzP2ZZsT8bHpHTggrdsiTw2tmzYnic
            {       
                    try {
                        int check = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        System.out.println("Input a port number.");
                        return;
                    }
                    // Checks if user is inputting a allow domain extension.
                    boolean contains = Arrays.asList(domainExtensions).contains(args[4].substring(args[4].length() - 4));
                    if (contains == false) {
                        System.out.println("Ensure you are using a valid domain extension.");
                        return;
                    }
                    System.out.println(args[4].substring(args[4].length() - 4));
                    try {
                        File startingFile = new File("CachingProxy.csv");
                        if (startingFile.createNewFile()) {
                            FileWriter writingToStart = new FileWriter("CachingProxy.csv");
                            System.out.println("Caching proxy server started.");
                            writingToStart.write("Port Number: " + args[2]);
                            writingToStart.write(System.lineSeparator() + "Origin: " + args[4]);
                            writingToStart.close();
                        }
                        else {
                            System.out.println("Server already started.");
                        }
                    } catch (IOException e) {
                        System.out.println("Error.");
                    }
            }
        }
        // FIGURE OUT WHY TEXT IS CUT SHORT
        // IT WAS THE & AFTER start_date I guess it is a keyword, just surround the argument with ""
        // To fix it.
        /*
        
        ALL THAT IS LEFT TO DO IS 
        OPTIMIZE THE PROGRAM 
        IMPLEMENT THE ABILITY TO CLEAR THE CACHE
        
        
        */
        else if (args.length != 2) {
            System.out.println("To start server run: caching-proxy --port <PORT NUMBER> --origin <URL>" + "\n" +
                               "To make request run: caching-proxy https://localhost:<PORT NUMBER>/<REQUEST>" + "\n" + 
                               "To reset server run: caching-proxy --clear-cache");
                                return;
        }
        else if (args[0].equals("caching-proxy") &
                args[1].contains("http://localhost:3000/"))        
        {          
                StringBuilder requestToForward = new StringBuilder(args[1].substring(21));
                String line;
                boolean cacheRetrieved = false;
                boolean toRequestOrNot = true;
                try {
                    BufferedReader readingFromFile = new BufferedReader(new FileReader("CachingProxy.csv"));
                    while((line = readingFromFile.readLine()) != null){
                        if (line.contains(requestToForward)) {
                            cacheRetrieved = true;
                            toRequestOrNot = false; 
                        }
                        else if (line.contains("cached")){
                            cacheRetrieved = false;
                        }
                        if (cacheRetrieved) {
                            System.out.println(line);
                        }
                    }
                    if (toRequestOrNot == false){
                        System.out.println("Retrieved from cache.");
                    }
                    readingFromFile.close();
                } catch (IOException e) {
                    System.out.println("Error.");
                }
                String URL = null;
                requestToForward.ensureCapacity(args[1].length() - 21);
                if (toRequestOrNot == true)
                {
                    try {
                        BufferedReader readingFromFile = new BufferedReader(new FileReader("CachingProxy.csv"));
                        readingFromFile.readLine();
                        URL = readingFromFile.readLine().substring(8);
                        readingFromFile.close();
                    } catch (IOException e) {
                        System.out.println("Error.");
                    }
                    URL += requestToForward.toString();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(URL))
                                .build();
                        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
                        System.out.println("Respone Body: " + response.body());
                        FileWriter writer = new FileWriter("CachingProxy.csv", true);
                        writer.write(System.lineSeparator() + "cached: " + requestToForward.toString());
                        writer.write(System.lineSeparator() + response.body());
                        writer.close();
                    } catch(IOException e) {
                        System.out.println("Error.");
                    } catch (InterruptedException e) {
                        System.out.println("Error.");
                    }
                }
        }
        else if (args[0].equalsIgnoreCase("caching-proxy") &
                 args[1].equalsIgnoreCase("--clear-cache")) 
        {
                File deletingFile = new File("CachingProxy.csv");
                if (deletingFile.delete()) {
                    System.out.println("Cache has been cleared.");
                } 
                else {
                    System.out.println("Failed to clear cache.");
                }
        } 
    }
}
