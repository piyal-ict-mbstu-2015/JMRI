package jmri.jmrit.catalog;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.PixelGrabber;
import javax.swing.JLabel;
import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017
 * @author Joe Comuzzi Copyright (C) 2018	
 */
public class NamedIconTest {

    @Test
    public void testCTor() {
        NamedIcon t = new NamedIcon("program:resources/logo.gif","logo");
        Assert.assertNotNull("exists",t);
    }

    // Test that resize Affine Transform doesn't affects size. The size comes from the width and
    // Height parameters
    @Test
    public void testTransformImage() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        AffineTransform t = AffineTransform.getScaleInstance(0.5, 0.5);
        
        ni.transformImage(w/2,  h,  t,  null);
        Assert.assertEquals(w/2, ni.getIconWidth());
        Assert.assertEquals(h, ni.getIconHeight());
    }
    
    // Test rotate method with no scaling, 45 deg.
    @Test
    public void testRotateNoScaling() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        
        ni.rotate(45, new JLabel());
        int rectSize = (int) Math.ceil((h + w)/Math.sqrt(2.));
        Assert.assertEquals(rectSize, ni.getIconHeight());
        Assert.assertEquals(rectSize, ni.getIconWidth());
    }
    
    // Test scaling, no rotation
    @Test
    public void testScaling() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        double scale = 1.2;
        
        ni.scale(scale, new JLabel());
        Assert.assertEquals((int) (h * scale), ni.getIconHeight());
        Assert.assertEquals((int) (w * scale), ni.getIconWidth());        
    }
    
    // Test rotate method with scaling, 270
    // should just swap height and width and we'll scale by 2.0
    @Test
    public void testRotate270() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        JLabel comp = new JLabel();
        double scale = 2.0;
        
        ni.scale(scale, comp);
        ni.rotate(270, comp);
        // The +1 in the below is a bit of a crock, but it's because the argument of
        // Math.ceil(Math.abs(w*Math.cos(rad))) is slightly more than zero, so it
        // rounds up!
        Assert.assertEquals((int) Math.ceil(w * scale) + 1, ni.getIconHeight());
        Assert.assertEquals((int) Math.ceil(h * scale) + 1, ni.getIconWidth());
    } 
    
    // Test setLoad and scale. 30 degrees is also simple geometry.
    // Also test getDegrees and getScale while we're here
    @Test
    public void testSetLoad()
    {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        JLabel comp = new JLabel();
        double scale = 0.333;
        int degrees = 30;
        
        ni.setLoad(degrees, scale, comp);  
        Assert.assertEquals(ni.getDegrees(), degrees);
        Assert.assertEquals(ni.getScale(), scale, .001);
        
        // Be wary of numerical instability in these tests. e.g. because of rounding in NamedIcon, sometimes
        // cos(30) is not exactly 0.5 and the ceil operation gives different answers!
        double sqrt3 = Math.sqrt(3);
        int expectedHeight = (int) (Math.ceil(h * sqrt3 * scale / 2.0) + Math.ceil(w * scale / 2.0));
        Assert.assertEquals(expectedHeight, ni.getIconHeight());
        int expectedWidth = (int) (Math.ceil(h * scale / 2.0) + Math.ceil(w * sqrt3 * scale / 2.0));
        Assert.assertEquals(expectedWidth, ni.getIconWidth());
    }
    
    // Test flip method
    @Test
    public void testFlip() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        JLabel comp = new JLabel();
        
        ni.flip(NamedIcon.NOFLIP, comp);       
        Assert.assertEquals(w, ni.getIconWidth());
        Assert.assertEquals(h, ni.getIconHeight());
        int [] noflipPixels = getPixels(ni);
        
        ni.flip(NamedIcon.VERTICALFLIP, comp);
        Assert.assertEquals(w, ni.getIconWidth());
        Assert.assertEquals(h, ni.getIconHeight());
        
        int [] flipPixels = getPixels(ni); 
        for (int j = 0; j < h; j++) {
            for (int i= 0; i < w; i++) {
                Assert.assertEquals(getPixel(noflipPixels, i, j, w), getPixel(flipPixels, i, h - 1 - j, w));
            }
        }
    }
    
    // Test createRotatedImage
    @Test
    public void testCreateRotatedImage() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        JLabel comp = new JLabel();
        
        Image rotImage = ni.createRotatedImage(ni.getImage(), comp, 1);
        Assert.assertEquals(h, rotImage.getWidth(null));
        Assert.assertEquals(w, rotImage.getHeight(null));   
        // should check image bits here
    }
    
    // Test setRotation and getRotation
    // N.B. unlike the other mutator methods on NamedIcon
    // setRotation does NOT reset the image each time it's
    // called. We use that behavior in the test.
    @Test
    public void testSetRotation() {
        NamedIcon ni = new NamedIcon("program:resources/logo.gif","logo");
        int h = ni.getIconHeight();
        int w = ni.getIconWidth();
        JLabel comp = new JLabel();
        
        Assert.assertEquals(0, ni.getRotation());
     
        ni.setRotation(3, comp);
        Assert.assertEquals(h, ni.getIconWidth());
        Assert.assertEquals(w, ni.getIconHeight());
        
        int [] rot3Pixels = getPixels(ni);
        
        ni.setRotation(2, comp);
        ni.setRotation(2, comp);
        
        int [] rot7Pixels = getPixels(ni);
         
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
//System.out.println("i = " + i + ", j = " + j);
//                Assert.assertEquals(getPixel(rot3Pixels, i, j, h), getPixel(rot7Pixels, i, j, h));
            }
        }
        
        Assert.assertEquals(2, ni.getRotation());
    }

    // Helper routine to grab the pixels from an Image
    private int [] getPixels(NamedIcon ni) {
        Image img = ni.getImage();
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int[] pixels = new int[w * h];
        PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
        try {
            pg.grabPixels();
        } catch (InterruptedException ie) {
        }
        return pixels;
    }
    
    // Helper routine to grab the i,j pixel 
    private int getPixel(int [] pixels, int i, int j, int width) {
//System.out.println("i = " + i + ", j = " + j + ", width = " + width + ", cood = " + (j * width + i));
        return pixels[j * width + i];
    }
    
    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(NamedIconTest.class);

}
