/**
 * Created by sayleebhide on 11/8/17.
 */
public class boundingbox {

    TPoint ll;
    TPoint ur;
    TPoint lr;
    TPoint ul;

    public boundingbox(TPoint _ll, TPoint _ur){
        ll = _ll;
        ur = _ur;
    }

    public void findBounds(){

        lr = new TPoint(ur.x , ll.y);
        ul = new TPoint(ll.x, ur.y);
    }
}
