/**
 * Created by sayleebhide on 10/14/17.
 */
public class event {

    PointSweep point;
    Line linesegment;
    Line intersectingLine;
    char type; //l , r,  i


    public event(PointSweep _point , char typeofPoint , Line _linesegment){

       point = _point;
        type = typeofPoint;
        linesegment = _linesegment;
    }

    public event(PointSweep _point, char typeofPoint , Line _linesegment , Line _linesegment2){
        point = _point;
        type = typeofPoint;
        linesegment = _linesegment;
        intersectingLine = _linesegment2;
    }
}
