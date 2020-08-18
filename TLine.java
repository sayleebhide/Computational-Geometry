/**
 * Created by sayleebhide on 11/8/17.
 */
public class TLine {
    TPoint P;
    TPoint Q;

    public TLine(TPoint _A , TPoint _B ){
        P = _A;
        Q = _B;
    }

    public TPoint getA(){
        return P;
    }

    public TPoint getB(){
        return Q;
    }
}
