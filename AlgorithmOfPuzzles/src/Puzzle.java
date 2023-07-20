import java.awt.image.BufferedImage;

public class Puzzle {

    private final BufferedImage puzzle;
    private final int[] upperSide;
    private final int[] lowerSide;
    private final int[] rightSide;
    private final int[] leftSide;
    private final int id;

    private static int counter = 1;

    public Puzzle(BufferedImage puzzle){
        this.puzzle = puzzle;
        int height = puzzle.getHeight();
        int width = puzzle.getWidth();

        upperSide = new int[width/3];
        lowerSide = new int[width/3];
        rightSide = new int[height/3];
        leftSide = new int[height/3];
        id = counter++;

        for(int i = 0; i<width/3; ++i){
            upperSide[i] = ((puzzle.getRGB(i*3, 0)&0xFF)
                    + (puzzle.getRGB(i*3+1, 0)&0xFF)
                    + (puzzle.getRGB(i*3+2, 0)&0xFF)
                    )/4;

            lowerSide[i] = ((puzzle.getRGB(i*3, height-1)&0xFF)
                    + (puzzle.getRGB(i*3+1, height-1)&0xFF)
                    + (puzzle.getRGB(i*3+2, height-1)&0xFF)
                    )/4;
        }

        for(int i = 0; i<height/3; ++i){
            leftSide[i] = ((puzzle.getRGB(0, i*3)&0xFF)
                    + (puzzle.getRGB(0, i*3 + 1)&0xFF)
                    + (puzzle.getRGB(0, i*3 + 2)&0xFF)
                    )/4;

            rightSide[i] = ((puzzle.getRGB(width-1, i*3)&0xFF)
                    + (puzzle.getRGB(width-1, i*3 + 1)&0xFF)
                    + (puzzle.getRGB(width-1, i*3 + 2)&0xFF)
                    )/4;
        }
    }

    public BufferedImage getPuzzle() {
        return puzzle;
    }

    public int[] getUpperSide() {
        return upperSide;
    }

    public int[] getLowerSide() {
        return lowerSide;
    }

    public int[] getRightSide() {
        return rightSide;
    }

    public int[] getLeftSide() {
        return leftSide;
    }

    public int getId() {
        return id;
    }
}
