import java.util.ArrayList;
import java.util.Random;

//2020-10-27
public class CollidingSpriteUniverse implements Universe {

	private boolean complete = false;
	private boolean teleport = false;
	private DisplayableSprite player1 = null;
	private ArrayList<DisplayableSprite> sprites = new ArrayList<DisplayableSprite>();	
	private String status = "";

	private final double VELOCITY = 300;	
	
	//	//require a separate list for sprites to be removed to avoid a concurence exception
	private ArrayList<DisplayableSprite> disposalList = new ArrayList<DisplayableSprite>();


	public CollidingSpriteUniverse () {

		double screenMinX = AnimationFrame.SCREEN_WIDTH / -2;
		double screenMinY = AnimationFrame.SCREEN_HEIGHT / -2;
		double screenMaxX = AnimationFrame.SCREEN_WIDTH / 2;
		double screenMaxY = AnimationFrame.SCREEN_HEIGHT / 2;
		
		//create random maze
		final int ROWS = 8;
		final int COLS = 8;
		final double BARRIER_FREQUENCY = 0.2;
		final double COIN_FREQUENCY = 0.4;
		final double COL_WIDTH = (AnimationFrame.SCREEN_WIDTH - 16) / (float)COLS; 
		final double ROW_HEIGHT = (AnimationFrame.SCREEN_HEIGHT - 16) / (float)ROWS; 

		Random random = new Random(1);
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				double minX = screenMinX + (col * COL_WIDTH);
				double minY = screenMinY + (row * ROW_HEIGHT);
				if (row == 0 && col == 0) {
					player1 = new SMSRSprite(minX + COL_WIDTH / 2 + 8, minY + ROW_HEIGHT / 2 + 8);
					sprites.add(player1);					
				}
				else if (row == ROWS - 1 && col == COLS -1) {
					sprites.add(new ExitSprite(minX - + COL_WIDTH / 2, minY - COL_WIDTH / 2, minX  + COL_WIDTH / 2, minX + ROW_HEIGHT / 2, true));
				}
				else if (row == (ROWS / 2) && col == (COLS / 2)) {
					sprites.add(new OtherSprite(minX + COL_WIDTH / 2 + 8, minY + ROW_HEIGHT / 2 + 8));
				}
				else {
					//place a coin?
					if (random.nextDouble() < COIN_FREQUENCY) {
						sprites.add(new CoinSprite(minX + COL_WIDTH / 2 + 8,minY + ROW_HEIGHT / 2 + 8));
					}
				}
				//place a barrier to right?
				if (random.nextDouble() < BARRIER_FREQUENCY) {
					sprites.add(new BarrierSprite(minX,minY, minX + COL_WIDTH + 16 , minY + 16, true));
				}
				//place a barrier down?
				if (random.nextDouble() < BARRIER_FREQUENCY) {
					sprites.add(new BarrierSprite(minX,minY, minX + 16 , minY + ROW_HEIGHT + 16, true));
				}
				
			}
		}
		
		//top
		sprites.add(new BarrierSprite(screenMinX,screenMinY, screenMaxX , screenMinY + 16, true));
		//bottom
		sprites.add(new BarrierSprite(screenMinX,screenMaxY - 16, screenMaxX , screenMaxY, true));
		//left
		sprites.add(new BarrierSprite(screenMinX,screenMinY, screenMinX + 16 , screenMaxY, true));
		//right
		sprites.add(new BarrierSprite(screenMaxX - 16,screenMinY, screenMaxX , screenMaxY, true));		
	}

	public double getScale() {
		return 1;
	}

	public double getXCenter() {
		return 0;
	}

	public double getYCenter() {
		return 0;
	}

	public void setXCenter(double xCenter) {
	}

	public void setYCenter(double yCenter) {
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public ArrayList<Background> getBackgrounds() {
		return null;
	}

	public DisplayableSprite getPlayer1() {
		return player1;
	}

	public ArrayList<DisplayableSprite> getSprites() {
		return sprites;
	}

	public boolean centerOnPlayer() {
		return false;
	}		

	public void update(KeyboardInput keyboard, long actual_delta_time) {

		double velocityX = 0;
		double velocityY = 0;
		
		//LEFT	
		if (keyboard.keyDown(37)) {
			velocityX = -VELOCITY;
		}
		//UP
		if (keyboard.keyDown(38)) {
			velocityY = -VELOCITY;			
		}
		// RIGHT
		if (keyboard.keyDown(39)) {
			velocityX += VELOCITY;
		}
		// DOWN
		if (keyboard.keyDown(40)) {
			velocityY += VELOCITY;			
		}
		
		for (int i = 0; i < sprites.size(); i++) {
			DisplayableSprite sprite = sprites.get(i);
			
			if (sprite instanceof MovableSprite) {
				MovableSprite movable = (MovableSprite)sprite;
				movable.setVelocityX(velocityX);
				movable.setVelocityY(velocityY);
			}
			
			sprite.update(this, null, actual_delta_time);
    	}
		
		disposeSprites();
		
		if (player1 instanceof CollidingSprite) {
			if ( ((CollidingSprite)player1).getIsAtExit() ) {
				this.complete = true;
			}
		}

	}

	public String toString() {
		return this.status;
	}	
	
    protected void disposeSprites() {
        
    	//collect a list of sprites to dispose
    	//this is done in a temporary list to avoid a concurrent modification exception
		for (int i = 0; i < sprites.size(); i++) {
			DisplayableSprite sprite = sprites.get(i);
    		if (sprite.getDispose() == true) {
    			disposalList.add(sprite);
    		}
    	}
		
		//go through the list of sprites to dispose
		//note that the sprites are being removed from the original list
		for (int i = 0; i < disposalList.size(); i++) {
			DisplayableSprite sprite = disposalList.get(i);
			sprites.remove(sprite);
			System.out.println("Remove: " + sprite.toString());
    	}
		
		//clear disposal list if necessary
    	if (disposalList.size() > 0) {
    		disposalList.clear();
    	}
    }

}
