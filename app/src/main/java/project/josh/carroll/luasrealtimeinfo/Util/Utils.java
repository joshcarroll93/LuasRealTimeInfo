package project.josh.carroll.luasrealtimeinfo.Util;

import android.util.Log;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import project.josh.carroll.luasrealtimeinfo.Data.LuasStop;
import project.josh.carroll.luasrealtimeinfo.Data.Tram;

public class Utils {

    private ArrayList<LuasStop> redLine = new ArrayList<>();
    private ArrayList<LuasStop> greenLine = new ArrayList<>();
    private String[] args = new String[4];
    private ArrayList<Tram> trams = new ArrayList<>();

    //0 for red line, 1 for green line
    private static int redOrGreen = 0;

    public ArrayList<ArrayList<LuasStop>> getResponseForLuasStops(URL url){

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url.openStream());

            getNodes(doc);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        ArrayList<ArrayList<LuasStop>> routes = new ArrayList<>(2);
        routes.add(redLine);
        routes.add(greenLine);
        return routes;
    }

    public List<Tram> getResponseForRealTimeInfo(URL url){
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(url.openStream());

            getNodesForRealTime(doc);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < trams.size(); i++){

            Log.d("Stop: " , trams.get(i).getStop() + "\n"
                    + "Direction: " + trams.get(i).getDirection()+ "\n"
                    + "Destination: " + trams.get(i).getDestination() + "\n"
                    + "Due Minutes: " + trams.get(i).getDueTime());

        }
        return trams;
    }

    private void getNodes(Node node){

        NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++){

            Node currentNode = childNodes.item(i);

            if(currentNode.hasAttributes()){

                LuasStop luasStop = new LuasStop();
                for (int j = 0; j < currentNode.getAttributes().getLength(); j++){

                    String nodeName = currentNode.getAttributes().item(j).getNodeName();
                    String nodeValue = currentNode.getAttributes().item(j).getNodeValue();

                    if(currentNode.getAttributes().item(0).getNodeValue().equals("Luas Red Line"))
                        redOrGreen = 0;
                    else if(currentNode.getAttributes().item(0).getNodeValue().equals("Luas Green Line"))
                        redOrGreen = 1;

                    switch (nodeName){

                        case "abrev":
                            luasStop.setAbbreviation(nodeValue);
                            break;

                        case "isCycleRide":
                            luasStop.setCycleAndRide(nodeValue);
                            break;

                        case "isParkRide":
                            luasStop.setParkAndRide(nodeValue);
                            break;

                        case "lat":
                            luasStop.setLatitude(nodeValue);
                            break;

                        case "long":
                            luasStop.setLongitude(nodeValue);
                            break;

                        case "pronunciation":
                            if(nodeValue.equalsIgnoreCase("Phibsborough"))
                                luasStop.setPronunciation("Dalymount / Phibsborough");

                            else
                                luasStop.setPronunciation(nodeValue);
                            break;
                    }
                }

                if (redOrGreen == 0 && luasStop.getAbbreviation() != null)
                    redLine.add(luasStop);

                else if(redOrGreen == 1 && luasStop.getAbbreviation() != null)
                    greenLine.add(luasStop);

            }
            getNodes(currentNode);
        }
    }

    private void getNodesForRealTime(Node node) {

        NodeList childNodes = node.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {

            Node currentNode = childNodes.item(i);

            if (currentNode.hasAttributes()) {

                for (int j = 0; j < currentNode.getAttributes().getLength(); j++) {

                    Log.d("UTILS",currentNode.getAttributes().item(j).getNodeName()
                            + " : " + currentNode.getAttributes().item(j).getNodeValue());

                    String nodeName = currentNode.getAttributes().item(j).getNodeName();
                    String nodeValue = currentNode.getAttributes().item(j).getNodeValue();

                    switch (nodeName){

                        case "stop":
                            args[0] = nodeValue;
                            break;
                        case "name":
                            args[1] = nodeValue;
                            break;
                        case "destination":
                            args[2] = nodeValue;
                            break;
                        case "dueMins":
                            args[3] = nodeValue;
                            break;
                    }
                }
            }
            checkValidity();

            getNodesForRealTime(currentNode);
        }
    }

    private void checkValidity(){

        for(int i = 0; i < args.length; i++){

            if(args[i] == null)
                break;

            if(i == 3){
                Tram tram = new Tram();
                tram.setStop(args[0]);
                tram.setDirection(args[1]);
                tram.setDestination(args[2]);
                tram.setDueTime(args[3]);

                trams.add(tram);
            }
        }
    }
}
