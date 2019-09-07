package com.company;

import java.io.*;
import java.net.URL;
import java.util.*;

public class WWWE {

    static final String BASE_URL = "https://en.wikipedia.org/";
    private final boolean DEBUGGING = true; // When set, report what's happening.
    private final String targetSearchQuery = "/wiki/Karin_Thürig";//This stores the String that we are searching for
    private String SEED_URL = "wiki/Greg_Mitchell";//this is appended to the BASE_URL to create the complete URL.
    private final int shingleLength = 5;//Stores the shinglelegnth used to calculate the simialrity
    private int nodesVisited = 0;
    boolean foundSolution = false;
    //Node is added to the Visited Hash Table only once all of its children have been explored!
    private HashSet<String> Visited = new HashSet<String>();
    //Node is added to the fringe when the node is observed for the first time!
    private LinkedList<SearchNode> fringe = new LinkedList<SearchNode>();
    //alreadyAdded will store the ALL the nodes that have been seen so far (Visited + fringe)
    private HashSet<String> alreadyAdded = new HashSet<>();
    //Priority Queue to sort the edges according to their corresponding edge weights
    private PriorityQueue<HyperLink> Edges = new PriorityQueue<>(Comparator.comparing(HyperLink::getHValue));
    private HashSet<String> linksInTarget = new HashSet<>();//This stores the links that are present in the target page specified by targetSearchQuery
    private String targetTextComponent;
    private int politeness = 0;//Timeout of 3s after howPolite number of page requests
    private int howPolite = 50;
    private int sleepTime = 3;

    public void MostFrequentWordsTarget(String targetURL) {

        ArrayList<String> myList = extractLinksOnTarget(targetURL);
        for (int i = 0; i < myList.size(); i++) {
            linksInTarget.add(myList.get(i));
        }
        //Set up the Target String that will act as Set2
        targetTextComponent = actualTextComponent(targetURL);
//        System.out.println(targetTextComponent);
        targetTextComponent = targetTextComponent.replaceAll("[^A-Za-z]+", "").toLowerCase();
        System.out.println(targetTextComponent);
        //So now I have a single block string with no punctuation and spaces
        //This I use as String 2 to calculate Hash Code Similarity
    }


    //Copied from WikiCrawler Class:
    public void BestFSearch() {

        MostFrequentWordsTarget(targetSearchQuery);
        String startNode = SEED_URL;

        //Create a root node
        SearchNode currentNode = new SearchNode(startNode);
        fringe.add(currentNode);
        Visited.add(currentNode.getNodeName());
        nodesVisited++;
        alreadyAdded.add(currentNode.getNodeName());
        //This should get the web page from the URL now.
//        rootTextContent = actualTextComponent(startNode);
        extractLinks(SEED_URL, currentNode);

        //Do the BFS Search
        while (fringe.peek() != null) {

            HyperLink maxHValueEdge = Edges.poll();

            if (maxHValueEdge != null) {
                if(DEBUGGING) System.out.println("Max edge to node: " +
                        maxHValueEdge.linkToPage  + "\n" +
                        " h1Value: " + maxHValueEdge.h1Value +"\n" +
                        " h2Value: " + maxHValueEdge.h2Value +"\n" +
                        " hValue: " + maxHValueEdge.hValue);
                String correspNode = maxHValueEdge.linkToPage;
                for (int i = 0; i < fringe.size(); i++) {
                    if (fringe.get(i).nodeName.equals(correspNode)) {
                        currentNode = fringe.remove(i);
                    }
                }
            }

            if (Edges.isEmpty()) break;

            if (currentNode != null) {
                //Add the node to Visited
                Visited.add(currentNode.getNodeName());
                nodesVisited++;
                if (DEBUGGING) System.out.println("Fringe gave: " + currentNode.getNodeName());

                // Go and fetch the contents of this file.
                String currentURL = currentNode.getNodeName();
//            if (DEBUGGING) System.out.println(contents);

                //Extract the html <A></A> tags from the html file
                //Add the Links to the Fringe if not Visited and not already in the fringe
                extractLinks(currentURL, currentNode);
                if (foundSolution) {
                    System.out.println("Nodes Visited: " + nodesVisited);
                    return;
                }
            }

        }
    }

