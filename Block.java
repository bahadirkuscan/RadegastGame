/*
This class specifies a single stone block of the terrain. Every block has a height(stone count) and a coordinate.
There are 3 types of blocks in the final stage: Laked, Outer-border and Inner-border.
 */
public class Block{
    public String coordinate;
    public int height;


    Block(String coordinate, int height){
        this.coordinate = coordinate;
        this.height = height;
    }

    public void addStone(){
        height += 1;
    }

}
