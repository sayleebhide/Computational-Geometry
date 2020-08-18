/**
 * Created by sayleebhide on 9/26/17.
 */
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class CH_GrahamsScan {

    public  int numPoints ;

    //First point is the bottom most point of the set
    public static pointhull firstPoint;

    //global point set array - points contained in the convex hull including the points on the hull
    public static pointhull[] points;

    //points sorted by polar angle
    public static pointhull[] sorted_points;

    //Final Arraylist containing the final points on the hull
    public static ArrayList<pointhull> hullArray = new ArrayList<>();

    /**
     * Function to read the points from the file
     * @param fileName Name of input file
     */
    public void readFile(String fileName){

        String line = null;

        try {

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //read no of points in the file
            numPoints = Integer.parseInt(bufferedReader.readLine());

            //initialize point array
            points = new pointhull[numPoints];

            //keep track of line number
            int lineNo = 0;

            while((line = bufferedReader.readLine())!=null && lineNo < numPoints ){

                //split using space delimitter into x and y to store in point DS
                String [] splitLine = line.split("\\s+");

                //store as x and y co
                int x = Integer.parseInt(splitLine[0]);

                int y = Integer.parseInt(splitLine[1]);

                //create new set of point and store it in pointhull DS
                points[lineNo] = new pointhull(x,y);

                lineNo++;

            }
            bufferedReader.close();

        }

        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");

        }
        //System.out.println("num of points are" + numPoints);


    }

    /**
     * Function to identify the bottommost point
     * TC : O(n) where n is the size of the points array
     * @param points points array
     */
    public void identify_bottom(pointhull[]points){
        pointhull bottomPoint = points[0];
        pointhull tempPoint;
        int index = 0;
        for( int i =1 ; i<points.length ; i++){
            if (points[i].y < bottomPoint.y){
                bottomPoint = points[i];
                index = i;
            }
            if (points[i].y == bottomPoint.y){
                if(points[i].x < bottomPoint.x){
                    bottomPoint = points[i];
                    index = i;
                }
            }
        }
        System.out.println("The bottom point is : "+ " "+ bottomPoint.x + " "+bottomPoint.y);
        firstPoint = bottomPoint;
        tempPoint = points[index];
        points[index] = points[0];
        points[0] = tempPoint;

    }

    /**
     * Function to identify slope of the line formed by the point and starting point
     * TC : Constant time
     * Referred to https://www.mathsisfun.com/polar-cartesian-coordinates.html for the idea of adding 180,360 for different quadrants
     * @param startingPoint Bottom most point that is identified and considered starting point
     * @param a other poiints
     * @return slope of other point wrt starting point
     */
    public double slope(pointhull startingPoint , pointhull a ){
        double slope = 0.0;
        double deltaY , deltaX =0;
        deltaX = a.x - startingPoint.x;
        deltaY = a.y - startingPoint.y;
        slope = deltaY/deltaX;
        //System.out.println("Slope for " + a.x + " " + a.y + " " + slope) ;
        //System.out.println(deltaX + " "+ deltaY);
        //System.out.println("----");

        //return slope for first quadrant;
        if(a.x > startingPoint.x && a.y>startingPoint.y)
            return Math.toDegrees(Math.atan(slope));

        //return slope for second quadrant
        if(a.x < startingPoint.x && a.y<startingPoint.y)
            return Math.toDegrees(Math.atan(slope)) + 180;

        // return slope for third quadrant
        if(a.x < startingPoint.x && a.y>startingPoint.y)
            return Math.toDegrees(Math.atan(slope)) + 180;

        //if point lies on same y axis in 1st quad 0 degrees
        if(a.x > startingPoint.x && a.y == startingPoint.y)
            return Math.toDegrees(Math.atan(0));

        //180 degrees
        if(a.x< startingPoint.x && a.y == startingPoint.y)
            return Math.toDegrees(180);

        //90 degrees
        if(a.x == startingPoint.x && a.y > startingPoint.y)
            return Math.toDegrees(90);

        //270 degrees
        if(a.x == startingPoint.x && a.y < startingPoint.y)
            return Math.toDegrees(270);

        //return slope for fourth quadrant
        return Math.toDegrees(Math.atan(slope)) + 360;
    }

    /**
     * Function to calculate Distance between the point and starting point
     * TC : Constant time
     * @param startingPoint Bottom most point that is identified and considered starting point
     * @param a other poiints
     * @return distance of other point wrt starting point
     */
    public double distance(pointhull startingPoint, pointhull a){

        double deltaX  = a.x - startingPoint.x;
        double deltaY  = a.y - startingPoint.y;
        double distance = Math.sqrt(Math.pow(deltaX,2.0)+Math.pow(deltaY,2.0));
        //System.out.println("Distance for " + a.x + " " + a.y + " " + distance) ;
        return distance;

    }

    /**
     *Function to convert array to an arraylist
     *TC : O(n) where n is the size of the array
     * @param points
     * @param arrayList
     * @return arraylist of set of points contained in points array
     */
    public ArrayList<pointhull> arrayToarrayList(pointhull[] points , ArrayList<pointhull>arrayList){
        for( int i = 0 ; i <points.length ; i++){
            arrayList.add(points[i]);
        }
        return arrayList;
    }

    /**
     * Function to sort points on the basis of polar angles - Used bubble sort O(n^2)
     * TC : O(n^2) where n is the size of the points array
     * Refered to professor Aaron Deevers notes on Convex hulls for this approach
     * @param points
     * @param startingPoint
     */
    public void sort_on_polar_angle(pointhull[] points,pointhull startingPoint) {


        pointhull tempPoint;
        for(int i = 1; i < points.length ; i ++){
            for(int j = i+1 ; j < points.length ; j++){
                double slope1 = slope(startingPoint,points[i]);
                double slope2 = slope(startingPoint,points[j]);


                if(slope1 > slope2){

                    tempPoint = points[i];
                    points[i] = points[j];
                    points[j] = tempPoint;
                }
            }
        }

        //convert arraylist to array after removed points
        //remove points with same angle and less distance
        ArrayList<pointhull> arrayList = new ArrayList<pointhull>();

        //sorted by polar angle
        arrayList = arrayToarrayList(points,arrayList);

        //System.out.println("Befre DELETING SIZE"+" "+ arrayList.size());
        //Retain only the farthest point
        for(int k = 1 ; k < arrayList.size() ; k ++){
            for(int l = k+1 ; l < arrayList.size() ; l ++){
                if((slope(startingPoint,arrayList.get(k))==slope(startingPoint,arrayList.get(l))) && (distance(startingPoint,arrayList.get(k)) < distance(startingPoint,arrayList.get(l)))){
                    arrayList.remove(k);
                }
            }
        }
        //convert arraylist to array - create new array since array cannot be resized
        sorted_points = new pointhull[arrayList.size()];

        //add elements from arraylist to array
        for(int k = 0 ; k<arrayList.size();k++){
            sorted_points[k] = arrayList.get(k);
        }


    }

    /**
     *Java has inbuilt stack which provides push pop and peek; Peforms scanning in counter clockwise direction using stack
     * TC : O(n)
     * Refered to professor Aaron Deevers notes on Convex hulls for this approach
     * @param stack
     * @param sorted_points
     */
    public void stack_push_sortedOrderRtoL(Stack<pointhull> stack,pointhull[] sorted_points){

        //Push the first three points into the stack
        for( int i = 0 ; i < 3 ; i++ ){
            stack.push(sorted_points[i]);
        }

        //For each next point, consider the angle formed by that point plus the two top elements of the stack.
        for(int i = 3 ; i < sorted_points.length ; i++) {

            while (stack.size() >= 2 && calculateOrientation(stack_before_top(stack),stack.peek(),sorted_points[i])>0) {
                //System.out.println("popping :" + " " + stack_peek(stack).x + " " + stack_peek(stack).y + " " + "index : " + i);
                stack.pop();
            }
                stack.push(sorted_points[i]);
                //System.out.println("pushing :" + " " + sorted_points[i].x + " " + sorted_points[i].y + " " + "index : " + i);

        }

    }

    /**
     *Function to find element below the top of the stack
     *TC : O(1)
     * @param stack
     * @return
     */
    public pointhull stack_before_top(Stack<pointhull> stack){
            // top value
            pointhull top = stack.peek();

            stack.pop();

            pointhull topbelow = stack.peek();

            stack.push(top);

            //System.out.println("Below Top of stack value is : "+ " "+ topbelow.x+ " "+ topbelow.y);

            return topbelow;
    }

    /**
     * Function to find the orientation of the three ordered points
     * TC: Constant Time
     * Referred to https://preparingforcodinginterview.wordpress.com/2016/08/30/orientation-of-3-ordered-points/ for this idea
     * @param p1 point 1
     * @param p2 point 2
     * @param p3 point 3
     * @return left turn/right turn/ collinear
     */
    public int calculateOrientation(pointhull p1, pointhull p2, pointhull p3){
        //(y2 - y1)*(x3 - x2) - (y3 - y2)*(x2 - x1)
        /*
        Above expression is negative when counterclockwise - LEFT TURN
        Above expression is 0 when  collinear
        Above expression is positive when  clockwise - RIGHT TURN
         */

        int orientation = (p2.y - p1.y) * (p3.x - p2.x) - (p3.y-p2.y)*(p2.x-p1.x);

        return orientation;
    }

    /**
     * Function to print stack values and add in the final array list
     * O(n) - n is size of stack
     * @param stack
     */
    public void printStack(Stack<pointhull>stack){

        pointhull hullPoint;
        while(!stack.isEmpty()){
           hullPoint =stack.pop();
           hullArray.add(hullPoint);
            System.out.println(hullPoint.x + " " + hullPoint.y);
        }
    }

    /**
     * Function to create new file that contains no of vertices and points forming the Convex hull
     * Referred http://www.codejava.net/java-se/file-io/how-to-read-and-write-text-file-in-java
     * @param hullArray
     */
    public void printToFile(ArrayList<pointhull>hullArray){
        try {
            FileWriter writer = new FileWriter("Hull_GS.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write(Integer.toString(hullArray.size()));

            bufferedWriter.newLine();

            Collections.reverse(hullArray);

            for(pointhull p : hullArray){

                bufferedWriter.write(Integer.toString(p.x)+ " " + Integer.toString(p.y) );
                bufferedWriter.newLine();

            }

            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Main Function
    public static void main(String[]args) {

        CH_GrahamsScan sc = new CH_GrahamsScan();
        Stack<pointhull> stack = new Stack<pointhull>();

        //String fileName = "test2.txt";

        String fileName ;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name");
        fileName = (scanner.next());

        long startTime = System.currentTimeMillis();

        sc.readFile(fileName);

        sc.identify_bottom(points);

        //O(n^2)
        sc.sort_on_polar_angle(points,firstPoint);

        //Grahams Scan using Stack
        sc.stack_push_sortedOrderRtoL(stack,sorted_points);

        //print stack
        sc.printStack(stack);

        //write to file
        sc.printToFile(hullArray);

        long endTime = System.currentTimeMillis();
        long runningTime = (endTime - startTime);
        //long runningTime_sec = TimeUnit.MILLISECONDS.toSeconds(runningTime);

        System.out.println("Total Running time of algorithm is" + "\t" + runningTime +" " +"milli seconds");

        //Total = O(n^2) Time Complexity


    }
}
