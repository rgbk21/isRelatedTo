package com.company;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;

public class DFS {

    final boolean DEBUGGING = true; // When set, report what's happening.
    final String START_NODE = "page1.html";
    final String directoryName = "C:\\Semester 3\\COM S 572 - AI\\Lab1\\intranets\\intranet1";

    int nodesVisited = 0;
    boolean foundSolution = false;
    //Node is added to the Visited Hash Table only once all of its children have been explored!
    HashSet<String> Visited = new HashSet<String>();
    //Node is added to the fringe when the node is observed for the first time!
    LinkedList<SearchNode> fringe   = new LinkedList<SearchNode>();
    //This will store the ALL the nodes that have been seen so far (Visited + fringe)
    HashSet<String> alreadyAdded = new HashSet<>();

    public void DFSSearch(){


        String startNode = START_NODE;

        SearchNode rootNode = new SearchNode(startNode);

        SearchNode currentNode = rootNode;
        fringe.add(currentNode);
        Visited.add(currentNode.getNodeName());
        if(DEBUGGING) System.out.println(currentNode.getNodeName());
        nodesVisited++;
        alreadyAdded.add(currentNode.getNodeName());

        //Do the DFS Search
        while(!fringe.isEmpty()) {
            //Remove the node in the front of the queue
            currentNode = fringe.removeFirst();
            //Add the node to Visited
            Visited.add(currentNode.getNodeName());
            if(DEBUGGING) System.out.println(currentNode.getNodeName());
            nodesVisited++;

            // Go and fetch the contents of this file.
            String contents = getFileContents(directoryName
                    + File.separator
                    + currentNode.getNodeName());
//            if (DEBUGGING) System.out.println(contents);

            //Extract the html <A></A> tags from the html file
            //Add the Links to the Fringe if not Visited and not already in the fringe
            extractLinks(contents, currentNode);
            if(foundSolution){
                System.out.println("Nodes Visited: " + nodesVisited);
                return;
            }

        }
    }

    private void extractLinks(String contents, SearchNode currentNode){

        int startIndex = 0;
        int stopIndex;

        if (contents.contains("<A HREF"))
        {
            while (true)
            {

                startIndex = contents.indexOf("<A HREF", startIndex);
//            System.out.println("Start Index is: " + startIndex);
                stopIndex = contents.indexOf(">", startIndex + 1);
//            System.out.println("Stop Index is: " + stopIndex);
                String tempString = contents.substring(startIndex + 10, stopIndex - 1);
//                System.out.println("Link is:" + tempString + "***");

                //If the page is not present in alreadyAdded, add it.
                if ( ! alreadyAdded.contains(tempString) )
                {
                    SearchNode newNode = new SearchNode(tempString);
                    newNode.parent = currentNode;
                    //Check if the link is the final state
                    String textContent = getFileContents(directoryName + File.separator + tempString);
                    if (textContent.contains("QUERY1 QUERY2 QUERY3 QUERY4")){
                        tracePath(newNode);
                        foundSolution = true;
                        return;
                    }
                    //Else add the file to the fringe
                    fringe.addFirst(newNode);
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

    // You'll need to design a Search node data structure.

    // Note that the above code assumes there is a method called getHvalue()
    // that returns (as a double) the heuristic value associated with a search node,
    // a method called getNodeName() that returns (as a String)
    // the name of the file (eg, "page7.html") associated with this node, and
    // a (void) method called reportSolutionPath() that prints the path
    // from the start node to the current node represented by the SearchNode instance.
    class SearchNode
    {
        private final String nodeName;
        private SearchNode parent = null;

        public SearchNode(String name) {
            nodeName = name;
        }

        public void reportSolutionPath() {
        }

        public String getNodeName(){
            return nodeName;
        }

        public SearchNode getParentNode(){
            return this.parent;
        }
    }


}
