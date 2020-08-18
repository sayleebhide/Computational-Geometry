/**
 * Created by sayleebhide on 11/8/17.
 */
public class TTrapezoid {
    TPoint lbv;
    TPoint rbv;
    TLine topSegment;
    TLine bottomSegment;

    TTrapezoid upperLeftNeighbour;
    TTrapezoid upperRightNeighbour;
    TTrapezoid lowerLeftNeighbour;
    TTrapezoid lowerRightNeighbour;

    String name;


    public TTrapezoid(TPoint _lbv, TPoint _rbv, TLine _topSegment, TLine _bottomSegment){
        lbv = _lbv;
        rbv = _rbv;
        topSegment = _topSegment;
        bottomSegment = _bottomSegment;
    }

    public void addULNeighbour(TTrapezoid _upperLeftTrapezoid){

        upperLeftNeighbour = _upperLeftTrapezoid;
    }

    public void addLLNeighbour(TTrapezoid _lowerLeftTrapezoid){

        lowerLeftNeighbour = _lowerLeftTrapezoid;

    }
    public void addURNeighbour(TTrapezoid _upperRightTrapezoid){
        upperRightNeighbour = _upperRightTrapezoid;

    }
    public void addLRNeighbour(TTrapezoid _lowerRightTrapezoid){

        lowerRightNeighbour = _lowerRightTrapezoid;

    }
    public void setName(String _name){
        name=_name;
    }
    public String getName(){
        return name;
    }

}
