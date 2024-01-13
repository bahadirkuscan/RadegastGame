/*
Laked blocks have money lakes on top of them. They don't have any connections with other blocks that can cause
leakage. They have their lake's symbol.
 */
public class LakedBlock extends Block{
    public String lake_symbol;
    LakedBlock(String coordinate, int height, Lake lake, String lake_symbol){
        super(coordinate, height);
        this.lake_symbol = lake_symbol;
    }

    public void addStone(){
        super.addStone();
    }

}
