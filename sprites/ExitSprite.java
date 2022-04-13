import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ExitSprite implements DisplayableSprite {

	private static Image image;
	private boolean visible = true;
	private double centerX = 0;
	private double centerY = 0;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	
	private double currentAngle = 90;
	private double ROTATION_SPEED = -180;	//degrees per second
	private final static int FRAMES = 360;
	private static Image[] rotatedImages = new Image[FRAMES];
	private static boolean framesLoaded = false;
	
	public ExitSprite(double minX, double minY, double maxX, double maxY, boolean visible) {
		
		if (framesLoaded == false) {
			try {
				image = ImageIO.read(new File("res/spiral-small.png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int i = 0; i < FRAMES; i++) {
				rotatedImages[i] = ImageRotator.rotate(image, i);
			}
			
		}
		
		this.centerX = (minX + maxX) / 2;
		this.centerY = (minY + maxY) / 2;
		this.width = maxX - minX;
		this.height = maxY - minY;
		this.visible = visible;
		
	}
	

	public Image getImage() {
		return rotatedImages[(int)currentAngle];
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	//DISPLAYABLE
	
	public boolean getVisible() {
		return this.visible;
	}
	
	public double getMinX() {
		return centerX - (width / 2);
	}

	public double getMaxX() {
		return centerX + (width / 2);
	}

	public double getMinY() {
		return centerY - (height / 2);
	}

	public double getMaxY() {
		return centerY + (height / 2);
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public double getCenterX() {
		return centerX;
	};

	public double getCenterY() {
		return centerY;
	};
	
	
	public boolean getDispose() {
		return dispose;
	}

	public void setDispose(boolean dispose) {
		this.dispose = dispose;
	}


	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		currentAngle -= (ROTATION_SPEED * (actual_delta_time * 0.001));
	    if (currentAngle >= 360) {
	    	currentAngle -= 360;
	    }
	    if (currentAngle < 0) {
	    	currentAngle += 360;
	    }
	    
	    int frame = (int)currentAngle;
	    if (rotatedImages[frame] != null) {
		    this.height = rotatedImages[frame].getHeight(null);
		    this.width = rotatedImages[frame].getWidth(null);
	    }
			
	}

}
