import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Splitter {

    public static void split(String path, int num) throws IOException {
        File f = new File(path);
        int rows = getRows(num);
        int columns = num/rows;
        BufferedImage img = ImageIO.read(f);

        List<BufferedImage> pieces = new ArrayList<>();
        int chunkWidth = img.getWidth() / columns;
        int chunkHeight = img.getHeight() / rows;

        for (int x = 0; x < rows; x++)
            for (int y = 0; y < columns; y++)
            {
                pieces.add(img.getSubimage(chunkWidth*y, chunkHeight*x,chunkWidth, chunkHeight));
            }
        int counter = 1;
        for(BufferedImage piece: pieces){

            File outputfile = new File("src/puzzles/piece"+counter+  ".png");
            ImageIO.write(piece, "png", outputfile);
            counter++;
        }
    }

    public static int getRows(int num){
        int k = (int)Math.sqrt(num);
        while(num%k!=0)
            k--;
        return k;
    }

}
