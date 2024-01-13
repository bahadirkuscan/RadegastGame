/*
Outer-border blocks have such heights and surroundings that the money lake that could be on top of them would leak directly
into the void without passing over any laked blocks. Therefore, they don't have any lake information and they take part in determining
lake volumes.
 */
public class OuterBorder extends Block{
    OuterBorder(String coordinate, int height){
        super(coordinate,height);
    }

    public void addStone(){
        super.addStone();
    }
}
