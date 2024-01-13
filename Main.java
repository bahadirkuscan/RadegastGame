/*
This is the Main class. This class reads the input file and the user inputs, creates and updates the terrain accordingly.
The class contains methods to determine the types of the blocks, identify lakes and make adjustments for the output.
The way of calling the methods are explained over each method body.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("input.txt");
        Scanner scanner = new Scanner(file);
        int horizontal_length = scanner.nextInt();
        int vertical_length = scanner.nextInt();
        new Terrain(horizontal_length, vertical_length); //Terrain is created
        int[] vertical_coordinates = new int[Terrain.TERRAIN_VERTICAL_LENGTH];
        String[] horizontal_coordinates = new String[Terrain.TERRAIN_HORIZONTAL_LENGTH];
        ArrayList<String> coordinates = new ArrayList<>();

        //setting the letter coordinates
        for (int h = 0; h < Terrain.TERRAIN_HORIZONTAL_LENGTH; h++){
            if (h < 26){
                horizontal_coordinates[h] = "" + (char)(h +97);
            }
            else{
                horizontal_coordinates[h] = (char)(h /26+96) + "" + (char)(h %26+97);
            }
        }

        //setting the number coordinates
        for (int v = 0; v < Terrain.TERRAIN_VERTICAL_LENGTH; v++){
            vertical_coordinates[v] = v;
        }

        //merging letter and number coordinates and creating every block with their properties
        for (int v = 0; v < Terrain.TERRAIN_VERTICAL_LENGTH; v++){
            for (int h = 0; h < Terrain.TERRAIN_HORIZONTAL_LENGTH; h++){
                coordinates.add(horizontal_coordinates[h] + vertical_coordinates[v]);

                int block_height = scanner.nextInt();
                Terrain.blocks[h][v] = new Block(coordinates.get(coordinates.size()-1), block_height);
                //outer-borders with direct connection to the void
                if (h == 0 || h == Terrain.TERRAIN_HORIZONTAL_LENGTH - 1 || v == 0 || v == Terrain.TERRAIN_VERTICAL_LENGTH - 1){
                    Terrain.blocks[h][v] = new OuterBorder(Terrain.blocks[h][v].coordinate, Terrain.blocks[h][v].height);
                }
            }
        }

        scanner.close();

        int stone_number = 1;
        Scanner input_taker = new Scanner(System.in);
        Terrain.terrainPrinter(horizontal_coordinates, vertical_coordinates);

        //input taking and stone adding phase
        while (stone_number < 11){
            System.out.printf("Add stone %d / 10 to coordinate:",stone_number);
            String input = input_taker.next();
            if (!coordinates.contains(input)){
                System.out.println("Not a valid step!");
                continue;
            }
            int index = coordinates.indexOf(input);
            Terrain.blocks[index % Terrain.TERRAIN_HORIZONTAL_LENGTH][index / Terrain.TERRAIN_HORIZONTAL_LENGTH].addStone();
            stone_number += 1;

            Terrain.terrainPrinter(horizontal_coordinates, vertical_coordinates);
            System.out.println("---------------");
        }

        //setting the new outer-borders after the stone placement
        for (int h = 0; h < Terrain.TERRAIN_HORIZONTAL_LENGTH; h++){
            outerBorderConnector(h,0);
            outerBorderConnector(h,Terrain.TERRAIN_VERTICAL_LENGTH -1);
        }
        for (int v = 0; v < Terrain.TERRAIN_VERTICAL_LENGTH; v++){
            outerBorderConnector(0, v);
            outerBorderConnector(Terrain.TERRAIN_HORIZONTAL_LENGTH -1, v);
        }

        //setting the inner-borders (if any) after outer-borders are finalized
        for (int v = 0; v < Terrain.TERRAIN_VERTICAL_LENGTH; v++){
            for (int h = 0; h < Terrain.TERRAIN_HORIZONTAL_LENGTH; h++){
                Block current_block = Terrain.blocks[h][v];
                if (!(current_block instanceof OuterBorder)){
                    ArrayList<Block> connected_borders = connectedBorders(h, v,new ArrayList<>(),new ArrayList<>());
                    if (connected_borders.size() == 0){  // no border connection
                        continue;
                    }
                    int min = connected_borders.get(0).height;
                    for (Block b : connected_borders){
                        if (b.height < min){
                            min = b.height;
                        }
                    }

                    // having a connection with a lower or equal border means there will be leakage and the block will be an inner-border
                    if (current_block.height >= min){
                        Terrain.blocks[h][v] = new InnerBorder(current_block.coordinate, current_block.height);
                    }
                }
            }
        }

        // flooding the terrain and identifying lakes
        double score = 0;
        for (int v = 0; v < Terrain.TERRAIN_VERTICAL_LENGTH; v++){
            for (int h = 0; h < Terrain.TERRAIN_HORIZONTAL_LENGTH; h++){
                Block current_block = Terrain.blocks[h][v];
                Lake lake = new Lake();
                if (!(current_block instanceof OuterBorder) && !(current_block instanceof InnerBorder) && !(current_block instanceof LakedBlock)){
                    flooder(h, v,lake);
                    Lake.nextSymbol();
                    score += lake.calculateScore();
                }
            }
        }

        Terrain.terrainPrinter(horizontal_coordinates, vertical_coordinates);
        System.out.print("Final score: " + significantFigureFix(score));


    }



    /*
    outerBorderConnector is called with every single initial outer-border (blocks located at the edges of the terrain), recursive method
    calls every single neighbour block that are equal or higher than the original block (since this is the way of leakage) and also not an outer-border already to set them as outer-borders as well
     */
    private static void outerBorderConnector(int horizontal_coordinate, int vertical_coordinate){
        Terrain.blocks[horizontal_coordinate][vertical_coordinate] = new OuterBorder(Terrain.blocks[horizontal_coordinate][vertical_coordinate].coordinate, Terrain.blocks[horizontal_coordinate][vertical_coordinate].height);
        Block current_block = Terrain.blocks[horizontal_coordinate][vertical_coordinate];
        for (int i = -1 ; i < 2 ; i++) {
            for (int k = -1; k < 2; k++) {
                if (i == 0 && k == 0) {
                    continue;
                }
                if (horizontal_coordinate +k >= 0 && horizontal_coordinate +k < Terrain.TERRAIN_HORIZONTAL_LENGTH && vertical_coordinate +i >= 0 && vertical_coordinate +i < Terrain.TERRAIN_VERTICAL_LENGTH) {
                    Block neighbour = Terrain.blocks[horizontal_coordinate + k][vertical_coordinate + i];
                    if (neighbour.height >= current_block.height && !(neighbour instanceof OuterBorder)) {
                        outerBorderConnector(horizontal_coordinate + k, vertical_coordinate + i);
                    }
                }
            }
        }
    }

    /*
    connectedBorders is called with every single block except the outer-borders. This recursive method calls every neighbour [if not called already by the original (this condition is checked with the help of called_blocks)]
     looking for borders. Found borders are stored in an array list which in the end contains every connected border.
     Returned list is used later to check if the original block is an inner-border or not by comparing the minimum height of the connected borders and the block's height
     */
    private static ArrayList<Block> connectedBorders(int horizontal_coordinate, int vertical_coordinate, ArrayList<Block> connected_borders, ArrayList<Block> called_blocks){
        Block current_block = Terrain.blocks[horizontal_coordinate][vertical_coordinate];
        called_blocks.add(current_block);
        for (int i = -1 ; i < 2 ; i++) {
            for (int k = -1; k < 2; k++) {
                if (i == 0 && k == 0) {
                    continue;
                }

                Block neighbour = Terrain.blocks[horizontal_coordinate + k][vertical_coordinate + i];
                if (!(neighbour instanceof OuterBorder) && !(neighbour instanceof InnerBorder) && !called_blocks.contains(neighbour)) {
                    connectedBorders(horizontal_coordinate + k, vertical_coordinate + i, connected_borders, called_blocks);
                }
                if (neighbour instanceof OuterBorder || neighbour instanceof InnerBorder){
                    connected_borders.add(neighbour);
                }
            }
        }
        return connected_borders;
    }


    /*
    flooder is called with every block that don't have their type determined yet. Recursive method calls every undetermined neighbour that are to be "laked".
    The called block is turned into a Laked Block and given its lake symbol.
    This method also helps to store the surrounding borders' heights in order to use it in score calculation later on.
     */
    private static void flooder(int horizontal_coordinate, int vertical_coordinate, Lake lake){
        Terrain.blocks[horizontal_coordinate][vertical_coordinate] = new LakedBlock(Terrain.blocks[horizontal_coordinate][vertical_coordinate].coordinate, Terrain.blocks[horizontal_coordinate][vertical_coordinate].height, lake, Lake.symbol);
        lake.blocks.add(Terrain.blocks[horizontal_coordinate][vertical_coordinate]);
        for (int i = -1 ; i < 2 ; i++){
            for (int k = -1 ; k < 2 ; k++){
                if(i==0 && k==0){
                    continue;
                }
                Block neighbour = Terrain.blocks[horizontal_coordinate +k][vertical_coordinate +i];
                if (neighbour instanceof OuterBorder|| neighbour instanceof InnerBorder){
                    lake.surrounding_border_heights.add(neighbour.height);
                }
                else {
                    if (!(neighbour instanceof LakedBlock)){
                        flooder(horizontal_coordinate +k, vertical_coordinate +i,lake);
                    }
                }

            }
        }
    }

    // this method is used to prepare the score for the requested output format
    private static String significantFigureFix(double number){
        String result = "";
        result += (int) number + ".";
        String x = "" + Math.round(((int)(number*1000)) / 10.0) % 100;
        if (x.equals("0")){
            x = "00";
        }
        return result + x;
    }
}



