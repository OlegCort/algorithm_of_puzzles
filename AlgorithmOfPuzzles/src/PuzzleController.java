import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PuzzleController {

    private static final double MAX_DIVERGENCE = 0.025;
    public static List<Puzzle> puzzles = new ArrayList<>();

    public static List<Integer[]> horizontalRelations;
    public static List<Integer[]> verticalRelations;

    public static int[][] field;

    public static void addPuzzle(BufferedImage puzzle){
        Puzzle puzzle1 = new Puzzle(puzzle);
        puzzles.add(puzzle1);
    }
    public static void readPuzzles(String path) throws IOException {
        File folder = new File(path);
        for (final File fileEntry : folder.listFiles()) {
            if(ImageIO.read(fileEntry)!=null){
                System.out.println(fileEntry.getName());
                addPuzzle(ImageIO.read(fileEntry));
            }

        }
    }

    public static Puzzle getById(int id){
        return puzzles.stream().filter(puzzle -> puzzle.getId()==id).findFirst().orElse(null);
    }

    public static boolean checkPuzzles(int id1, int id2, Position position){
        Puzzle puzzle1 = getById(id1);
        Puzzle puzzle2 = getById(id2);
        if(puzzle1==null||puzzle2==null) return false;
        int[][] side1 = new int[1][3];
        int[][] side2 = new int[1][3];
        switch (position) {
            case HORIZONTAL -> {
                side1 = puzzle1.getLowerSide();
                side2 = puzzle2.getUpperSide();
            }
            case VERTICAL -> {
                side1 = puzzle1.getRightSide();
                side2 = puzzle2.getLeftSide();
            }
        }
        List<Double> relations = new ArrayList<>();
        for(int i = 0; i<side1.length; ++i){
            relations.add(((double)(
                    Math.abs(side1[i][0] - side2[i][0])
                    + Math.abs(side1[i][1] - side2[i][1])
                    + Math.abs(side1[i][2] - side2[i][2])

            ))/765);
        }
        double res = 0;
        for(Double num: relations)
            res+=num;
        res/=relations.size();
        System.out.println(res);
       // return !(relations.stream().sorted((x, y) -> Double.compare(y, x)).findFirst().get() > MAX_DIVERGENCE);
        return (res < MAX_DIVERGENCE);
    }

    public static void setRelations(){
        horizontalRelations = new ArrayList<>();
        verticalRelations = new ArrayList<>();
        int res;
        for(Puzzle puzzle1:puzzles){
            for(Puzzle puzzle2: puzzles){
                if(puzzle1==puzzle2) continue;
                int id1 = puzzle1.getId();
                int id2 = puzzle2.getId();
                if(!horizontalRelations.contains(new Integer[]{id2, id1, 1})) {
                    res = checkPuzzles(id1, id2, Position.HORIZONTAL) ? 1 : 0;
                    horizontalRelations.add(new Integer[]{id1, id2, res});
                }
                if(!verticalRelations.contains(new Integer[]{id2, id1, 1})){
                    res = checkPuzzles(id1, id2, Position.VERTICAL) ? 1 : 0;
                    verticalRelations.add(new Integer[]{id1, id2, res});
                }
            }
        }
    }

    public static void paint(){
        for(int i = 0; i<horizontalRelations.size(); ++i){
            System.out.println(horizontalRelations.get(i)[0] + " " + horizontalRelations.get(i)[1] + " " + horizontalRelations.get(i)[2]);
        }
        System.out.println("\n");
        for(int i = 0; i<horizontalRelations.size(); ++i){
            System.out.println(verticalRelations.get(i)[0] + " " + verticalRelations.get(i)[1] + " " + verticalRelations.get(i)[2]);
        }
    }

    public static List<Puzzle> getLeftPuzzles(){
        List<Puzzle> res = new ArrayList<>();
        for(Puzzle puzzle: puzzles){
            List<Integer[]> list = verticalRelations.stream().filter(x->(x[1]== puzzle.getId())&&(x[2]==1)).toList();
            if(list.isEmpty()) res.add(puzzle);
        }
        System.out.println(res);
        return res;
    }

    public static void getLeftSide(){
        List<Puzzle> leftSide = getLeftPuzzles();

        Puzzle upperLeft = null;
        for(Puzzle puzzle: leftSide){
            int id = puzzle.getId();
            List<Integer[]> list = horizontalRelations.stream().filter(x->x[1]==id).filter(x->x[2]==1).toList();
            if(list.size()==0){
                upperLeft = puzzle;
                break;
            }
        }

        field = new int[leftSide.size()][puzzles.size()/leftSide.size()];
        field[0][0] = upperLeft.getId();
        List<Puzzle> unused = leftSide;
        unused.remove(upperLeft);

        for(int i = 1; i< leftSide.size(); ++i){
            for(Puzzle puzzle: unused){
                if(checkPuzzles(field[i-1][0], puzzle.getId(), Position.HORIZONTAL)){
                    field[i][0] = puzzle.getId();
                    unused.remove(puzzle);
                }
            }
        }
        System.out.println(puzzles.size());
        System.out.println(leftSide.size());
        for(int i = 0; i < field.length; ++i){
            for(int j = 0; j < field[0].length; ++j){
                System.out.print(field[i][j] + " ");
            }
            System.out.println();
        }
    }





}
