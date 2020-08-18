import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by sayleebhide on 10/14/17.
 */
public class PlaneSweep_BO {

    public static SweepLineAVLNode root;

    //Data structure for sweep line status
    public static SweepLineAVL slstatus = new SweepLineAVL();

    public static SweepLineAVL eventQueue = new SweepLineAVL();

    public int numPoints;

    public static Line[] lineArray;

    public static ArrayList<PointSweep> intersectionPointList = new ArrayList<PointSweep>();

    /**
     * Read the file as per the format
     * @param fileName Name of the file to be read
     */
    public void readFile(String fileName){

        //reading one line at at time
        String line = null;

        PointSweep point1;

        PointSweep point2;

        try {

            //reads text files in default encoding
            FileReader fileReader = new FileReader(fileName);

            //wraped in bufferedreader
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read no of points in the file
            numPoints = Integer.parseInt(bufferedReader.readLine());

            //initialize Line array
            lineArray = new Line[numPoints];

            //keep track of line number
            int lineNo = 0;

            //while file doesnt reach end of line
            while((line = bufferedReader.readLine())!=null && lineNo < numPoints ){

                //split using space delimitter into x and y to store in point DS
                String [] splitLine = line.split("\\s+");

                //store as x and y co
                int x1 = Integer.parseInt(splitLine[0]);

                int y1 = Integer.parseInt(splitLine[1]);

                int x2 = Integer.parseInt(splitLine[2]);

                int y2 = Integer.parseInt(splitLine[3]);


                //create Startingpoing and ending point of line segment
                point1 = new PointSweep(x1,y1);
                point2 = new PointSweep(x2,y2);

                //create new set of point objects and store it in pointhull DS

                if(x1>x2)
                    lineArray[lineNo] = new Line(point2,point1);
                else
                    lineArray[lineNo] = new Line(point1,point2);

                lineNo++;

            }
            bufferedReader.close();

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

    public void writeFile(ArrayList<PointSweep>intersectionPointList){
        try {
            FileWriter writer = new FileWriter("PlaneSweep_BO.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(Integer.toString(intersectionPointList.size()));
            bufferedWriter.newLine();

            for(PointSweep p : intersectionPointList){

                bufferedWriter.write(Double.toString(p.x)+ " " + Double.toString(p.y) );
                bufferedWriter.newLine();

            }

            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void insertArrayintoTree( Line[] lineArray){
        for(int i = 0 ; i < lineArray.length ; i++){
            eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root , new event(lineArray[i].pointA , 'l' , lineArray[i]));
            eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root , new event(lineArray[i].pointB , 'r' , lineArray[i]));
        }
        System.out.println(eventQueue.root.height);

        eventQueue.printInorder(eventQueue.root);
    }

    public double findyintercept(Line line , double xcoeff){

        double m = (line.pointB.y - line.pointA.y)/(line.pointB.x - line.pointA.x);

        //double b = line.pointA.y - ( m * line.pointA.x);
        //double y1 = ( m * xcoeff) + b;

        //double y1 = m*(xcoeff - line.pointA.x) + line.pointA.y;

        double y1 = line.pointA.y - m * (line.pointA.x - xcoeff);

        return y1;

    }

    public int intersect(Line a , Line b){
        double orient1 = orient(a.pointA , a.pointB , b.pointA);
        double orient2 = orient(a.pointA , a.pointB , b.pointB);
        double orient3 = orient(b.pointA , b.pointB , b.pointA);
        double orient4 = orient(b.pointA , b.pointB , a.pointB);

        if (orient1 != orient2 && orient3!= orient4){
            //System.out.println("Sending" + " " + i + " " + j);
            //findIntersectionPoint(a.pointA , a.pointB , b.pointA , b.pointB);
            return 1;
        }
        //special cases
        if (orient1 == 0  && testCollinear(a.pointA , b.pointA , a.pointB)==1){
            //System.out.println("Sending" + " " + i + " " + j);
            //findIntersectionPoint(a.pointA , a.pointB , b.pointA , b.pointB);
            return 1;
        }

        if(orient2 ==0 && testCollinear(a.pointA , b.pointB , a.pointB)==1){
            //System.out.println("Sending" + " " + i + " " + j);
            //findIntersectionPoint(a.pointA , a.pointB , b.pointA , b.pointB);
            return 1;
        }

        if(orient3 ==0 && testCollinear(b.pointA , a.pointA , b.pointB)==1){
            //System.out.println("Sending" + " " + i + " " + j);
            //findIntersectionPoint(a.pointA , a.pointB , b.pointA , b.pointB);
            return 1;
        }

        if(orient4 ==0 && testCollinear(a.pointA , a.pointB , a.pointB)==1){
            //System.out.println("Sending" + " " + i + " " + j);
            //findIntersectionPoint(a.pointA , a.pointB , b.pointA , b.pointB);
            return 1;
        }
        else
            return -1;
    }

    /**
     * Find orientation of three points
     * @param a point 1
     * @param b point 2
     * @param c point 3
     * @return orientation
     */
    public double orient(PointSweep a , PointSweep b , PointSweep c){

        //(y2 - y1)*(x3 - x2) - (y3 - y2)*(x2 - x1)
        /*
        Above expression is negative when counterclockwise - LEFT TURN
        Above expression is 0 when  collinear
        Above expression is positive when  clockwise - RIGHT TURN
         */
        double orientation = (b.y - a.y) * (c.x - b.x) - (c.y-b.y)*(b.x-a.x);

        return orientation;


    }

    public int testCollinear(PointSweep a , PointSweep b , PointSweep c){

        if(b.x <= Math.max(a.x , c.x) && Math.max(a.x , c.x) <= b.x && b.y <= Math.max(a.y , c.y) && Math.max(a.y , c.y) <= b.y ){
            return 1;
        }
        return -1;
    }

    /**
     * Referred https://www.topcoder.com/community/data-science/data-science-tutorials/geometry-concepts-line-intersection-and-its-applications/
     * for logic of finding intersection point
     * @param L
     * @param M
     * @param N
     * @param O
     */
    public PointSweep findIntersectionPoint(PointSweep L , PointSweep M , PointSweep N , PointSweep O){

        boolean lineSegment1 = false;
        boolean lineSegment2 = false;

        //For line L---M
        double A1 = M.y - L.y;
        double B1 = L.x - M.x;
        double C1 = A1*L.x + B1*L.y;

        //For line N----0
        double A2 = O.y - N.y;
        double B2 = N.x - O.x;
        double C2 = A2 * N.x + B2 * N.y;

        double det  = A1 * B2 - A2 * B1;

        //Intersecting points are
        double x_int  = (B2 * C1 - B1 * C2)/det;
        double y_int  = (A1 * C2 - A2 * C1)/det;

        //since its a line segment make sure your point x_int , y_int lies on both of your line segments

        //check for L---M
        if( x_int >= Math.min(L.x , M.x) && x_int <= Math.max(L.x , M.x) && y_int >= Math.min(L.y , M.y) && y_int <= Math.max(L.y , M.y) ){
            lineSegment1 = true;
        }

        //check for M---N
        if( x_int >= Math.min(N.x , O.x) && x_int <= Math.max(N.x , O.x) && y_int >= Math.min(N.y , O.y) && y_int <= Math.max(N.y , O.y) ){
            lineSegment2 = true;
        }

        //if present on both line segments
        if(lineSegment1&&lineSegment2){
            //intersectionPointList.add(new PointSweep(x_int , y_int));
            //System.out.println("Added" + " " + x_int + " "+ y_int);
            return new PointSweep((x_int),(y_int));
        }
    return null;
    }

    /**
     * Main plane sweep algorithm
     * @param eventQueue AVL tree for events
     * @param eventList Array list of events
     */
    public void implementAlgorithm(SweepLineAVL eventQueue , ArrayList<event> eventList){
        int i=0;


        //while event queue is non empty , extract next event in queue
        while(i!=eventQueue.eventList.size()) {

            //next event in list
            event e = eventList.get(i);
            slstatus.pred = null;
            slstatus.succ = null;

            /*System.out.println("INTERSECTION POINGS" + intersectionPointList.size() + " i" + " " + i);
            for(PointSweep point : intersectionPointList){
                System.out.println("The points are" + point.x + " " + point.y);
            }*/

            //first reorder the sweep line entries acc to y intercept
            slstatus.clearTree();

            double xcord = eventList.get(i).point.x;

            for(int l = 0 ; l < slstatus.entryList.size() ; l ++ ){
                double ycord = findyintercept(slstatus.entryList.get(l).line , xcord);
                //System.out.println(" the new y cord is" + ycord + " with x cord" + xcord);
                slstatus.entryList.get(l).ycoeff = ycord;
                slstatus.entryList.get(l).xcoeff = xcord;
                slstatus.root = slstatus.insertinSweepLineStatus(slstatus.root , slstatus.entryList.get(l));
            }

           // System.out.println(" THE CURRENT SWEEP LINE ENTRIES ARE " + i);
            slstatus.entryList.clear();

            //insert back in sweepline status array list
            slstatus.printInorderSweepLine(slstatus.root);


            //left end point
            if(e.type =='l'){


                //insert this line segment into sweeplinestatus sorted by y cordinates
                double xcoeff = eventList.get(i).point.x;
                double ycoeff = findyintercept(e.linesegment , eventList.get(i).point.x);
                //System.out.println("Inserting in status line" + xcoeff + " " + ycoeff);
                sweepLineEntry currentEntry = new sweepLineEntry(e.linesegment,xcoeff,ycoeff);
                slstatus.root = slstatus.insertinSweepLineStatus(slstatus.root , currentEntry);

                //inserting in entry list
                slstatus.entryList.clear();
                slstatus.printInorderSweepLine(slstatus.root);

                Line up = null;
                Line down = null;
                int cur_index;
                cur_index = slstatus.entryList.indexOf(currentEntry);
                if(cur_index!=0)
                    up = (slstatus.entryList.get(cur_index -1).line);
                if(cur_index!=slstatus.entryList.size()-1)
                    down = slstatus.entryList.get(cur_index+1).line;


                if(up==null){
                    //System.out.println("No line on top");
                }
                else{
                    //System.out.println("TOP is " + up.pointA.x + " " + up.pointA.y + " "+ up.pointB.x + " "+ up.pointB.y);
                }

                if(down==null){
                    //System.out.println("No line on bottom");
                }
                else{
                    //System.out.println("Bottom is " + down.pointA.x + " " + down.pointA.y + " "+ down.pointB.x + " "+ down.pointB.y);
                }

                //check intersection of current line and top line and add event to eventqueue
                if(up!=null){
                    if(intersect(up,e.linesegment)==1){
                        PointSweep POI = findIntersectionPoint(up.pointA, up.pointB, e.linesegment.pointA , e.linesegment.pointB);
                        if(POI!=null) {
                            eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root, new event(POI, 'i', e.linesegment,up));
                            eventQueue.eventList.clear();
                            eventQueue.printInorder(eventQueue.root);
                        }
                    }
                }

                if(down!=null){
                    if(intersect(down,e.linesegment)==1){
                        PointSweep POI = findIntersectionPoint(down.pointA, down.pointB, e.linesegment.pointA , e.linesegment.pointB);
                        if(POI!=null) {
                            eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root, new event(POI, 'i', e.linesegment , down));
                            eventQueue.eventList.clear();
                            eventQueue.printInorder(eventQueue.root);
                        }
                    }
                }


            }

            //if right end point
            if(e.type == 'r'){


                //find the entry in sweep line
                //find these lines in the sweep line entry list
                sweepLineEntry entry = null;
                for(int l = 0 ; l  < slstatus.entryList.size() ; l ++ ){
                    if(Math.round(e.point.x * 10000.0)/10000.0 == Math.round(slstatus.entryList.get(l).xcoeff * 10000.0)/10000.0 && Math.round(e.point.y*10000.0)/10000.0 == Math.round(slstatus.entryList.get(l).ycoeff * 10000.0)/10000.0){
                        //System.out.println("Removing" + slstatus.entryList.get(l).xcoeff + " " + slstatus.entryList.get(l).ycoeff);
                        entry = slstatus.entryList.get(l);
                    }
                }


                //if up line is preseent
                Line up = null;
                Line down = null;
                int cur_index;
                cur_index = slstatus.entryList.indexOf(entry);
                if(cur_index!=0)
                    up = (slstatus.entryList.get(cur_index -1).line);
                if(cur_index!=slstatus.entryList.size()-1)
                    down = slstatus.entryList.get(cur_index+1).line;

                if(up==null){
                    //System.out.println("No line on top");
                }
                if(down==null){
                    //System.out.println("No line on bottom");
                }


                // delete line segment from status line

                Iterator<sweepLineEntry> iter = slstatus.entryList.iterator();

                while(iter.hasNext()){
                    sweepLineEntry e1 = iter.next();

                    if(e1.xcoeff==entry.xcoeff && e1.ycoeff == entry.ycoeff){
                        iter.remove();
                    }
                }

                //build avl tree from list excluding the finished line segment

                //first clear it
                slstatus.clearTree();

                //re insert all elements back
                for(int i_ = 0 ;  i_ < slstatus.entryList.size() ; i_ ++ ){
                    slstatus.root = slstatus.insertinSweepLineStatus(slstatus.root,slstatus.entryList.get(i_));
                }

                //order them
                slstatus.entryList.clear();
                slstatus.printInorderSweepLine(slstatus.root);

                //calculate intersection of current line and top line if event is present, add in event queue
                if(up!=null && down!=null){
                    if(intersect(up,down)==1){
                        PointSweep POI = findIntersectionPoint(up.pointA, up.pointB, down.pointA , down.pointB);
                        if(POI!=null) {
                            eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root, new event(POI, 'i', up, down));
                            eventQueue.eventList.clear();
                            eventQueue.printInorder(eventQueue.root);
                        }
                    }
                }



            }

            //if event is an intersection point
            if(e.type == 'i'){

                //report this intersection
                intersectionPointList.add(e.point);

                //let s' and s'' be the two intersecting segments , swap these two entries in the sweep line status
                Line s_1 = e.linesegment;
                Line s_2 = e.intersectingLine;

                //System.out.println(" The intesecting line segments are" + s_1.pointA.x + " " + s_1.pointA.y + " " + s_2.pointA.x + " " + s_2.pointA.y);

                //find these lines in the sweep line entry list
                sweepLineEntry s_1entry;
                sweepLineEntry s_2entry;
                ArrayList<Integer> twoIndex = new ArrayList<>();
                for(int l = 0 ; l  < slstatus.entryList.size() ; l ++ ){
                  if(Math.round((slstatus.entryList.get(l).xcoeff)*10000.0)/10000.0 == Math.round(e.point.x * 10000.0)/10000.0 && Math.round(slstatus.entryList.get(l).ycoeff *10000.0)/10000.0 == Math.round(e.point.y*10000.0)/10000.0){
                      twoIndex.add(l);
                      //System.out.println("adding" + l);
                  }
                }

                s_1entry = slstatus.entryList.get(twoIndex.get(0));
                s_2entry = slstatus.entryList.get(twoIndex.get(1));

                // if both entries found in sweep line
                if(s_1entry!=null && s_2entry!=null){
                    //swap the contents of the two entries
                    int a =slstatus.entryList.indexOf(s_1entry);
                    int b =slstatus.entryList.indexOf(s_2entry);
                    Collections.swap(slstatus.entryList,a,b);
                }

                //Check new adjacencies after swap is performed
                Line A = null;

                if(slstatus.entryList.indexOf(s_2entry) !=0) {
                    int a_in = (slstatus.entryList.indexOf(s_2entry) - 1);
                    A = slstatus.entryList.get(a_in).line;
                    //System.out.println("a_in" + a_in);
                }

                if(A==null){
                    //System.out.println("No line on top after swapping");
                }
                //else System.out.println("Found line on top " + A.pointA.x + " " + A.pointA.y);


                Line B = null;
                if(slstatus.entryList.indexOf(s_1entry)!=slstatus.entryList.size()-1) {
                    int b_in = (slstatus.entryList.indexOf(s_1entry) + 1);
                    //System.out.println("b_in" + b_in);
                    B = slstatus.entryList.get(b_in).line;
                }

                if(B==null){
                    //System.out.println("No line on bottom after swapping");
                }
                //else System.out.println("Found line on bottom " + B.pointA.x + " " + B.pointA.y);

                //check new adjacencies and intersection event
                if(A!=null && intersect(s_1entry.line , A)==1){

                    //System.out.println("came here" + A.pointA.x + " " + A.pointA.y + " "+ s_1.pointA.x + " " + s_1.pointA.y);
                    PointSweep POI = findIntersectionPoint(s_1entry.line.pointA, s_1entry.line.pointB, A.pointA, A.pointB);
                    if(POI!=null) {
                        eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root, new event(POI, 'i', s_1entry.line, A));
                        eventQueue.eventList.clear();
                        eventQueue.printInorder(eventQueue.root);
                    }

                }

                //check new adjacencies and intersection event
                if(B!=null && intersect(s_2entry.line , B)==1){
                    //System.out.println("came here b" + B.pointA.x + " " + B.pointA.y + " "+ s_2.pointA.x + " " + s_2.pointA.y);

                    PointSweep POI = findIntersectionPoint(s_2entry.line.pointA, s_2entry.line.pointB, B.pointA, B.pointB);
                    if(POI!=null) {
                        eventQueue.root = eventQueue.insertinEventQueue(eventQueue.root, new event(POI, 'i', s_2entry.line, B));
                        eventQueue.eventList.clear();
                        eventQueue.printInorder(eventQueue.root);
                    }

                }

            }

            i = i + 1;
        }

        System.out.println("The intersection poings are" + intersectionPointList.size());
        for(PointSweep point : intersectionPointList){
            System.out.println(point.x  + " " + point.y);
        }
    }

    public static void main(String[]args){

        PlaneSweep_BO bo = new PlaneSweep_BO();

        //String fileName = "datapoints.txt";
        String fileName ;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name");
        fileName = (scanner.next());

        // read file and store lines in line array
        bo.readFile(fileName);

        // insert all end points into event queue
        bo.insertArrayintoTree(lineArray);


        long sttime = System.currentTimeMillis();

        //implement the main algorithm
        bo.implementAlgorithm(eventQueue , eventQueue.eventList);

        long etime = System.currentTimeMillis();

        System.out.println("Time" + (etime-sttime));

        bo.writeFile(intersectionPointList);


    }



}
