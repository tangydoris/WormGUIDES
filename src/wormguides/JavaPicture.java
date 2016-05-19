package wormguides;

import java.awt.*;
import java.awt.MediaTracker;
import com.sun.image.codec.jpeg.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.*; 
import javax.imageio.*;

public class JavaPicture{
    
    public BufferedImage bimg;
    public JFrame shower = new JFrame();
    public ImageIcon imgIcon;
    public Canvas canvas;
    public Image image;
    
    /** 
     * Constructor
     */
    public JavaPicture(){
        //No picture, no values
    }
    
    public int getWidth(){
        return bimg.getWidth();
    }
    
    public int getHeight(){
        return bimg.getHeight();
    }
    
    
    /**
     * Load image
     */
    public boolean loadImage(File file){
       try{
            /*bimg = null;
            filename = "file://" + filename;
            URL u = new URL(filename);
            File f = new File(u.getPath());
            bimg = ImageIO.read(f);
            */
            
            //Toolkit toolkit = Toolkit.getDefaultToolkit();
			//image = toolkit.getImage(filename);
    	   image = ImageIO.read(file);
			MediaTracker mediaTracker = new MediaTracker(shower);
			mediaTracker.addImage(image, 0);
			try
			{
				mediaTracker.waitForID(0);
			}
			catch (InterruptedException ie)
			{
				//The file did not load
				System.err.println(ie);
			}
			
			bimg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics g = bimg.getGraphics();
			g.drawImage(image, 0, 0, null);

            return true;
        }
        catch (Exception e) {return false;}
    }

    public void createNewImage(int width, int height){
	bimg = null;
	bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void repaintImage(){
        if (shower.isVisible()){
            imgIcon.setImage(bimg.getScaledInstance(bimg.getWidth(), bimg.getHeight(), Image.SCALE_FAST));
            shower.repaint();
        }
    }

    public void showPictureWithTitle(String s){
        if (shower.isVisible()){
            imgIcon.setImage(bimg.getScaledInstance(bimg.getWidth(), bimg.getHeight(), Image.SCALE_FAST));
            shower.setTitle(s);
            shower.repaint();
        }
        else {
            shower = new JFrame(s);
            imgIcon = new ImageIcon(bimg.getScaledInstance(bimg.getWidth(), bimg.getHeight(), Image.SCALE_FAST));
            shower.getContentPane().add(new JLabel(imgIcon));
            shower.setResizable(false);
            shower.pack();
            shower.setVisible(true);
        }
    }

    /**
     * Saves the image represented by the JavaPicture object onto disk.
     * 
     * @param newfilename the file name to save to
     * @exception java.io.IOException raised if the save fails
     */
    public boolean saveImage(String newfilename) throws java.io.IOException
    {
    	
    	FileOutputStream out;
        JPEGImageEncoder jpeg;
	File filen;
        try {
	    filen = new File(newfilename);
	    //if (filen.canWrite()){
		//return false;}
            out = new FileOutputStream(filen);
        }
        catch (Exception e){
            System.out.println("Sorry -- that filename ("+newfilename+") isn't working");
            return false;
        }
        
        try {
            jpeg = JPEGCodec.createJPEGEncoder(out);
        }
        catch (Exception e) {
            System.out.println("Unable to create a JPEG encoder");
            return false;
        }
        
        
        JPEGEncodeParam param = jpeg.getDefaultJPEGEncodeParam(bimg);
        param.setQuality(1.0f,true);
        jpeg.encode(bimg,param);
        out.close();
	return true;
    }

    /**
     * Returns the pixel value of a pixel in the picture, given its coordinates.
     *
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @return the pixel value as an integer
     */
    public int getBasicPixel(int x, int y)
    {
        // to access pixel at row 'j' and column 'i' from the upper-left corner of
        // image.
        return bimg.getRGB(x,y);
    }
    
    /** 
     * Sets the value of a pixel in the picture.
     *
     * @param x the x coordinate of the pixel
     * @param y the y coordinate of the pixel
     * @param rgb the new rgb value of the pixel
     */     
    public void setBasicPixel(int x, int y, int rgb)
    {
        bimg.setRGB(x,y,rgb);
    }
    
    /**
     * Returns a JavaPixel object representing a pixel in the picture given its coordinates
     *
     * @param x the x coordinates of the pixel
     * @param y the y coordinates of the pixel
     * @return a JavaPixel object representing the requested pixel
     */
    public JavaPixel getPixel(int x, int y)
    {
        return new JavaPixel(this,x,y);
    }
}
        
        
        

