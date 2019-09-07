package com.company;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class BestFirstSearch {

    final boolean DEBUGGING = true; // When set, report what's happening.
    final String START_NODE = "page1.html";
    final String directoryName = "C:\\Semester 3\\COM S 572 - AI\\Lab1\\intranets\\intranet7";

    int nodesVisited = 0;
    boolean foundSolution = false;
    //Node is added to the Visited Hash Table only once all of its children have been explored!
    HashSet<String> Visited = new HashSet<String>();
    //Node is added to the fringe when the node is observed for the first time!
//    LinkedList<SearchNode> fringe   = new LinkedList<SearchNode>();
    LinkedList<SearchNode> fringe = new LinkedList<SearchNode>();
    //This will store the ALL the nodes that have been seen so far (Visited + fringe)
    HashSet<String> alreadyAdded = new HashSet<>();
    PriorityQueue<HyperLink> Edges = new PriorityQueue<>(Comparator.comparing(HyperLink::getHValue));


    public void BestFSearch(){


        String startNode = START_NODE;
        String rootTextContent = "";
        //Create a root node
        SearchNode currentNode = new SearchNode(startNode);
        fringe.add(currentNode);
        Visited.add(currentNode.getNodeName());
        nodesVisited++;
        alreadyAdded.add(currentNode.getNodeName());
        rootTextContent = getFileContents(directoryName+File.separator+START_NODE);
        extractLinks(rootTextContent, currentNode);

        //Do the BFS Search
        while(fringe.peek() != null) {

            HyperLink maxHValueEdge = Edges.poll();

            if(maxHValueEdge != null) {
                System.out.println("Max edge to node: " + maxHValueEdge.linkToPage);
                String correspNode = maxHValueEdge.linkToPage;
                for(int i = 0; i < fringe.size(); i++){
                    if(fringe.get(i).nodeName.equals(correspNode)){
                        currentNode = fringe.remove(i);
                    }
                }
            }

            if(Edges.isEmpty()) break;

            if (currentNode != null) {
                //Add the node to Visited
                Visited.add(currentNode.getNodeName());
                nodesVisited++;
                if(DEBUGGING) System.out.println("Fringe gave: " + currentNode.getNodeName());

                // Go and fetch the contents of this file.
                String contents = getFileContents(directoryName
                        + File.separator
                        + currentNode.getNodeName());
//            if (DEBUGGING) System.out.println(contents);

                //Extract the html <A></A> tags from the html file
                //Add the Links to the Fringe if not Visited and not already in the fringe
                extractLinks(contents, currentNode);
                if (foundSolution) {
                    System.out.println("Nodes Visited: " + nodesVisited);
                    return;
                }
            }

        }
    }

    private void extractLinks(String contents, SearchNode currentNode){

        int startIndex = 0;
        int stopIndex;
        String completeATag = "";

        if (contents.contains("<A HREF"))
        {
            while (true)
            {
                startIndex = contents.indexOf("<A HREF", startIndex);
                stopIndex = contents.indexOf(">", startIndex + 1);
                String tempString = contents.substring(startIndex + 10, stopIndex - 1);

                stopIndex = contents.indexOf(">", stopIndex + 1);
                completeATag = contents.substring(startIndex , stopIndex + 1);

                //If the page is not present in alreadyAdded, add it.
                if ( ! alreadyAdded.contains(tempString) )
                {
                    SearchNode newNode = new SearchNode(tempString);
                    newNode.parent = currentNode;
                    //Create a new edge between the parent and child node
                    HyperLink newEdge = new HyperLink(completeATag, currentNode.nodeName);
                    //Add that to Edges Priority Queue
                    Edges.add(newEdge);
                    System.out.println(currentNode.nodeName + "---" + newNode.nodeName +" hval: " + newEdge.hValue );
                    //Check if the link is the final state
                    String textContent = getFileContents(directoryName + File.separator + tempString);
                    if (textContent.contains("QUERY1 QUERY2 QUERY3 QUERY4")){
                        tracePath(newNode);
                        foundSolution = true;
                        return;
                    }
                    //Else add the file to the fringe
                    fringe.add(newNode);
                    alreadyAdded.add(tempString);
                }

                if (contents.indexOf("<A HREF", stopIndex + 1) == -1)
                {
                    break;
                } else {
                    startIndex = stopIndex;
                }

            }
        }
    }

    // This method will read the contents of a file, returning it
    // as a string.  (Don't worry if you don't understand how it works.)
    //Note: this requires the complete path. NOT just the file name.
    public static synchronized String getFileContents(String fileName)
    {
        File file = new File(fileName);
        String results = null;

        try
        {
            int length = (int)file.length(), bytesRead;
            byte byteArray[] = new byte[length];

            ByteArrayOutputStream bytesBuffer = new ByteArrayOutputStream(length);
            FileInputStream inputStream = new FileInputStream(file);
            bytesRead = inputStream.read(byteArray);
            bytesBuffer.write(byteArray, 0, bytesRead);
            inputStream.close();

            results = bytesBuffer.toString();
        }
        catch(IOException e)
        {
            System.out.println("Exception in getFileContents(" + fileName + "), msg=" + e);
        }

        return results;
    }

    private void tracePath(SearchNode s){

        System.out.println("Trace Path: ");
        while(s.getParentNode() != null){
            System.out.println(s.getNodeName());
            s = s.getParentNode();
        }
        System.out.println(s.getNodeName());
    }


    class HyperLink{

        private int hValue = 0;
        public String linkFromPage = "";
        private String completeATag = "";
        public String textComponent = "";
        public String linkToPage = "";

        public HyperLink(String aTag, String parentNode){

            int startIndex = 0;
            int stopIndex = 0;
            linkFromPage = parentNode;
            completeATag = aTag;

//            System.out.println("String passed to HyperLink: " + completeATag);
            //Extracting the link to the page pointed to by the <A> tag
            startIndex = completeATag.indexOf("<A", startIndex);
            stopIndex = completeATag.indexOf(">", startIndex + 1);
            linkToPage = completeATag.substring(startIndex + 10, stopIndex - 1);
            //Extracting the text component of the <A> tag
            startIndex = completeATag.indexOf(">", startIndex);
            stopIndex = completeATag.indexOf("<", startIndex + 1);
            textComponent = completeATag.substring(startIndex + 2, stopIndex - 1);
            calcTotalhValue();
        }

        public int getHValue(){
            return this.hValue;
        }

        public String getTextComponent(){
            return this.textComponent;
        }

        private void calcTotalhValue(){
//            System.out.println("Pre hValue: " + this.hValue);
            hValue = -(calcHeuristic1() + calcHeuristic2() + calcHeuristic3());
//            System.out.println("Post hValue: " + this.hValue);

        }

        //This heuristic counts the number of QUERY appearing the page
        private int calcHeuristic1() {


            int queryCount = 0;
            String completePath = directoryName + File.separator + linkToPage;
            String textContent = getFileContents(completePath);
            queryCount = returnQueryCount(textContent);
//            System.out.println("Heuristic 1 value: " + queryCount);
            return queryCount;
        }

        //This heuristic will calculate the number of QUERY in the textComponent of the tag
        private int calcHeuristic2(){
//            System.out.println("Heuristic 2 value: " + returnQueryCount(textComponent));
            int queryCount = returnQueryCount(textComponent);
            queryCount = queryCount * 3;
            return queryCount;
        }

        //This will calculate if the number of QUERY appear in the correct order
        private int calcHeuristic3(){

            int queryCount = 0;

            if(textComponent.contains("QUERY")){

                if(textComponent.contains("QUERY1")) {
                    if (textComponent.indexOf("QUERY1") < textComponent.indexOf("QUERY2")) queryCount++;
                    if (textComponent.indexOf("QUERY1") < textComponent.indexOf("QUERY3")) queryCount++;
                    if (textComponent.indexOf("QUERY1") < textComponent.indexOf("QUERY4")) queryCount++;
                }

                if(textComponent.contains("QUERY2")) {
                    if (textComponent.indexOf("QUERY2") < textComponent.indexOf("QUERY3")) queryCount++;
                    if (textComponent.indexOf("QUERY2") < textComponent.indexOf("QUERY4")) queryCount++;
                }

                if(textComponent.contains("QUERY3")) {
                    if (textComponent.indexOf("QUERY3") < textComponent.indexOf("QUERY4")) queryCount++;
                }
            }
//            System.out.println("Heuristic 3 value: " + queryCount);
            queryCount = queryCount * 5;
            return queryCount;
        }
        //Returns the number of occurrence of the string QUERY in the string s.
        private int returnQueryCount(String s){

            String textContent = s;
            int startIndex = 0;
            int stopIndex = 0;
            int queryCount = 0;

            if (textContent.contains("QUERY")) {

                while (true) {
                    startIndex = textContent.indexOf("QUERY", startIndex);
                    stopIndex = startIndex + 5;
//                    if(DEBUGGING) {
//                        String t = textContent.substring(startIndex, stopIndex);
//                        System.out.println(t);
//                    }
                    queryCount++;
                    if (textContent.indexOf("QUERY", stopIndex + 1) == -1) {
                        break;
                    } else {
                        startIndex = stopIndex;
                    }
                }
            }
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
