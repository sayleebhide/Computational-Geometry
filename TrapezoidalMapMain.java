/**
 * Created by sayleebhide on 11/8/17.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.*;

public class TrapezoidalMapMain {

    public int numPoints;

    //Array containing randomized lines

    public static ArrayList<TLine> lineArray = new ArrayList<TLine>();

    //bounding box
    public boundingbox box;

    //Search structure
    public static TNode root;

    //curent trapezoid list
    ArrayList<TTrapezoid> currentTrapezoidList = new ArrayList<TTrapezoid>();

    //adjacency matrix
    public int adjacencyMatrix[][];

    //Function to read file
    public void readFile(String fileName){

        //reading one line at at time
        String line = null;

        TPoint point1;

        TPoint point2;

        try {

            //reads text files in default encoding
            FileReader fileReader = new FileReader(fileName);

            //wraped in bufferedreader
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read no of points in the file
            numPoints = Integer.parseInt(bufferedReader.readLine());

            //initialize Line array
            //lineArray = new TLine[numPoints];

            //keep track of line number
            int lineNo = 0;

            //while file doesnt reach end of line
            while((line = bufferedReader.readLine())!=null && lineNo <= numPoints ) {

                //bounding box
                if (lineNo == 0) {

                    String[] splitLine = line.split("\\s+");


                    int x1 = Integer.parseInt(splitLine[0]);

                    int y1 = Integer.parseInt(splitLine[1]);

                    int x2 = Integer.parseInt(splitLine[2]);

                    int y2 = Integer.parseInt(splitLine[3]);

                    point1 = new TPoint(x1, y1);
                    point2 = new TPoint(x2, y2);

                    box = new boundingbox(point1,point2);

                    //find remaining bounds
                    box.findBounds();


                } else {
                    //split using space delimitter into x and y to store in point DS
                    String[] splitLine = line.split("\\s+");

                    //store as x and y co
                    int x1 = Integer.parseInt(splitLine[0]);

                    int y1 = Integer.parseInt(splitLine[1]);

                    int x2 = Integer.parseInt(splitLine[2]);

                    int y2 = Integer.parseInt(splitLine[3]);


                    //create Startingpoing and ending point of line segment
                    point1 = new TPoint(x1, y1);
                    point2 = new TPoint(x2, y2);

                    //create new set of point objects and store it in pointhull DS

                    if (x1 > x2)
                        //lineArray[lineNo] = new TLine(point2, point1);
                        lineArray.add(new TLine(point2, point1));
                    else
                        lineArray.add(new TLine(point1, point2));
                    //lineArray[lineNo] = new TLine(point1, point2);
                }

                lineNo++;

            }

            bufferedReader.close();


            //System.out.println("The no of lines are" + lineArray.length);

        }

        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error '" + fileName + "'");

        }
        //System.out.println("num of points are" + numPoints);


    }

    //intersection function to find IP which act like bounding vertices
    public TPoint findIntersectionOfLineWithSegmentsOfTrapezoid(TPoint L , TLine line, TLine bound){

        boolean lineSegment1 = false;
        boolean lineSegment2 = false;

        TPoint N = line.getA();
        TPoint O = line.getB();

        //For line L---M
        double A1 = 1;
        double B1 = 0;
        double C1 = A1*L.x + B1*L.y;

        //For line N----0
        double A2 = O.y - N.y;
        double B2 = N.x - O.x;
        double C2 = A2 * N.x + B2 * N.y;

        double det  = A1 * B2 - A2 * B1;

        //Intersecting points are
        double x_int  = (B2 * C1 - B1 * C2)/det;
        double y_int  = Math.abs((A1 * C2 - A2 * C1)/det);

        //since its a line segment make sure your point x_int , y_int lies on both of your line segments
        //if line is top segment then line.y > bound.y else if line is bottom segment, line.y<bound.y

        //check for L---M
        if(y_int >= Math.min(line.P.y,bound.P.y) && y_int <= Math.max(line.P.y,bound.P.y) ){
            lineSegment1 = true;
        }

        //check for M---N
        if( x_int >= Math.min(N.x , O.x) && x_int <= Math.max(N.x , O.x) && y_int >= Math.min(N.y , O.y) && y_int <= Math.max(N.y , O.y) ){
            lineSegment2 = true;
        }

        //if present on both line segments
        if(lineSegment1&&lineSegment2){

            return new TPoint(x_int,y_int);
            //System.out.println("Added" + " " + x_int + " "+ y_int);
        }
        return null;
    }

    //To find if point lies above or below the line
    public boolean findPointAboveBelow(TPoint p, TLine line){


            //(y2 - y1)*(x3 - x2) - (y3 - y2)*(x2 - x1)
        /*
        Above expression is negative when counterclockwise - LEFT TURN
        Above expression is 0 when  collinear
        Above expression is positive when  clockwise - RIGHT TURN
         */
        double orientation = (line.getB().y - line.getA().y) * (p.x - line.getB().x) - (p.y-line.getB().y)*(line.getB().x-line.getA().x);

        if (orientation<0)
                return true;

        return false;

    }

    //which trapezoid contains the point
    public TTrapezoid findTrapezoidWhichHasPoint(TPoint Lpoint, TPoint Rpoint , TLine line){

        //Trace through search structure to get trapezoid that has the point
        TNode searchNode = root;

        //while we dont encounter a leaf node
        while (!(searchNode instanceof Leaf)){

            //1. If it is an XNode (LEFT/RIGHT)
            if(searchNode instanceof XNode){

                if (Lpoint.x < ((XNode) searchNode).point.x){
                    searchNode = searchNode.getLeft();
                    //System.out.println(((XNode) searchNode).point.x);
                    System.out.println("here in xNODE LEFT");
                }
                else{
                    searchNode = searchNode.getRight();
                    //System.out.println(((XNode) searchNode).point.x);
                    System.out.println("here in xNODE right");
                }
            }

            //2. If it is a YNode (TOP/BOTTOM)
            if(searchNode instanceof YNode){

                if(findPointAboveBelow(Lpoint,((YNode)searchNode).line)){
                    System.out.println((Lpoint.x));
                    searchNode = searchNode.getLeft();

                    System.out.println("here in yNODE LEFT");
                }
                else{
                    System.out.println((Lpoint.x) +" " + ((YNode)searchNode).line.getA().x + " " + ((YNode)searchNode).line.getB().x );
                    searchNode = searchNode.getRight();
                    //System.out.println(((YNode) searchNode).line.P.x);
                    System.out.println("here in yNODE right");
                }
            }
        }

        //Casting
        Leaf returnNode = (Leaf)searchNode;

        //return trapezoid
        return returnNode.trapezoid;
    }

    //find the list of intersecting trapezoids
    public ArrayList<TTrapezoid> findIntersectingTrapezoidsList(TLine line){

        //List of intersecting trapezoids
        ArrayList<TTrapezoid> intersectingTrapezoids = new ArrayList<>();

        //find which trapezoid contains the point and add to list
        TTrapezoid firstT = findTrapezoidWhichHasPoint(line.getA(), line.getB(), line);
        intersectingTrapezoids.add(firstT);

        System.out.println("First added" + firstT.lbv.x + " " + firstT.rbv.x);

        if(currentTrapezoidList.size()>1) {

            while (line.getB().x > firstT.rbv.x) {

                if (findPointAboveBelow(firstT.rbv, line)) {
                    firstT = firstT.lowerRightNeighbour;
                    System.out.println("Adding LR" +  " " + firstT.lbv.x + " " + firstT.rbv.x);
                }
                else {
                    firstT = firstT.upperRightNeighbour;
                    System.out.println("Adding UR" +  " " + firstT.lbv.x + " " + firstT.rbv.x);
                }

                intersectingTrapezoids.add(firstT);
            }
        }

        return intersectingTrapezoids;
    }

    // main algorithm
    public void incrementalAlgorithm(){

        //#1. Add bounding box as initial trapezoid
        //we know that every leaf corresponds to trapezoid

        //create a new trapezoid that is the bounding box
        TTrapezoid t = new TTrapezoid(box.ll,box.lr,new TLine(box.ul,box.ur),new TLine(box.ll,box.lr));
        //currentTrapezoidList.add(t);

        //add trapezoid as leaf and also add it to trapezoid list
        Leaf leaf = new Leaf(t,"T0");

        //initialize history graph assigning first trapezoid
        root = leaf;

        System.out.println("current trapezoid list was" + currentTrapezoidList.size());

        //root.traverseInorder(root);

        //#2. Build incremental algorithm by inserting each line
        int count=0;

        int trap_count=0;

        for(TLine line : lineArray){
            count++;
            //#1. Which trapezoid(s) the line intersects?

            //Let intersectingTrapezoids be the list of trapezoids the line intersects
            ArrayList<TTrapezoid> intersectingTrapezoids = new ArrayList<>();

            System.out.println("line" + " " + line.P.x + " " + line.P.y);

            intersectingTrapezoids = findIntersectingTrapezoidsList(line);

            System.out.println("It is intersecting " + intersectingTrapezoids.size() + " \tTRAPEZOIDS");


            //#2. Update Trapezoidal Map and Search Structure


            //Case 1: Two segment endpoints lie within the trapezoid

            //Single trapezoid is replaced by four other trapezoids

            //creat two xnodes each for each end point

            //create ynode for line segment
            if(intersectingTrapezoids.size()==1){

                //old trapezoid
                TTrapezoid initialTrapezoid = intersectingTrapezoids.get(0);
                //currentTrapezoidList.remove(initialTrapezoid);
                //left trapezoid
                TTrapezoid leftTrapezoid = new TTrapezoid(initialTrapezoid.lbv,line.getA(),initialTrapezoid.topSegment,initialTrapezoid.bottomSegment);
                currentTrapezoidList.add(leftTrapezoid);

                //Top trapezoid
                TTrapezoid topTrapezoid = new TTrapezoid(line.getA(),line.getB(),initialTrapezoid.topSegment,initialTrapezoid.bottomSegment);
                currentTrapezoidList.add(topTrapezoid);

                //Bottom trapezoid
                TTrapezoid bottomTrapezoid = new TTrapezoid(line.getA(),line.getB(),initialTrapezoid.topSegment,initialTrapezoid.bottomSegment);
                currentTrapezoidList.add(bottomTrapezoid);

                //Right trapezoid
                TTrapezoid rightTrapezoid = new TTrapezoid(line.getB(),initialTrapezoid.rbv,initialTrapezoid.topSegment,initialTrapezoid.bottomSegment);
                currentTrapezoidList.add(rightTrapezoid);


                //System.out.println("current trapezoid list was" + currentTrapezoidList.size());

                //create xnode for left endpoing
                XNode leftEndPoint = new XNode(line.getA(),"P"+count);

                //create xnode for right endpoint
                XNode rightEndPoint = new XNode(line.getB(),"Q"+count);

                //create ynode for line segment
                YNode segmentNode = new YNode(line,"S"+count);

                //add neighbours
                leftTrapezoid.upperRightNeighbour = topTrapezoid;
                leftTrapezoid.lowerRightNeighbour = bottomTrapezoid;

                topTrapezoid.upperRightNeighbour = rightTrapezoid;
                topTrapezoid.upperLeftNeighbour = leftTrapezoid;
                topTrapezoid.lowerRightNeighbour = rightTrapezoid;
                topTrapezoid.lowerLeftNeighbour = leftTrapezoid;

                bottomTrapezoid.upperRightNeighbour =rightTrapezoid;
                bottomTrapezoid.lowerRightNeighbour = rightTrapezoid;
                bottomTrapezoid.upperLeftNeighbour = leftTrapezoid;
                bottomTrapezoid.lowerLeftNeighbour = leftTrapezoid;

                rightTrapezoid.upperLeftNeighbour = topTrapezoid;
                rightTrapezoid.lowerLeftNeighbour = bottomTrapezoid;

                //create trapezoid leaves
                Leaf leftTrapezoidLeaf = new Leaf(leftTrapezoid,"T"+trap_count);
                leftTrapezoid.setName("T"+trap_count);
                trap_count++;
                Leaf rightTrapezoidLeaf = new Leaf(rightTrapezoid,"T"+trap_count);
                rightTrapezoid.setName("T"+trap_count);
                trap_count++;
                Leaf topTrapezoidLeaf = new Leaf(topTrapezoid,"T"+trap_count);
                topTrapezoid.setName("T"+trap_count);
                trap_count++;
                Leaf bottomTrapezoidLeaf = new Leaf(bottomTrapezoid,"T"+trap_count);
                bottomTrapezoid.setName("T"+trap_count);
                trap_count++;

                //create following structure
                leftEndPoint.left = leftTrapezoidLeaf;
                leftEndPoint.right = rightEndPoint;

                rightEndPoint.left = segmentNode;
                rightEndPoint.right = rightTrapezoidLeaf;

                segmentNode.left = topTrapezoidLeaf;
                segmentNode.right = bottomTrapezoidLeaf;

                //find and replace existing trapezoid with following structure
                TNode searchNode = root;

                //only one i.e if trapezoid was bounding box
                if(root instanceof Leaf){

                    root = leftEndPoint;
                    //currentTrapezoidList.remove(initialTrapezoid);
                }
                //if many other trapezoids exist
                else {
                    while (!(searchNode instanceof Leaf)) {

                        if (searchNode instanceof XNode) {

                            if (initialTrapezoid.rbv.x == ((XNode) searchNode).point.x) {
                                if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(initialTrapezoid)) {
                                    //currentTrapezoidList.remove(initialTrapezoid);
                                    searchNode.left = leftEndPoint;
                                }

                                searchNode = searchNode.getLeft();
                            } else {
                                if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(initialTrapezoid)) {
                                    //currentTrapezoidList.remove(initialTrapezoid);
                                    searchNode.right = leftEndPoint;
                                }
                                searchNode = searchNode.getRight();
                            }

                        }

                        if (searchNode instanceof YNode) {

                            if (findPointAboveBelow(line.P,((YNode)searchNode).line)) {
                                if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(initialTrapezoid)) {
                                    //currentTrapezoidList.remove(initialTrapezoid);
                                    searchNode.left = leftEndPoint;
                                }
                                searchNode = searchNode.getLeft();
                            } else {
                                if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(initialTrapezoid)) {
                                    //currentTrapezoidList.remove(initialTrapezoid);
                                    searchNode.right = leftEndPoint;
                                }
                                searchNode = searchNode.getRight();
                            }
                        }

                    }
                }

                //root.traverseInorder(root);
                System.out.println("current Trapezoid list was" + currentTrapezoidList.size());
                System.out.println("---------------------");
            }


            else {
                System.out.println("The size of intersecting is" + intersectingTrapezoids.size());

                while (intersectingTrapezoids.size() != 0) {

                    //Case 2: Single left/right endpoint lies in the trapezoid

                    //Single trapezoid is replaced by three other trapezoids

                    //let p denote the end point that is within the trapezoid

                    // create xnode for p whose left child is the leaf trapezoid

                    // create ynode for the segment  whos left and right child are two trapezoids present top and below


                    //first trapezoid will be the one in wich the left end point lies so that will get divided in three trapezoids

                    TTrapezoid firstTrapezoid = intersectingTrapezoids.get(0);
                    currentTrapezoidList.remove(firstTrapezoid);
                    String name = firstTrapezoid.getName();

                    //change the bounding vertices and segments to divide in 3 parts
                    //change the bounding vertices and segments to divide in 3 parts
                    TPoint lp_ts,lp_bs;
                    TTrapezoid topTrapezoid,bottomTrapezoid;

                    TTrapezoid leftTrapezoid = new TTrapezoid(firstTrapezoid.lbv, line.getA(), firstTrapezoid.topSegment, firstTrapezoid.bottomSegment);
                    currentTrapezoidList.add(leftTrapezoid);

                    if(findPointAboveBelow(firstTrapezoid.rbv,line)) {
                        //bottom trapezoid wall trimmed
                        lp_ts = findIntersectionOfLineWithSegmentsOfTrapezoid(firstTrapezoid.rbv, line, firstTrapezoid.bottomSegment);
                        topTrapezoid = new TTrapezoid(line.getA(), firstTrapezoid.rbv, firstTrapezoid.topSegment, line);
                        bottomTrapezoid = new TTrapezoid(line.getA(), line.getB(), line, firstTrapezoid.bottomSegment);
                        currentTrapezoidList.add(topTrapezoid);
                    }
                    else {
                        //top trapezoid wall trimmed
                        lp_bs = findIntersectionOfLineWithSegmentsOfTrapezoid(firstTrapezoid.rbv, line, firstTrapezoid.topSegment);
                        topTrapezoid = new TTrapezoid(line.getA(), line.getB(), firstTrapezoid.topSegment, line);
                        bottomTrapezoid = new TTrapezoid(line.getA(), firstTrapezoid.rbv, line, firstTrapezoid.bottomSegment);
                        currentTrapezoidList.add(bottomTrapezoid);
                    }

                    XNode leftEndPoint = new XNode(line.getA(), "P" + count);

                    YNode segmentNode = new YNode(line, "S" + count);

                    if (leftTrapezoid.topSegment == topTrapezoid.topSegment)
                        leftTrapezoid.upperRightNeighbour = topTrapezoid;

                    if (leftTrapezoid.bottomSegment == topTrapezoid.bottomSegment)
                        leftTrapezoid.lowerRightNeighbour = topTrapezoid;

                    if (leftTrapezoid.topSegment == bottomTrapezoid.topSegment)
                        leftTrapezoid.upperRightNeighbour = bottomTrapezoid;

                    if (leftTrapezoid.bottomSegment == bottomTrapezoid.bottomSegment)
                        leftTrapezoid.lowerRightNeighbour = bottomTrapezoid;


                    topTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;

                    bottomTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;
                    bottomTrapezoid.lowerRightNeighbour = firstTrapezoid.lowerRightNeighbour;

                    Leaf leftTrapezoidLeaf = new Leaf(leftTrapezoid, name );
                    leftTrapezoid.setName(name);

                    Leaf topTrapezoidLeaf = new Leaf(topTrapezoid, "T" +trap_count);
                    topTrapezoid.setName("T"+trap_count);
                    trap_count++;
                    Leaf bottomTrapezoidLeaf = new Leaf(bottomTrapezoid, "T" + trap_count);
                    bottomTrapezoid.setName("T"+trap_count);
                    trap_count++;

                    leftEndPoint.left = leftTrapezoidLeaf;
                    leftEndPoint.right = segmentNode;
                    segmentNode.left = topTrapezoidLeaf;
                    segmentNode.right = bottomTrapezoidLeaf;

                    //update in search strcture
                    TNode searchNode = root;
                    while (!(searchNode instanceof Leaf)) {

                        if (searchNode instanceof XNode) {

                            if (firstTrapezoid.lbv.x < ((XNode) searchNode).point.x) {
                                if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                    //currentTrapezoidList.remove(firstTrapezoid);
                                    searchNode.left = leftEndPoint;
                                    break;
                                }

                                searchNode = searchNode.getLeft();
                            } else {
                                if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                    //currentTrapezoidList.remove(firstTrapezoid);
                                    searchNode.right = leftEndPoint;
                                    break;
                                }
                                searchNode = searchNode.getRight();
                            }

                        }

                        if (searchNode instanceof YNode) {

                            if (findPointAboveBelow(line.getA(), ((YNode) searchNode).line)) {
                                if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                    //currentTrapezoidList.remove(firstTrapezoid);
                                    searchNode.left = leftEndPoint;
                                    break;
                                }
                                searchNode = searchNode.getLeft();
                            } else {
                                if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                    //currentTrapezoidList.remove(firstTrapezoid);
                                    searchNode.right = leftEndPoint;
                                    break;
                                }
                                searchNode = searchNode.getRight();
                            }
                        }

                    }
                    //root.traverseInorder(root);

                    intersectingTrapezoids.remove(firstTrapezoid);

                    System.out.println("current size of Trapezoid list after evaluating left point" + currentTrapezoidList.size());



                    //if next trapezoid is the one right point lies in
                    firstTrapezoid = intersectingTrapezoids.get(0);
                    name=firstTrapezoid.getName();

                    //Cae 3: No segment endpoints lies in the trapezoid

                    //single trapezoid is replaced by two other trapezoids

                    //Replace leaf node of the trapezoid with ynode whose left and right child is top and bottom trapezoids
                    if (line.getA().x < firstTrapezoid.lbv.x && line.getB().x > firstTrapezoid.rbv.x) {

                        intersectingTrapezoids.remove(firstTrapezoid);
                        currentTrapezoidList.remove(firstTrapezoid);


                        System.out.println("cutting through pLEASE");

                        //for lbv, calculate intersection point of initial trapezoid lbv and line segment
                        TPoint lbv_point = findIntersectionOfLineWithSegmentsOfTrapezoid(firstTrapezoid.lbv, line, firstTrapezoid.bottomSegment);
                        TPoint rbv_point = findIntersectionOfLineWithSegmentsOfTrapezoid(firstTrapezoid.rbv, line, firstTrapezoid.bottomSegment);

                        TTrapezoid upTrapezoid = new TTrapezoid(lbv_point, rbv_point, firstTrapezoid.topSegment, new TLine(lbv_point, rbv_point));
                        TTrapezoid downTrapezoid = new TTrapezoid(lbv_point, rbv_point, new TLine(lbv_point, rbv_point), firstTrapezoid.bottomSegment);

                        currentTrapezoidList.add(upTrapezoid);

                        currentTrapezoidList.add(downTrapezoid);


                        YNode Segmentnode = new YNode(line, "S" + count);

                        upTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        upTrapezoid.lowerRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        downTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        downTrapezoid.lowerRightNeighbour = firstTrapezoid.upperRightNeighbour;

                        Leaf upTrapezoidLeaf = new Leaf(upTrapezoid, name);
                        upTrapezoid.setName(name);

                        Leaf downTrapezoidLeaf = new Leaf(downTrapezoid, "T" + trap_count);
                        downTrapezoid.setName("T"+trap_count);
                        trap_count++;

                        Segmentnode.left = upTrapezoidLeaf;
                        Segmentnode.right = downTrapezoidLeaf;


                        //update in search strcture
                        searchNode = root;

                        while (!(searchNode instanceof Leaf)) {

                            if (searchNode instanceof XNode) {

                                if (firstTrapezoid.rbv.x <= ((XNode) searchNode).point.x) {
                                    if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.left = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getLeft();

                                } else {
                                    if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.right = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getRight();
                                }

                            }

                            if (searchNode instanceof YNode) {
                                System.out.println(((YNode) searchNode).line.P.x + " " + ((YNode) searchNode).line.Q.x);
                                if (findPointAboveBelow(line.getA(), ((YNode) searchNode).line)) {
                                    if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.left = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getLeft();

                                } else {
                                    if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.right = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getRight();
                                }
                            }

                        }
                        //root.traverseInorder(root);
                        System.out.println("current Trapezoid list was after evaluting cut" + currentTrapezoidList.size());
                        //System.out.println("---------------------");

                        firstTrapezoid = intersectingTrapezoids.get(0);
                        name = firstTrapezoid.getName();

                    }


                    //if intersects one of the lines
                    if (line.getB().x == firstTrapezoid.rbv.x) {

                        //System.out.println("hi");
                        //divides in two parts
                        intersectingTrapezoids.remove(firstTrapezoid);
                        currentTrapezoidList.remove(firstTrapezoid);



                        //for lbv, calculate intersection point of initial trapezoid lbv and line segment
                        TPoint lbv_point = findIntersectionOfLineWithSegmentsOfTrapezoid(firstTrapezoid.lbv, line, firstTrapezoid.bottomSegment);
                        TPoint rbv_point = line.getB();

                        TTrapezoid upTrapezoid = new TTrapezoid(lbv_point, rbv_point, firstTrapezoid.topSegment, new TLine(lbv_point, rbv_point));
                        TTrapezoid downTrapezoid = new TTrapezoid(lbv_point, rbv_point, new TLine(lbv_point, rbv_point), firstTrapezoid.bottomSegment);

                        currentTrapezoidList.add(upTrapezoid);

                        currentTrapezoidList.add(downTrapezoid);


                        YNode Segmentnode = new YNode(line, "S" + count);

                        upTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        upTrapezoid.lowerRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        downTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        downTrapezoid.lowerRightNeighbour = firstTrapezoid.upperRightNeighbour;

                        Leaf upTrapezoidLeaf = new Leaf(upTrapezoid, name);
                        upTrapezoid.setName(name);
                        Leaf downTrapezoidLeaf = new Leaf(downTrapezoid, "T" + trap_count);
                        downTrapezoid.setName("T"+trap_count);
                        trap_count++;

                        Segmentnode.left = upTrapezoidLeaf;
                        Segmentnode.right = downTrapezoidLeaf;

                        //update in search strcture
                        searchNode = root;

                        while (!(searchNode instanceof Leaf)) {

                            if (searchNode instanceof XNode) {

                                if (firstTrapezoid.rbv.x <= ((XNode) searchNode).point.x) {
                                    if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.left = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getLeft();

                                } else {
                                    if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.right = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getRight();
                                }

                            }

                            if (searchNode instanceof YNode) {
                                System.out.println(((YNode) searchNode).line.P.x + " " + ((YNode) searchNode).line.Q.x);
                                if (findPointAboveBelow(line.getA(), ((YNode) searchNode).line)) {
                                    if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.left = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getLeft();

                                } else {
                                    if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.right = Segmentnode;
                                        break;
                                    }
                                    searchNode = searchNode.getRight();
                                }
                            }

                        }
                        //root.traverseInorder(root);
                        System.out.println("current Trapezoid list was" + currentTrapezoidList.size());
                        System.out.println("---------------------");

                    }



                    if (line.getB().x < firstTrapezoid.rbv.x) {

                        System.out.println("EVALUATING RIGHT POINT" + firstTrapezoid.lbv.x);

                        intersectingTrapezoids.remove(firstTrapezoid);
                        currentTrapezoidList.remove(firstTrapezoid);
                        trap_count--;

                        TPoint rp_ts,rp_bs;
                        TTrapezoid RtopTrapezoid,RbottomTrapezoid,RrightTrapezoid;

                        if(findPointAboveBelow(firstTrapezoid.rbv,line)) {
                            rp_ts = findIntersectionOfLineWithSegmentsOfTrapezoid(line.getB(), firstTrapezoid.topSegment, firstTrapezoid.bottomSegment);
                            RtopTrapezoid = new TTrapezoid(firstTrapezoid.lbv, line.getB(), firstTrapezoid.bottomSegment, line);
                            RbottomTrapezoid = new TTrapezoid(firstTrapezoid.bottomSegment.getA(), firstTrapezoid.rbv, line, firstTrapezoid.bottomSegment);
                        }
                        else {
                            //bottom traps bottom segment is first trapezoids bottom segment
                            rp_bs = findIntersectionOfLineWithSegmentsOfTrapezoid(line.getB(), firstTrapezoid.bottomSegment, firstTrapezoid.topSegment);
                            RtopTrapezoid = new TTrapezoid(line.getA(), line.getB(), firstTrapezoid.topSegment, line);
                            RbottomTrapezoid = new TTrapezoid(firstTrapezoid.lbv, rp_bs, line, firstTrapezoid.bottomSegment);
                        }



                        RrightTrapezoid = new TTrapezoid(line.getB(), firstTrapezoid.rbv, firstTrapezoid.topSegment, firstTrapezoid.bottomSegment);

                        currentTrapezoidList.add(RbottomTrapezoid);
                        currentTrapezoidList.add(RtopTrapezoid);
                        currentTrapezoidList.add(RrightTrapezoid);



                        XNode rightEndPoint = new XNode(line.getB(), "Q" + count);

                        YNode RsegmentNode = new YNode(line, "S" + count);

                        topTrapezoid.upperRightNeighbour = RtopTrapezoid;
                        bottomTrapezoid.upperRightNeighbour = RbottomTrapezoid;

                        RtopTrapezoid.upperRightNeighbour = RrightTrapezoid;
                        RtopTrapezoid.lowerRightNeighbour = RrightTrapezoid;
                        RbottomTrapezoid.upperRightNeighbour = RrightTrapezoid;
                        RbottomTrapezoid.lowerRightNeighbour = RrightTrapezoid;

                        RrightTrapezoid.upperRightNeighbour = firstTrapezoid.upperRightNeighbour;
                        RrightTrapezoid.lowerRightNeighbour = firstTrapezoid.upperRightNeighbour;

                        Leaf RrightTrapezoidLeaf = new Leaf(RrightTrapezoid, name);
                        RrightTrapezoid.setName(name);
                        Leaf RtopTrapezoidLeaf = new Leaf(RtopTrapezoid, "T" + trap_count);
                        RtopTrapezoid.setName("T"+trap_count);
                        trap_count++;
                        Leaf RbottomTrapezoidLeaf = new Leaf(RbottomTrapezoid, "T" + trap_count);
                        RbottomTrapezoid.setName("T"+trap_count);
                        trap_count++;

                        rightEndPoint.right = RrightTrapezoidLeaf;
                        rightEndPoint.left = RsegmentNode;

                        RsegmentNode.left = RtopTrapezoidLeaf;
                        RsegmentNode.right = RbottomTrapezoidLeaf;


                        //update in search strcture
                        searchNode = root;
                        while (!(searchNode instanceof Leaf)) {

                            if (searchNode instanceof XNode) {

                                if (firstTrapezoid.rbv.x <= ((XNode) searchNode).point.x) {
                                    if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.left = rightEndPoint;
                                        break;
                                    }
                                    searchNode = searchNode.getLeft();

                                } else {
                                    if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.right = rightEndPoint;
                                        break;
                                    }
                                    searchNode = searchNode.getRight();
                                }

                            }

                            if (searchNode instanceof YNode) {
                                System.out.println(((YNode) searchNode).line.P.x + " " + ((YNode) searchNode).line.Q.x);
                                if (findPointAboveBelow(line.getA(), ((YNode) searchNode).line)) {
                                    if (searchNode.getLeft() instanceof Leaf && ((Leaf) searchNode.getLeft()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.left = rightEndPoint;
                                        break;
                                    }
                                    searchNode = searchNode.getLeft();

                                } else {
                                    if (searchNode.getRight() instanceof Leaf && ((Leaf) searchNode.getRight()).trapezoid.equals(firstTrapezoid)) {
                                        //currentTrapezoidList.remove(firstTrapezoid);
                                        searchNode.right = rightEndPoint;
                                        break;
                                    }
                                    searchNode = searchNode.getRight();
                                }
                            }

                        }

                        //root.traverseInorder(root);
                        System.out.println("current Trapezoid list was After evaluating right point" + currentTrapezoidList.size());
                        System.out.println("current intersecting" + intersectingTrapezoids.size());
                        System.out.println("---------------------");



                    }

                }
            }

        }
        root.traverseInorder(root);
        System.out.println(root.size);

        adjacencyMatrix = new int[root.size+1][root.size+1];

    }

    //ADD edges to create adjacency matrix
    public void addInAdjacencyMatrix(){


        int i=0;
        int j=0;

        for(i = 0 ; i< root.size ; i++){

            for(j = 0; j< root.size ; j++) {

                if (root.nodeList.get(i) != root.nodeList.get(j)) {

                    if (root.nodeList.get(i).left == root.nodeList.get(j) || root.nodeList.get(i).right == root.nodeList.get(j)) {

                        adjacencyMatrix[j][i] = 1;
                    } else adjacencyMatrix[j][i] = 0;

                }
            }

        }
    }

    //print the adjacency matrix and calculate row and column sum
    public void printAdjacencyMatrix(){



        int rowSum=0;
        int colSum=0;

        int i,j=0;

        for(i =0 ; i <= root.size ; i ++ ){
            int sum=0;
            for(j = 0 ; j < root.size ; j++){
                sum+=adjacencyMatrix[i][j];
            }
            adjacencyMatrix[i][j] = sum;

        }

        for(i=0 ; i <= root.size ; i ++ ){
            int sum = 0 ;

            for( j= 0 ; j < root.size ; j ++ ){

                sum+=adjacencyMatrix[j][i];
            }
            adjacencyMatrix[j][i]=sum;
        }

        //for printing on console
        /*for(i = 0 ; i <= root.size; i ++ ){

            if(i==root.size)
                System.out.println("\t" + "sum");
            else {
                System.out.print("\t" + root.nodeList.get(i).name);
            }
        }

        System.out.println();


        for(i = 0 ; i <= root.size; i++){

            if(i==root.size)
                System.out.print("sum");
            else
                System.out.print(root.nodeList.get(i).name);

            for(j=0 ; j <=  root.size ; j ++ ){

               System.out.print("\t" + adjacencyMatrix[i][j]);
            }
            System.out.println();
        }*/

    }

    //function to write adjacency matrix to file
    public void writeFile(){
        try {
            FileWriter writer = new FileWriter("TrapezoidalMatrix.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            for(int i = 0 ; i <= root.size; i ++ ){

                if(i==root.size)
                    bufferedWriter.write("Sum");
                else {
                    bufferedWriter.write( "\t" + root.nodeList.get(i).name);
                }
            }

           bufferedWriter.newLine();

            for(int i =0 ; i<=root.size ; i ++  ){

                if(i==root.size)
                    bufferedWriter.write("Sum");
                else
                    bufferedWriter.write(root.nodeList.get(i).name);

                for(int j = 0 ; j <= root.size ; j ++ ) {

                    bufferedWriter.write("\t" + adjacencyMatrix[i][j]);

                }
                bufferedWriter.newLine();

            }

            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //function that performs query searching in the tree structure
    public void searchQuery(TPoint Lpoint){
        //Trace through search structure to get trapezoid that has the point
        TNode searchNode = root;
        //while we dont encounter a leaf node
        while (!(searchNode instanceof Leaf)){

            //1. If it is an XNode (LEFT/RIGHT)
            if(searchNode instanceof XNode){

                if (Lpoint.x < ((XNode) searchNode).point.x){
                    System.out.print("\t" + searchNode.name);
                    searchNode = searchNode.getLeft();

                }
                else{
                    System.out.print("\t" + searchNode.name);
                    searchNode = searchNode.getRight();

                }
            }

            //2. If it is a YNode (TOP/BOTTOM)
            if(searchNode instanceof YNode){

                if(findPointAboveBelow(Lpoint,((YNode)searchNode).line)){
                    System.out.print("\t" + searchNode.name);
                    searchNode = searchNode.getLeft();

                }
                else{
                    System.out.print("\t" + searchNode.name);
                    searchNode = searchNode.getRight();

                }
            }
        }

        //Casting
        Leaf returnNode = (Leaf)searchNode;
        System.out.print("\t" + returnNode.name);

    }


    public static void main(String[]args){


        TrapezoidalMapMain tm = new TrapezoidalMapMain();


        //String filename = "inputPoints_smb6390.txt";


        String fileName ;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name");
        fileName = (scanner.next());

        //Read the file
        tm.readFile(fileName);

        //main algorithm
        tm.incrementalAlgorithm();

        tm.addInAdjacencyMatrix();

        tm.printAdjacencyMatrix();

        tm.writeFile();

        System.out.println("Enter the query points");
        System.out.println("Enter x in Double format");
        double x = scanner.nextDouble();
        System.out.println("Enter y in Double format");
        double y = scanner.nextDouble();

        //search query
        tm.searchQuery(new TPoint(x,y));


    }
}
