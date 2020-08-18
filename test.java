/**
 * Created by sayleebhide on 9/26/17.
 */
import java.util.*;
import java.lang.*;
import java.util.Scanner.*;
import java.io.*;
public class test {

    public static void printToFile(ArrayList<pointhull>hullArray){
        try {
            FileWriter writer = new FileWriter("TEST50.txt", true);
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

    public static void main(String[]args){
        int [] randomarr = new int[5];

        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        ArrayList<pointhull> arrayList1 = new ArrayList<pointhull>();

        LinkedHashSet<pointhull> list = new LinkedHashSet<>();


        for(int i =0; i<5;i++){
            randomarr[i] = i;
        }

        for(int i: randomarr){
            System.out.println("Values :"+ randomarr[i]);
            arrayList.add(randomarr[i]);
        }
        System.out.println(arrayList.size());
        arrayList.remove(2);
        System.out.println(arrayList.size());

        double a = Math.toDegrees(Math.atan(-3.33)) + 180;
        System.out.println(a);
        System.out.println(Math.toDegrees(0));

        arrayList.add(1, 10);
        arrayList.add(2, 20);
        arrayList.add(3, null);

        for(int i= 0  ; i <arrayList.size() ; i ++){
            System.out.println("Values :"+ arrayList.get(i));
        }

        Random rand = new Random();
        //generate test cases
        for(int i = 0; i< 50 ;i++){
            //int x = rand.nextInt(50);
            //int y=  rand.nextInt(10);
            int x=(rand.nextInt(80)-40);
            int y=(rand.nextInt(100)-50);
            pointhull point = new pointhull(x,y);
            list.add(point);
        }

        for(pointhull p : list){
            arrayList1.add(p);
        }

        printToFile(arrayList1);



    }
}
