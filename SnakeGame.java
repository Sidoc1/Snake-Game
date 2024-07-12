import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.*;

import javax.management.monitor.GaugeMonitor;
import javax.swing.JFrame;
import java.awt.font.TextLayout;
import java.lang.Object;
import java.util.EventObject;
//import java.awt.EventListener;
import java.lang.Object;
import java.util.EventObject;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.Timer;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
public class SnakeGame extends JPanel  implements ActionListener{
	private final int width;
	private final int height;
	private final int cellSize;
	private final Random random= new Random();
	private Direction direction= Direction.RIGHT;
	private Direction newDirection= Direction.RIGHT;
	private static final int FRAME_RATE=20;
	private boolean gameStarted = false;
	private boolean gameOver = false;
	private  GamePoint food;
	private int highScore=0;
	private final List<GamePoint> snake = new ArrayList<>();

	public SnakeGame(final int width, final int height){
		super();
		 this.width=width;
		 this.height=height;
		 this.cellSize=width/(FRAME_RATE*2);
		 setPreferredSize( new Dimension(width,height));
		 setBackground(Color.BLACK);
	}
	
	
	public void startGame(){
		resetGameData();
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		requestFocusInWindow();

	addKeyListener(new KeyAdapter(){
	  @Override
       public void keyPressed( final KeyEvent e){
          HandleKeyEvent(e.getKeyCode());
		}

	
		});
		new Timer(1000/FRAME_RATE,this).start();
		//repaint();
	}
	private void HandleKeyEvent(final int keyCode) {
		if(!gameStarted){
		if( keyCode == KeyEvent.VK_SPACE){
			System.out.println("Space was Pressed");
			gameStarted=true;
		  }
		
		} else if( !gameOver){
			System.out.print("hello "+gameOver);
			switch (keyCode) {
				case KeyEvent.VK_UP:
				   if( direction != Direction.DOWN){
					newDirection= Direction.UP;
				   }
					break;
				case KeyEvent.VK_DOWN:
				if( direction != Direction.UP){
					newDirection= Direction.DOWN;
				}
					break;
				case KeyEvent.VK_RIGHT:
				  if( direction != Direction.LEFT){
					newDirection= Direction.RIGHT;
				  }
					break;
				case KeyEvent.VK_LEFT:
				if( direction != Direction.RIGHT){
					newDirection= Direction.LEFT;
				}
					break;
			
				default:
					break;
			}
		}else if( keyCode == KeyEvent.VK_SPACE){
			gameStarted =false;
			gameOver = false;
			resetGameData();
		}
	}

	private enum Direction{
		 UP, DOWN, RIGHT, LEFT
	}

	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		//System.out.println(gameStarted);
		if(!gameStarted){
		printMessage(g,"Press Space Bar to begin game");
	}else{
		g.setColor(Color.cyan);
		g.fill3DRect(food.x, food.y, cellSize, cellSize, gameOver);
		Color snackColor = Color.GREEN;
		g.setColor(Color.green);
		for(final var point: snake){
			g.setColor(snackColor);
			g.fillRect(point.x,point.y,cellSize,cellSize);
			final int newGreen = (int) Math.round(snackColor.getGreen() * (.90));
			snackColor =new Color(0,newGreen,0);
		}
		if(gameOver){
			final int currentScore = snake.size();
			if(currentScore > highScore){
				highScore=currentScore;
			}
			printMessage(g,"Your score "+currentScore+"\n High Score: "+ highScore+"\n Press Space Bar to Reset");
		}
	}


	
  }


	private void printMessage( final Graphics g,final String message) {
		g.setColor(Color.WHITE);
		//g.drawString("MY string",0,0);
		g.setFont(g.getFont().deriveFont(30f));
		int currentHeight = height /3;
		final var graphics2D = (Graphics2D) g;
		final var frc = graphics2D.getFontRenderContext();
        for( final var line : message.split("\n")){
			final var layout = new TextLayout(line, g.getFont(), frc);
			final var bounds = layout.getBounds();
			final var targetWidth = (float) (width - bounds.getWidth()) /2;
			layout.draw(graphics2D, targetWidth, currentHeight);
			currentHeight+= g.getFontMetrics().getHeight();
		}
	}
  private void resetGameData(){
    snake.clear();
	snake.add( new GamePoint(width/2,height/2));
	generateFood();
  }

  private void generateFood(){
      do{

		food= new GamePoint(random.nextInt(width/cellSize)*cellSize,random.nextInt(height/cellSize)*cellSize);
	  }while( snake.contains(food));
  }

  @Override
    public void actionPerformed(final ActionEvent e) {
        // Implement the action to be performed when an action event occurs
        // For example, update the game state and repaint the panel
		if( gameStarted && !gameOver){
		move();
		}
		repaint();
    }

	private void move(){
	  direction=newDirection;
      final GamePoint head= snake.get(snake.size()-1);
	  System.out.print(direction);
	  final GamePoint newHead =  switch(direction){
	 
    case UP -> new GamePoint(head.x,head.y-cellSize);
	case DOWN -> new GamePoint(head.x,head.y+cellSize);
	case RIGHT -> new GamePoint(head.x+cellSize,head.y);
    case LEFT -> new GamePoint(head.x-cellSize,head.y);
	  };
	  snake.add(newHead);
	 // System.out.print(checkCollision());
	 if( newHead.equals(food)){
		generateFood();
	}else if (checkCollision()){
		gameOver=true;
		snake.remove(snake.size()-1);
	  }
	  else{
		snake.remove(0);
	  }
	  direction = newDirection;
	}


	private boolean checkCollision(){
         final GamePoint head = snake.get(snake.size()-1);
		 final var invalidWidth=(head.x <0) || (head.x>= width);
		 final var invalidHeight=(head.y <0) || (head.y>= height);
		 if( invalidWidth || invalidHeight){
			return true;
		 }
		 return snake.size() != new HashSet<>(snake).size();
	}
    
  private record GamePoint(int x, int y) {

  }

}