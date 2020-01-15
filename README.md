# isRelatedTo
Code to find out a path from Wikipedia page A to another Wikipedia page B. You can read more about the project at this link: https://rgbk21.github.io/Projects/isRelatedTo/isRelatedTo.html

The code to find the path is in the WWWE.java file. 

To run the code, make the following changes:
1) In the file WWWE.java, change the global variable - SEED_URL to your source wiki page link. This will be treated as the source vertex, u
2) In the same file, change the global variable - targetSearchQuery to your target wiki page link. This will be treated as the target vertex, v.
3) That's it. Go to Main.java and run the code.

The code will keep running until it finally outputs the path from u to v.
Do note that the SEED_URL does not have the forward slash in front while the targetSearchQuery does.
