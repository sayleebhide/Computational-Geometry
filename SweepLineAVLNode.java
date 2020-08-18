/**
 * Created by sayleebhide on 10/13/17.
 */
public class SweepLineAVLNode {

    SweepLineAVLNode left , right;
    int data;
    PointSweep data_point;
    Line line;
    event event;
    sweepLineEntry entry;
    int height;
    double xcoeff;
    double ycoeff;

    public SweepLineAVLNode(){
        left = null;
        right =null;
        data = 0;
        height = 0;
    }

    public SweepLineAVLNode(int n){
        data = n;
        height = 1;
    }

    public SweepLineAVLNode(PointSweep point){
        data_point = point;
        height = 1;
    }

    public SweepLineAVLNode(event _event){
        event = _event;
        height = 1;
    }

    public SweepLineAVLNode(sweepLineEntry _entry){
        entry = _entry;
        height = 1;
    }

    public SweepLineAVLNode(Line _line , double a , double b){
        line = _line;
        height = 1;
        xcoeff = a;
        ycoeff =b;
    }
}
