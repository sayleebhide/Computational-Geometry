/**
 * Created by sayleebhide on 10/9/17.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.*;

public class PlaneSweep_BF {

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

    /**
     * Helper to me - to check if values have entered data structure correctly
     * @param lineArray array containing lines
     */
    public void printLineArray(Line[] lineArray){
        for(int i =0; i < lineArray.length ; i++){

            System.out.println("The line in line array" + " " + i);
            System.out.println("The first point in Line array is" + lineArray[i].pointA.x + " " + lineArray[i].pointA.y);
            System.out.println("The Second point in Line array is" + lineArray[i].pointB.x + " " + lineArray[i].pointB.y);
            System.out.println("--------");
        }
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
    public void findIntersectionPoint(PointSweep L , PointSweep M , PointSweep N , PointSweep O){

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
            intersectionPointList.add(new PointSweep(x_int , y_int));
            //System.out.println("Added" + " " + x_int + " "+ y_int);
        }

    }

    /**
     * Referred http://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
     * @param lineArray
     */
    public void bruteForce_Sweep(Line [] lineArray){

        //Iterate through line array and check if eveery line intersects with every other line. This will take O(n^2)

        for( int i = 0 ; i < lineArray.length ; i ++){
            for (int j = i+1 ; j < lineArray. length ; j++){
                double orient1 = orient(lineArray[i].pointA , lineArray[i].pointB , lineArray[j].pointA);
                double orient2 = orient(lineArray[i].pointA , lineArray[i].pointB , lineArray[j].pointB);
                double orient3 = orient(lineArray[j].pointA , lineArray[j].pointB , lineArray[i].pointA);
                double orient4 = orient(lineArray[j].pointA , lineArray[j].pointB , lineArray[i].pointB);

                //general case
                if (orient1 != orient2 && orient3!= orient4){
                    //System.out.println("Sending" + " " + i + " " + j);
                    findIntersectionPoint(lineArray[i].pointA , lineArray[i].pointB , lineArray[j].pointA , lineArray[j].pointB);
                }
                //special cases
                if (orient1 == 0  && testCollinear(lineArray[i].pointA , lineArray[j].pointA , lineArray[i].pointB)==1){
                    //System.out.println("Sending" + " " + i + " " + j);
                    findIntersectionPoint(lineArray[i].pointA , lineArray[i].pointB , lineArray[j].pointA , lineArray[j].pointB);
                }

                if(orient2 ==0 && testCollinear(lineArray[i].pointA , lineArray[j].pointB , lineArray[i].pointB)==1){
                    //System.out.println("Sending" + " " + i + " " + j);
                    findIntersectionPoint(lineArray[i].pointA , lineArray[i].pointB , lineArray[j].pointA , lineArray[j].pointB);
                }

                if(orient3 ==0 && testCollinear(lineArray[j].pointA , lineArray[i].pointA , lineArray[j].pointB)==1){
                    //System.out.println("Sending" + " " + i + " " + j);
                    findIntersectionPoint(lineArray[j].pointA , lineArray[i].pointB , lineArray[j].pointA , lineArray[j].pointB);
                }

                if(orient4 ==0 && testCollinear(lineArray[i].pointA , lineArray[i].pointB , lineArray[i].pointB)==1){
                    //System.out.println("Sending" + " " + i + " " + j);
                    findIntersectionPoint(lineArray[j].pointA , lineArray[i].pointB , lineArray[j].pointA , lineArray[j].pointB);
                }
                else
                    continue;

            }
        }
    }

    public void writeFile(ArrayList<PointSweep>intersectionPointList){
        try {
            FileWriter writer = new FileWriter("PlaneSweep_BF.txt", true);
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



    public static void main(String[]args){

        PlaneSweep_BF ps = new PlaneSweep_BF();

        //String fileName = "datapoints.txt";
        String fileName ;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file name");
        fileName = (scanner.next());

        ps.readFile(fileName);

        //ps.printLineArray(lineArray);
        long sttime = System.currentTimeMillis();

        ps.bruteForce_Sweep(lineArray);

        long etime = System.currentTimeMillis();

        ps.writeFile(intersectionPointList);

        //System.out.println("Total no of intersection points are" + " " + intersectionPointList.size());
        System.out.println("Time " + (etime-sttime));


    }
}
