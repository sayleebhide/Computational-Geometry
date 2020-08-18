import java.util.ArrayList;

/**
 * Created by sayleebhide on 11/8/17.
 */
public class TNode {

    public TNode left;
    public TNode right;

    String name;

    public ArrayList<TNode>nodeList=new ArrayList<>();

    int size;

    public TNode(){
        left = null;
        right = null;
    }

    public TNode getLeft(){
        return left;
    }

    public TNode getRight(){
        return right;
    }

    public void traverseInorder(TNode node){
        {
            if (node == null)
                return;

        /* first recur on left child */
            traverseInorder(node.left);

        /* then print the data of node */
            if(node instanceof XNode) {
                System.out.println(((XNode) node).point.x + " " + "XNODE" + " " + ((XNode) node).name);
                nodeList.add(node);
            }
            if(node instanceof YNode) {
                System.out.println(((YNode) node).line.P.x + " " + ((YNode) node).line.P.y + " " + ((YNode) node).line.Q.x + " " + ((YNode) node).line.Q.y + "YNODE" + " " + ((YNode) node).name);
                nodeList.add(node);
            }
            if(node instanceof Leaf) {
                System.out.println(((Leaf) node).trapezoid.lbv.x + " " + ((Leaf) node).trapezoid.rbv.x + " " + "TRAPEZOID" + " " + ((Leaf) node).name);
                nodeList.add(node);
            }

            size++;

        /* now recur on right child */
            traverseInorder(node.right);
        }

    }




}
