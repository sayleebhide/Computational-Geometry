/**
 * Created by sayleebhide on 10/9/17.
 */
public class Line {
    PointSweep pointA;
    PointSweep pointB;

    public Line(PointSweep _A, PointSweep _B){
        pointA = _A;
        pointB = _B;
    }
    public Line(){
        pointA = null;
        pointB = null;
    }
}
