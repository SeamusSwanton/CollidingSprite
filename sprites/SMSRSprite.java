import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;


public class SMSRSprite implements DisplayableSprite, MovableSprite, CollidingSprite {
	
	private static final double VELOCITY = 200;
	private static final int WIDTH = 50;
	private static final int HEIGHT = 50;
	private static final int PERIOD_LENGTH = 200;
	private static final int IMAGES_IN_CYCLE = 1;
	
	private static Image[] images;	
	private double centerX = 400;
	private double centerY = 300;
	private double width = 50;
	private double height = 50;
	private boolean dispose = false;	
	private double velocityX = 0;
	private double velocityY = 0;
	private int elapsedTime = 0;
	private boolean isAtExit = false;

	
	public int score = 0;
	boolean isCloseToSprite = false;
	private String proximityMessage = "not close";
	
	private Direction direction = Direction.NEUTRAL;
	
	private enum Direction { NEUTRAL(0), DOWN(1), LEFT(2), UP(3), RIGHT(4);
		private int value = 0;
		private Direction(int value) {
			this.value = value; 
		} 
	}

	
	public SMSRSprite(double centerX, double centerY) {

		this.centerX = centerX;
		this.centerY = centerY;
		
		if (images == null) {
			try {
				images = new Image[5];
				for (int i = 0; i < 5; i++) {
					String path = String.format("res/SMSR/SMSRsprite-%d.png", i);
					images[i] = ImageIO.read(new File(path));
				}
			}
			catch (IOException e) {
				System.err.println(e.toString());

			}		
		}		
	}

	//overloaded constructor which allows universe to change aspects of the sprite
	public SMSRSprite(double centerX, double centerY, double height, double width) {
		this(centerX, centerY);
		
		this.height = height;
		this.width = width;
	}

	public Image getImage() {
		
		int index = direction.value;
						
		return SMSRSprite.images[index];
				
	}
	
	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
		
	}

	public void setVelocityY(double pixelsPerSecond) {
		this.velocityY = pixelsPerSecond;	
	}

	public void setVelocityX(double pixelsPerSecond) {
		this.velocityX = pixelsPerSecond;
	}


	
	//DISPLAYABLE
	
	public boolean getVisible() {
		return true;
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
	}

	public double getCenterY() {
		return centerY;
	}
	
	
	public boolean getDispose() {
		return dispose;
	}
	
	public long getScore() {
		return score;
	}

	public void setDispose(boolean dispose) {
		this.dispose = dispose;
	}


	public void update(Universe universe, KeyboardInput keyboard, long actual_delta_time) {
		
		elapsedTime += actual_delta_time;
		
		
		if (velocityY < 0) {
			direction = Direction.UP;
//			System.out.println("UP");
		}
		
		else if (velocityY > 0) {
			direction = Direction.DOWN;
//			System.out.println("DOWN");
		}
		
		else if (velocityX < 0) {
			direction = Direction.LEFT;
//			System.out.println("LEFT");
		}
		
		else if (velocityX > 0) {
			direction = Direction.RIGHT;
//			System.out.println("RIGHT");
		}
		
		else if (velocityX == 0 && velocityY == 0) {
			direction = Direction.NEUTRAL;
//			System.out.println("NEUTRAL");
		}
		
		//calculate potential change in position based on velocity and time elapsed		
		double deltaX = actual_delta_time * 0.001 * velocityX;
		double deltaY = actual_delta_time * 0.001 * velocityY;
		
		//before changing position, check if the new position would result in a collision with another sprite
		//move only if no collision results		
		
		if ( checkIsAtExit( universe) == false) {
		}
		
		if ( getAcquiredCoin( universe) == false) {
		}
		
		if ( checkCollisionWithBarrier( universe, deltaX, 0) == false) {
			this.centerX += deltaX;
		}
		//keep separated
		if ( checkCollisionWithBarrier( universe, 0, deltaY) == false) {
			this.centerY += deltaY;

		}
	}

	private boolean checkCollisionWithBarrier(Universe sprites, double deltaX, double deltaY) {

		//deltaX and deltaY represent the potential change in position
		boolean colliding = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite instanceof BarrierSprite) {
				if (CollisionDetection.overlaps(this.getMinX() + deltaX, this.getMinY() + deltaY, 
						this.getMaxX()  + deltaX, this.getMaxY() + deltaY, 
						sprite.getMinX(),sprite.getMinY(), 
						sprite.getMaxX(), sprite.getMaxY())) {
					colliding = true;
					break;					
				}
			}
		}		
		return colliding;		
	}

	public boolean getAcquiredCoin(Universe sprites) {
		
		boolean colliding = false;

		for (DisplayableSprite sprite : sprites.getSprites()) {
			if (sprite instanceof CoinSprite) {
				if (CollisionDetection.pixelBasedOverlaps(this, sprite)) {
					
						//coin pickup
					((CoinSprite)sprite).setDispose(true);
					this.score +=100;
					colliding = true;
					break;
				}
			}			
		}
		return colliding;
	}
	
	public boolean checkIsAtExit(Universe universe) {
		
		for (DisplayableSprite sprite : universe.getSprites()) {
			
			if (CollisionDetection.pixelBasedOverlaps(this, sprite)) {
				
				if (sprite instanceof ExitSprite) {
					this.isAtExit = true;
				}
				
			}			
		
		}
		return isAtExit;	
	}


	public boolean checkProximityMessage(Universe sprites) {

		
		for (DisplayableSprite sprite : sprites.getSprites()) {
			
			if (!(sprite instanceof CoinSprite || sprite instanceof BarrierSprite
				|| sprite instanceof SMSRSprite || sprite instanceof ExitSprite)) {
				
				double x = sprite.getCenterX() - this.getCenterX();
				double y = sprite.getCenterY() - this.getCenterY();
				double proximityToSprite = Math.sqrt(x*x + y*y);
				
				if (proximityToSprite <= 100) {
					isCloseToSprite = true;
				}
				else {
					isCloseToSprite = false;
				}
			}
		}
		return isCloseToSprite;
	}
	
	
	public String getProximityMessage() {
		
		if (isCloseToSprite == true) {
			return  "close";
		}
		else {
			return "not close";
		}	
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}


	public boolean getIsAtExit() {
		return isAtExit;
	}


	

}





