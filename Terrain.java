/*
This class specifies the terrain. The terrain stores its horizontal length, vertical length and every single block it contains.
Its method prints out the terrain with the requested output format.
 */


public class Terrain {
    public static int TERRAIN_HORIZONTAL_LENGTH;
    public static int TERRAIN_VERTICAL_LENGTH;
    public static Block[][] blocks;


    Terrain(int width, int height){
        TERRAIN_HORIZONTAL_LENGTH = width;
        TERRAIN_VERTICAL_LENGTH = height;
        blocks = new Block[width][height];
    }


    public static void terrainPrinter(String[] width_coords, int[] height_coords){  //Terrain is printed out with requested format
        for (int h = 0; h < TERRAIN_VERTICAL_LENGTH; h++){
            if (h < 10){
                System.out.print("  " + height_coords[h]);
            }
            else{
                System.out.print(" " + height_coords[h]);
            }


            for (int w = 0; w < TERRAIN_HORIZONTAL_LENGTH; w++){
                if (!(blocks[w][h] instanceof LakedBlock)) {
                    if (blocks[w][h].height < 10) {
                        System.out.print("  " + blocks[w][h].height);
                    } else {
                        System.out.print(" " + blocks[w][h].height);
                    }
                }
                else{
                    LakedBlock x = (LakedBlock) blocks[w][h];
                    if (x.lake_symbol.length() == 1){
                        System.out.print("  " + x.lake_symbol);
                    }
                    else {
                        System.out.print(" " + x.lake_symbol);
                    }
                }
            }
            System.out.println(" ");
        }

        System.out.print("   ");
        for (String letters : width_coords){
            if (letters.length() < 2){
                System.out.print("  " + letters);
            }
            else{
                System.out.print(" " + letters);
            }
        }
        System.out.println(" ");
    }
}
