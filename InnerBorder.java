/*
Inner-border blocks are like outer-border blocks, only difference is the way of leakage. The money lake that could be
on top of them leaks over the laked blocks and then reaches the outer-borders, to leak into the void afterwards.
 */
public class InnerBorder extends Block{
    InnerBorder(String coordinate, int height){
        super(coordinate,height);
    }

    public void addStone(){
        super.addStone();
    }

}
