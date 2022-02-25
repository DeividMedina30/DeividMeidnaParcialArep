package edu.escuelaing.arep;

import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.net.*;
import java.io.*;
import java.util.*;


public class HttpServer {

    private static Gson gson;
    private ServerSocket serverSocket;
    private Socket clientSocket;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        gson = new Gson();
        boolean running = true;

        try {
            serverSocket = new ServerSocket(getPort()); //sOCKET DE SERVIDOR, PUERTO 35000
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        while (running) {

            Socket clientSocket = null; //Cliente socket
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept(); //Se pone a aceptar conexiones
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); //Flujo de salida
            BufferedReader in = new BufferedReader( //Flujo de entrada
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = null;
            String file = "";
            String[] fileNewConsulta ;
            boolean primeraLinea = true;
            while ((inputLine = in.readLine()) != null) {
                if (primeraLinea) {
                    file = inputLine.split(" ")[1];
                    primeraLinea = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println(file);
            //Siempre responde la misma página
            if(file.startsWith("/Clima")) {
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text /html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Consultando Clima</title>\n"
                        + "</head>"
                        + "<body>"
                        + "<h1> Consultando Clima </h1>"
                        + "<p>Consulte el Clima de una Ciudad o Lugar</p>"
                        + "<div  id='obtenerConsultaClimaidiv'></div>"
                        + "<form action= '/consulta' methord='get'>"
                        + "<input id= 'climatextid' type= 'text'   placeholder='Ingrese la Ciudad o lugar del cual desea conocer el Clima. ' >"
                        + "<input id='climaButton' type='submit' value='Submit' >"
                        + "</body>"
                        + "</html>" + inputLine;
            }else if (file.contains("/consulta?lugar=")){
                fileNewConsulta = file.split("=");
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text /html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Consultando Una consulta</title>\n"
                        + "</head>"
                        + "<body>"
                        + "<h1> Consultando el clima de una Ciudad o Lugar. </h1>"
                        + "</body>"
                        + "</html>" + inputLine;
                outputLine += CreandoConexionApi(fileNewConsulta[1]);
            }else{
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text /html\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html>"
                        + "<html>"
                        + "<head>"
                        + "<meta charset=\"UTF-8\">"
                        + "<title>Consultando Clima</title>\n"
                        + "</head>"
                        + "<body>"
                        + "<h1> Consultando Clima </h1>"
                        + "<p>Consulte el Clima de una Ciudad o Lugar</p>"
                        + "<div  id='obtenerConsultaClimaidiv'></div>"
                        + "<form action= '/consulta' methord='get'>"
                        + "<input id= 'climatextid' type= 'text'   placeholder='Ingrese la Ciudad o lugar del cual desea conocer el Clima. ' >"
                        + "<input id='climaButton' type='submit' value='Submit' >"
                        + "</body>"
                        + "</html>" + inputLine;
            }

            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public static String CreandoConexionApi(String Ciudad) throws IOException {

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + Ciudad +  "&appid=bc337958c219159e9a81d75502e4204d";
        URL urlClima = new URL(url);
        HttpsURLConnection conectarUrl = (HttpsURLConnection)urlClima.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(conectarUrl.getInputStream()));
        String input, output = "";
        while ((input = in.readLine()) != null){
            output += input;
        }
        return gson.toJson(output);
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set(i.e. on localhost)
    }

}
