import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PuzzleController {

    public static List<Puzzle> puzzles = new ArrayList<>();

    public static List<Integer[]> horizontalRelations;
    public static List<Integer[]> verticalRelations;

    public static int[][] field;

    public static void addPuzzle(BufferedImage puzzle){
        Puzzle puzzle1 = new Puzzle(puzzle);
        puzzles.add(puzzle1);
    }
    public static void readPuzzles(File folder) throws IOException {
       // File folder = new File(path);
        for (final File fileEntry : folder.listFiles()) {
            if(ImageIO.read(fileEntry)!=null){
                addPuzzle(ImageIO.read(fileEntry));
            }
        }
    }

    public static Puzzle getById(int id){
        return puzzles.stream().filter(puzzle -> puzzle.getId()==id).findFirst().orElse(null);
    }

    private static double checkPuzzles(int id1, int id2, Position position){
        Puzzle puzzle1 = getById(id1);
        Puzzle puzzle2 = getById(id2);
        if(puzzle1==null||puzzle2==null) return 1;
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
        return res;
    }

    private static void setRelations(){
        horizontalRelations = new ArrayList<>();
        verticalRelations = new ArrayList<>();

        List<Double[]> list = new ArrayList<>();

        for(Puzzle puzzle1:puzzles){
            for(Puzzle puzzle2: puzzles){
                if(puzzle1==puzzle2) continue;
                int id1 = puzzle1.getId();
                int id2 = puzzle2.getId();
                list.add(new Double[]{(double)id1, (double)id2, 0.0, checkPuzzles(id1, id2, Position.HORIZONTAL)});
                list.add(new Double[]{(double)id1, (double)id2, 1.0, checkPuzzles(id1, id2, Position.VERTICAL)});
                }
        }
        list = list.stream().sorted(Comparator.comparingDouble(x -> x[3])).collect(Collectors.toList());
        int k = -1;
        for(int i = 0; i<list.size()-1; ++i){
            if(list.get(i)[3]*1.6<list.get(i+1)[3]){
                k = i;
                break;
            }
        }

        for(int i = 0; i<=k; ++i){
            Double[] params = list.get(i);
            if(params[2]==0.0) horizontalRelations.add(new Integer[]{params[0].intValue(), params[1].intValue()});
            else verticalRelations.add(new Integer[]{params[0].intValue(), params[1].intValue()});
        }
    }

    private static List<Puzzle> getLeftPuzzles(){
        List<Puzzle> res = new ArrayList<>();
        for(Puzzle puzzle: puzzles){
            List<Integer[]> list = verticalRelations.stream().filter(x->(x[1]== puzzle.getId())).toList();
            if(list.isEmpty()) res.add(puzzle);
        }
        return res;
    }

    private static void getLeftSide(){
        List<Puzzle> leftSide = getLeftPuzzles();

        Puzzle upperLeft = null;
        for(Puzzle puzzle: leftSide){
            int id = puzzle.getId();
            List<Integer[]> list = horizontalRelations.stream().filter(x->x[1]==id).toList();
            if(list.size()==0){
                upperLeft = puzzle;
                break;
            }
        }
        field = new int[leftSide.size()][puzzles.size()/leftSide.size()];
        field[0][0] = upperLeft.getId();

        for(int i = 1; i< leftSide.size(); ++i){
            final int j = i;
            int id = horizontalRelations.stream().filter(x->x[0]==field[j-1][0]).findFirst().get()[1];
            field[i][0] = id;
        }
    }

    private static void completeField() {
        for (int i = 0; i < field.length; ++i) {
            for (int j = 1; j < field[0].length; ++j) {
                final int k = field[i][j - 1];
                int id = verticalRelations.stream().filter(x -> x[0] == k).findFirst().get()[1];
                field[i][j] = id;
            }
        }
    }
    private static void buildImage() throws IOException {
        BufferedImage source =new BufferedImage(puzzles.get(0).getPuzzle().getWidth()*field[0].length, puzzles.get(0).getPuzzle().getHeight()*field.length, BufferedImage.TYPE_INT_BGR);

        Graphics g = source.getGraphics();

        for(int i = 0; i<field.length; ++i){
            for(int j = 0; j<field[0].length; ++j){
                g.drawImage(getById(field[i][j]).getPuzzle(), j*puzzles.get(0).getPuzzle().getWidth(), i*puzzles.get(0).getPuzzle().getHeight(), null);
            }
        }
        g.dispose();

//        File outputFile = new File("/Users/olegmisialo/Desktop/result.png");
//        ImageIO.write(source, "png", outputFile);

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                final JPanel component = new JPanel() {

                    @Override
                    public void paintComponent(final Graphics g) {
                        g.drawImage(source, 0, 0, null);
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(
                                source.getWidth(this), source.getHeight(this));
                    }
                };

                final JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(component);
                frame.pack();
                frame.setVisible(true);
            }
        });

    }
    public static void buildPuzzle() throws IOException {
        setRelations();
        getLeftSide();
        completeField();
        buildImage();

    }


}