    private String actualTextComponent(String seed_url) {
        String myString = "";
        try {
            URL url = new URL(BASE_URL + seed_url);
            InputStream is = url.openStream();
            politeness++;
            if (politeness > howPolite) {
                try {
                    Thread.sleep(sleepTime);
                    System.out.println("Sleeping......");
                } catch (Exception e) {
                    System.out.println(e);
                }
                politeness = 0;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder(1024);
            String s = "";
            while ((s = br.readLine()) != null) {
                s = s + "\n";
                content.append(s);
            }
            myString = content.toString();
            //System.out.println(content);
        } catch (IOException ex) {

            System.out.println(ex);
        }

        String splitString = "";
        if (myString.contains("<p>")) {
            String[] temp = myString.split("<p>", 2);
            splitString = "<p>" + temp[1];
        }

        String problemString = "<div role=\"navigation\" class=\"navbox\"";
        if (splitString.contains(problemString)) {
            String[] temp2 = splitString.split(problemString, 2);
            splitString = temp2[0];
//            System.out.println(splitString);
        }

        String referenceString = "<h2><span class=\"mw-headline\" id=\"References\">";
        if (splitString.contains(referenceString)) {
            String[] temp2 = splitString.split(referenceString, 2);
            splitString = temp2[0];
//            System.out.println(splitString);
        }

        //We need to extract the actual text component from the split string.
        //Searching for text within the > actual text <
        int startIndex = 0;
        int stopIndex;
        StringBuilder finalString = new StringBuilder(1024);
        String textComponent = "";//"actual text component" of the html page

        if (myString.contains("<p>")) {//Added this for Bad Links
            while (true) {
                startIndex = splitString.indexOf('>', startIndex);
                stopIndex = splitString.indexOf('<', startIndex - 1);
                String tempString = splitString.substring(startIndex + 1, stopIndex);
                finalString = finalString.append(tempString);
                if (splitString.indexOf('<', stopIndex + 1) == -1) {
                    break;
                } else {
                    startIndex = stopIndex;
                }

            }
            textComponent = finalString.toString();
//        textComponent = textComponent.replaceAll("(\\s)\\1","$1");
            textComponent = textComponent.replaceAll("\\s+", " ");
//        System.out.println(textComponent);
        }
        return textComponent;
    }

    private ArrayList<String> extractLinksOnTarget(String seed_url) {

        int startIndex = 0;
        int stopIndex;
        ArrayList<String> linksInThisHTML = new ArrayList<>();
        String pageInHTML = "";//contains entire page in html
        try {
            URL url = new URL(BASE_URL + seed_url);
            politeness++;
            if (politeness > howPolite) {
                try {
                    System.out.println("Sleeping......");
                    Thread.sleep(sleepTime);
                } catch (Exception e) {

                    System.out.println(e);
                }
                politeness = 0;
            }
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder(1024);
            String s = "";
            while ((s = br.readLine()) != null) {
                s = s + "\n";
                content.append(s);
            }
            br.close();
            //pageINHTML stores the page asItIs in HTML, with HTML code and everything
            pageInHTML = content.toString();
//            System.out.println(pageInHTML);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        if (pageInHTML.contains("<p>")) {//Added this for Bad Links
            String[] temp = pageInHTML.split("<p>", 2);
            pageInHTML = "<p>" + temp[1];

            //To get rid of all of the Links in the Collapsible Section of the Wikipedia Articles
            String problemString = "<div role=\"navigation\" class=\"navbox\"";
            if (pageInHTML.contains(problemString)) {
                String[] temp2 = pageInHTML.split(problemString, 2);
                pageInHTML =  temp2[0];
            }

            //Remove the References Section as well. Seems Useless.
            String referenceString = "<h2><span class=\"mw-headline\" id=\"References\">";
            if (pageInHTML.contains(referenceString)) {
                String[] temp2 = pageInHTML.split(referenceString, 2);
                pageInHTML = temp2[0];
//            System.out.println(splitString);
            }



//        System.out.println("**** SEED URL IS: " + seed_url + " ******" );
            if (pageInHTML.contains("\"/wiki/")) {
                while (true) {

                    startIndex = pageInHTML.indexOf("\"/wiki/", startIndex);
//            System.out.println("Start Index is: " + startIndex);
                    stopIndex = pageInHTML.indexOf("\"", startIndex + 1);
//            System.out.println("Stop Index is: " + stopIndex);
                    String tempString = pageInHTML.substring(startIndex + 1, stopIndex);
//            System.out.println("Link is:" + tempString);

                    linksInThisHTML.add(tempString);

                    if (pageInHTML.indexOf("\"/wiki/", stopIndex + 1) == -1) {
                        break;
                    } else {
                        startIndex = stopIndex;
                    }

                }
            }

        }
        return linksInThisHTML;
    }

    private void extractLinks(String seed_url, SearchNode parentNode) {

        int startIndex = 0;
        int stopIndex;

        String pageInHTML = "";//contains entire page in html
        try {
            URL url = new URL(BASE_URL + seed_url);
            politeness++;
            if (politeness > howPolite) {
                try {
                    System.out.println("Sleeping......");
                    Thread.sleep(sleepTime);
                } catch (Exception e) {

                    System.out.println(e);
                }
                politeness = 0;
            }
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder(1024);
            String s = "";
            while ((s = br.readLine()) != null) {
                s = s + "\n";
                content.append(s);
            }
            br.close();
            //pageINHTML stores the page asItIs in HTML, with HTML code and everything
            pageInHTML = content.toString();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        if (pageInHTML.contains("<p>")) {//Added this for Bad Links
            String[] temp = pageInHTML.split("<p>", 2);
            pageInHTML = "<p>" + temp[1];

            //To get rid of all of the Links in the Collapsible Section of the Wikipedia Articles
            String problemString = "<div role=\"navigation\" class=\"navbox\"";
            if (pageInHTML.contains(problemString)) {
                String[] temp2 = pageInHTML.split(problemString, 2);
                pageInHTML =  temp2[0];
            }

            //Remove the References Section as well. Seems Useless.
            String referenceString = "<h2><span class=\"mw-headline\" id=\"References\">";
            if (pageInHTML.contains(referenceString)) {
                String[] temp2 = pageInHTML.split(referenceString, 2);
                pageInHTML = temp2[0];
//            System.out.println(splitString);
            }

            if (pageInHTML.contains("\"/wiki/")) {
                while (true) {

                    startIndex = pageInHTML.indexOf("\"/wiki/", startIndex);
                    stopIndex = pageInHTML.indexOf("\"", startIndex + 1);
                    String tempString = pageInHTML.substring(startIndex + 1, stopIndex);

                    if (!(alreadyAdded.contains(tempString)) &&
                            !(seed_url.equals(tempString)) &&
                            !(tempString.contains("%") || tempString.contains(":") /*|| tempString.contains(".") */|| tempString.contains("usr/bin/god"))) {

                        SearchNode newNode = new SearchNode(tempString);
                        newNode.parent = parentNode;
                        HyperLink newEdge;
                        //Create a new edge between the parent and child node
                        if (parentNode == null) {
                            newEdge = new HyperLink(SEED_URL, newNode.nodeName);
                        } else {
                            newEdge = new HyperLink(parentNode.nodeName, newNode.nodeName);
                        }
                        //Add that to Edges Priority Queue
                        Edges.add(newEdge);
                        if (parentNode == null ) {
                            if(DEBUGGING) System.out.println(SEED_URL + "---" + newNode.nodeName +
                                    " hval: " + newEdge.hValue +
                                    " h1Val: " + newEdge.h1Value +
                                    " h2Val: " + newEdge.h2Value);
                        } else {
                            if(DEBUGGING) System.out.println(parentNode.nodeName + "---" + newNode.nodeName +
                                    " hval: " + newEdge.hValue +
                                    " h1Val: " + newEdge.h1Value +
                                    " h2Val: " + newEdge.h2Value);
                        }

                        //Check if the link is the final state
                        String textContent = newNode.nodeName;
                        if (textContent.equals(targetSearchQuery)) {
                            tracePath(newNode);
                            foundSolution = true;
                            return;
                        }
                        //Else add the file to the fringe
                        fringe.add(newNode);
                        alreadyAdded.add(tempString);
                    }

                    if (pageInHTML.indexOf("\"/wiki/", stopIndex + 1) == -1) {
                        break;
                    } else {
                        startIndex = stopIndex;
                    }

                }
            }
        }
    }

    private void tracePath(SearchNode s) {

        System.out.println("Trace Path: ");
        while (s.getParentNode() != null) {
            System.out.println(s.getNodeName());
            s = s.getParentNode();
        }
        System.out.println(s.getNodeName());
    }

    class HyperLink {

        public String linkFromPage = "";
        public String textComponent = "";
        public String linkToPage = "";
        private int hValue = 0;
        private int h1Value = 0;
        private int h2Value = 0;

        public HyperLink(String parentNode, String childNode) {

            linkFromPage = parentNode;
            linkToPage = childNode;
            calcTotalhValue();
        }

        public int getHValue() {
            return this.hValue;
        }

        public String getTextComponent() {
            return this.textComponent;
        }

        private void calcTotalhValue() {
            hValue = -( /*calcHeuristic1() + */ calcHeuristic3());
        }

        //This heuristic uses the Similarity between the pages to give an appropriate heuristic value to the page
        private int calcHeuristic1() {

            int queryCount = 0;
            ArrayList<String> set1 = new ArrayList<>();
            String completePath = linkToPage;
            String textContent = actualTextComponent(completePath);
            textContent = textContent.replaceAll("[^A-Za-z]+", "").toLowerCase();
            HashCodeSimilarity myHash = new HashCodeSimilarity(textContent, targetTextComponent, shingleLength);
            float simValue = myHash.similarity();
//            queryCount = returnQueryCount(textContent);
//            System.out.println("Heuristic 1 value: " + queryCount);

            /* Applies a selective weight to the similarities calculated
            if (simValue < 0.1){
                queryCount = (int) (simValue * 1000);
            }else if ( 0.1 < simValue && simValue < 0.2){
                queryCount = (int) (simValue * 2000);
            }else if ( 0.2 < simValue && simValue < 0.3){
                queryCount = (int) (simValue * 3000);
            }else if ( 0.3 < simValue && simValue < 0.4){
                queryCount = (int) (simValue * 4000);
            }else if ( 0.5 < simValue && simValue < 0.6){
                queryCount = (int) (simValue * 5000);
            }else{
                queryCount = (int) (simValue * 8000);
            }
            */

            queryCount = (int) (simValue * 1000);
            h1Value = queryCount;
            return queryCount;
        }


        private int calcHeuristic2(){

            int queryCount = 0;




            return queryCount;
        }

        //This heuristic will assign a value to the page depending upon how many wiki links it shares with the target page link
        private int calcHeuristic3() {

            int queryCount = 0;
            ArrayList<String> myList;
            myList = extractLinksOnTarget(linkToPage);
            for (int i = 0; i < myList.size(); i++) {
                if (linksInTarget.contains(myList.get(i))) {
                    queryCount += 1;
                }
            }
//            System.out.println("Heuristic 3 value: " + queryCount);
            h2Value = queryCount;
            return queryCount;
        }
    }


    // You'll need to design a Search node data structure.
    // Note that the above code assumes there is a method called getHvalue()
    // that returns (as a double) the heuristic value associated with a search node,
    // a method called getNodeName() that returns (as a String)
    // the name of the file (eg, "page7.html") associated with this node, and
    // a (void) method called reportSolutionPath() that prints the path
    // from the start node to the current node represented by the SearchNode instance.
    class SearchNode {


        private final String nodeName;
        private SearchNode parent = null;

        public SearchNode(String name) {
            nodeName = name;
        }

        public String getNodeName() {
            return nodeName;
        }

        public SearchNode getParentNode() {
            return this.parent;
        }
    }

}
