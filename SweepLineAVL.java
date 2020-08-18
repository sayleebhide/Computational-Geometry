import java.util.ArrayList;

/**
 * Created by sayleebhide on 10/13/17.
 */
public class SweepLineAVL {

    SweepLineAVLNode root;

    Line pred;

    Line succ;

    public SweepLineAVL(){
        root =null;
    }

    public ArrayList<event> eventList = new ArrayList<>();

    public ArrayList<sweepLineEntry> entryList = new ArrayList<>();

    public void clearTree(){
            root = null;
    }

    public int height(SweepLineAVLNode node){
        if(node==null)
            return 0;
        else
            return node.height;
    }

    public int max(int l, int r) {
        return (l > r) ? l : r;
    }

    public SweepLineAVLNode rightRotate(SweepLineAVLNode node){
        SweepLineAVLNode x = node.left;
        SweepLineAVLNode T2 = x.right;

        x.right = node;
        node.left = T2;

        node.height = max(height(node.left) , height(node.right)) +1 ;
        x.height = max(height(x.left) , height(x.right)) +1;

        return x;
    }

    public SweepLineAVLNode leftRotate(SweepLineAVLNode node){
        SweepLineAVLNode y = node.right;
        SweepLineAVLNode T2 = y.left;

        // Perform rotation
        y.left = node;
        node.right = T2;

        //  Update heights
        node.height = max(height(node.left), height(node.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;

        // Return new root
        return y;
    }

    public int balanceFactor(SweepLineAVLNode node){
        if(node ==null)
            return 0;

        else
            return height(node.left)- height(node.right);
    }

    public SweepLineAVLNode insertinEventQueue(SweepLineAVLNode node, event event){

        //System.out.println("Inserting in Event queue" + event.point.x + " " + event.point.y);

        if(node == null) {
            node = new SweepLineAVLNode(event);
            return node;
        }

        if(event.point.x < node.event.point.x){
            node.left = insertinEventQueue(node.left , event);
        }

        if (event.point.x > node.event.point.x){
            node.right = insertinEventQueue(node.right , event);
        }

        if(event.point.x == node.event.point.x){
            if (event.point.y < node.event.point.y){
                node.left  = insertinEventQueue(node.left , event);
            }

            else if (event.point.y > node.event.point.y )
                node.right = insertinEventQueue(node.right,event);
        }
        if(event.point.x==node.event.point.x && event.point.y == node.event.point.y)
            return node;

        node.height = 1 + max(height(node.left) , height(node.right));

        int bf = balanceFactor(node);

        //left child of left subtree
        if(bf>1 && event.point.x < node.left.event.point.x){
            return rightRotate(node);
        }

        //right child of right subtree
        if(bf < -1 && event.point.x > node.right.event.point.x){
            return leftRotate(node);
        }

        //right child of left subtree
        if(bf > 1 && event.point.x > node.left.event.point.x){
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if(bf < -1 && event.point.x < node.right.event.point.x){
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;

    }

    public SweepLineAVLNode insertinSweepLineStatus(SweepLineAVLNode node, sweepLineEntry entry){

        //System.out.println("Inserting in sweep line " + entry.line.pointA.x + " " + entry.line.pointA.y + " " + entry.line.pointB.x + " "+ entry.line.pointB.y);
        //System.out.println("hi");

        if(node == null) {
            node = new SweepLineAVLNode(entry);
            return node;
        }

        if(entry.ycoeff < node.entry.ycoeff){
            node.left = insertinSweepLineStatus(node.left ,entry);
        }

        if (entry.ycoeff > node.entry.ycoeff){
            node.right = insertinSweepLineStatus(node.right , entry);
        }

        if(entry.ycoeff == node.entry.ycoeff){
            node.right = insertinSweepLineStatus(node.right,entry);
        }
        else
            return node;

        node.height = 1 + max(height(node.left) , height(node.right));

        int bf = balanceFactor(node);

        //left child of left subtree
        if(bf>1 && entry.ycoeff < node.entry.ycoeff){
            return rightRotate(node);
        }

        //right child of right subtree
        if(bf < -1 && entry.ycoeff > node.entry.ycoeff){
            return leftRotate(node);
        }

        //right child of left subtree
        if(bf > 1 && entry.ycoeff > node.entry.ycoeff){
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if(bf < -1 && entry.ycoeff < node.entry.ycoeff){
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;

    }

    public void printInorder(SweepLineAVLNode node)
    {
        //System.out.println("came here" + node.data);
        if (node == null) {
            return;
        }

        /* first recur on left child */
        printInorder(node.left);

        /* then print the data of node */
        //System.out.print(" [" + " " + node.event.point.x + " " + node.event.point.y + " " +  "" + node.event.type + "]");
        //System.out.println();

        //eventList.add(node.event);
        if(!eventList.contains(node.event))
            eventList.add(node.event);

        /* now recur on right child */
        printInorder(node.right);
    }

    public void printInorderSweepLine(SweepLineAVLNode node)
    {
        //System.out.println("came here" + node.data);
        if (node == null) {
            return;
        }

        /* first recur on left child */
        printInorderSweepLine(node.right);

        /* then print the data of node */
        //System.out.print(" [" + " " + node.entry.xcoeff + " " + node.entry.ycoeff + " " +  "" + "]");
        //System.out.println();

        //eventList.add(node.event);
        if(!entryList.contains(node.entry))
            entryList.add(node.entry);

        /* now recur on right child */
        printInorderSweepLine(node.left);
    }

}
