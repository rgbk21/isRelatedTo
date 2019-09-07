package com.company;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

public class WikiCrawler {
    static final String BASE_URL = "https://en.wikipedia.org";
    private String SEED_URL;
    private int MAX;
    private String Name;
    private ArrayList<String> TOPICS;
    private int politeness = 0;
    private HashSet<String> VISITED = new HashSet<>();
    private LinkedList<String> BFSQueue = new LinkedList<>();



    public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName) {
        SEED_URL = seedUrl;
        MAX = max;
        TOPICS = topics;
        Name = fileName;
    }

    public void crawl() {


        try{
            PrintStream out = new PrintStream(new FileOutputStream(Name));
            System.setOut(out);}catch(Exception e){
            System.out.println(e);
        }

        String actualText;//contains actual text component
        actualText = actualTextComponent(SEED_URL);
//        System.out.println(actualText);
        if (searchForTopics(actualText))
        {
            System.out.println(MAX);
            VISITED.add(SEED_URL);
            BFSQueue.add(SEED_URL);
//            extractLinks(SEED_URL);
        } else {
            System.out.println("0");
        }

        String seed = "";
        while (!BFSQueue.isEmpty())
        {
            seed = BFSQueue.removeFirst();
            actualText = actualTextComponent(seed);
            if (searchForTopics(actualText))
            {
                extractLinks(seed);
            }
        }


    }

    private void extractLinks(String seed_url) {

        int startIndex = 0;
        int stopIndex;

        String pageInHTML = "";//contains entire page in html
        try {
            URL url = new URL(BASE_URL + seed_url);
            politeness++;
            if (politeness > 25) {
                try {
//                    System.out.println("Sleeping......");
                    Thread.sleep(3000);
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
            pageInHTML = content.toString();
//            System.out.println(pageInHTML);
        } catch (IOException ex) {
            System.out.println(ex);
        }


        HashSet<String> alreadyAdded = new HashSet<>();

        String[] temp = pageInHTML.split("<p>", 2);
        pageInHTML = "<p>" + temp[1];

//        System.out.println("**** SEED URL IS: " + seed_url + " ******" );
        if (pageInHTML.contains("\"/wiki/"))
        {
            while (true)
            {

                startIndex = pageInHTML.indexOf("\"/wiki/", startIndex);
//            System.out.println("Start Index is: " + startIndex);
                stopIndex = pageInHTML.indexOf("\"", startIndex + 1);
//            System.out.println("Stop Index is: " + stopIndex);
                String tempString = pageInHTML.substring(startIndex + 1, stopIndex);
//            System.out.println("Link is:" + tempString);

                if (VISITED.size() < MAX  && VISITED.contains(tempString) &&
                        !(alreadyAdded.contains(tempString)) && !(seed_url.equals(tempString)))
                {
//                    System.out.println("Adding edge 1");
                    System.out.println(seed_url + " " + tempString);
                    alreadyAdded.add(tempString);
                }

                if (VISITED.size() < MAX  && checkCriteria(tempString, seed_url))
                {
//                    System.out.println("Adding edge 2");

                    System.out.println(seed_url + " " + tempString);
                    VISITED.add(tempString);
                    BFSQueue.add(tempString);
                    alreadyAdded.add(tempString);
                }

                if (VISITED.size() == MAX)
                {
                    if (VISITED.contains(tempString) && !(alreadyAdded.contains(tempString)) &&
                            !(seed_url.equals(tempString)))
                    {
//                    System.out.println("Edge already formed with " + tempString + " is " +
//                            alreadyAdded.contains(tempString));
//                        System.out.println("Adding edge 3");
                        System.out.println(seed_url + " " + tempString);
                        alreadyAdded.add(tempString);
                    }

                }

                if (pageInHTML.indexOf("\"/wiki/", stopIndex + 1) == -1)
                {
                    break;
                } else {
                    startIndex = stopIndex;
                }

            }
        }
    }

    private boolean checkCriteria(String url, String seed_url) {

        if (url.contains("#") || url.contains(":")) {
//            System.out.println(url + " Failed (url.contains(\"#\") || url.contains(\":\")");
            return false;
        }
        if (url.equals(seed_url)) {
//            System.out.println(url + " Failed (url.equals(seed_url)");
            return false;
        }

        if (VISITED.contains(url)) {
//            System.out.println(url + " Failed (VISITED.contains(url))");
            return false;
        }

        if (!searchForTopics(actualTextComponent(url))) {
//            System.out.println(url + " Failed (!searchForTopics(actualTextComponent(url)) )");
            return false;
        }

        return true;

    }

    private String actualTextComponent(String seed_url) {
        String myString = "";
        try {
            URL url = new URL(BASE_URL + seed_url);
            InputStream is = url.openStream();
            politeness++;
            if (politeness > 25) {
                try {
                    Thread.sleep(3000);
//                    System.out.println("Sleeping......");
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

        String[] temp = myString.split("<p>", 2);
        String splitString = "<p>" + temp[1];
        //System.out.println("what i need: " + splitString);

        //We need to extract the actual text component from the split string.
        //Searching for text within the > actual text <

        int startIndex = 0;
        int stopIndex;
        StringBuilder finalString = new StringBuilder(1024);
        String textComponent = "";//"actual text component" of the html page
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
        return textComponent;

    }



    private boolean searchForTopics(String s) {
        if (TOPICS.isEmpty()) {
            return true;
        } else {
            for (int i = 0; i < TOPICS.size(); i++) {
                if (!s.contains(TOPICS.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}

