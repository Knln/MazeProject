import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	
	/**
	 * Some serial ID
	 */
	private static final long serialVersionUID = 3273612744300406780L;
	
	private BufferedImage backgroundImage;	
	private BufferedImage foregroundImage;
	private int width;
	private int height;
	private boolean isForegroundVisible;

    public ImagePanel(String backgroundIcon, String foregroundIcon, int height, int width, boolean isForegroundVisible) {
        
    	// load the background image
    	if (backgroundIcon != null) {
    		try {
    			backgroundImage = ImageIO.read(new File(backgroundIcon));
    		} catch (IOException e) {
        		System.out.println("Error: " + backgroundImage + " not found");
        	}
    	}
        
        //load the foreground image
        if (foregroundIcon != null) {
    		try {
    			foregroundImage = ImageIO.read(new File(foregroundIcon));
    		} catch (IOException e) {
    			System.out.println("Error: " + foregroundImage + " not found");
    		}
    	}
        
        this.isForegroundVisible = isForegroundVisible;
        this.height = height;
        this.width = width;      
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (width * height == 0){
        	//if height and width are unreasonable (i.e. 0) use default image size
        	g2d.drawImage(backgroundImage, 0, 0, this);
        	if (this.isForegroundVisible == true){
        		g2d.drawImage(foregroundImage, 0, 0, this);
        	}
        } else {
        	//use designated width and height
        	g2d.drawImage(backgroundImage, 0, 0, width, height, this);
        	if (this.isForegroundVisible == true){
        		g2d.drawImage(foregroundImage, 0, 0, width, height, this);
        	}
        }
        g2d.dispose();
    }
}