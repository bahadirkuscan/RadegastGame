/*
This class specifies a lake in the terrain. There is a lake symbol that advances with every new lake creation.
Every lake store the blocks they contain and the heights of the surrounding borders to find the minimum and calculate
the score of the lake.
 */
import java.util.ArrayList;

public class Lake {
    private static char letter1 = 'A';
    private static char letter2 = 'A';
    public static String symbol = "" + letter1;
    public ArrayList<Block> blocks;
    public ArrayList<Integer> surrounding_border_heights;
    public int minimum_border_height;

    Lake(){
        blocks = new ArrayList<>();
        surrounding_border_heights = new ArrayList<>();
    }

    public static void nextSymbol(){  //method for advancing to the next symbol
        if (symbol.length() == 1){
            if (letter1 == 'Z'){
                letter1 = 'A';
                symbol = letter1 + "" + letter2;
            }

            else {
                letter1 += 1;
                symbol = "" + letter1;
            }
        }

        else{
            if (letter2 == 'Z'){
                letter1 += 1;
                letter2 = 'A';
                symbol = letter1 + "" + letter2;
            }

            else{
                letter2 += 1;
                symbol = letter1 + "" + letter2;
            }
        }
    }

    public double calculateScore(){ //score is calculated with comparing minimum border height with lake's block heights
        double score = 0;
        minimum_border_height = surrounding_border_heights.get(0);
        for (int x : surrounding_border_heights){
            if (x < minimum_border_height){
                minimum_border_height = x;
            }
        }

        for (Block block : blocks){
            score += minimum_border_height - block.height;
        }
        return Math.sqrt(score);
    }
}
