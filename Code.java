package snake;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.Timer;
public class Snake {
    static ArrayList<SnakeSegment> segments = new ArrayList();
    static ArrayList<Directions> keyQueue = new ArrayList();
    static Window window;
    static Target target = new Target();
    static Directions currentDir = Directions.LEFT;
    static int size = 5;
    public static void gameOver(){
        window.popUp("You lose with a score of " + (segments.size() - 5) / 3);
        segments.clear();
        currentDir = Directions.LEFT;
        for(int i = 0; i < 5; i++)
            segments.add(new SnakeSegment(30, 30));
    }
    public static void keyPressed(char key){
        if(key == 'w'){
            if(keyQueue.size() > 0){
                if(keyQueue.get(keyQueue.size() - 1) != Directions.DOWN)
                    keyQueue.add(Directions.UP);
            }
            else if(currentDir != Directions.DOWN)
                keyQueue.add(Directions.UP);
        }
        if(key == 'a'){
            if(keyQueue.size() > 0){
                if(keyQueue.get(keyQueue.size() - 1) != Directions.RIGHT)
                    keyQueue.add(Directions.LEFT);
            }
            else if(currentDir != Directions.RIGHT)
                keyQueue.add(Directions.LEFT);
        }
        if(key == 'd'){
            if(keyQueue.size() > 0){
                if(keyQueue.get(keyQueue.size() - 1) != Directions.LEFT)
                    keyQueue.add(Directions.RIGHT);
            }
            else if(currentDir != Directions.LEFT)
                keyQueue.add(Directions.RIGHT);
        }
        if(key == 's'){
            if(keyQueue.size() > 0){
                if(keyQueue.get(keyQueue.size() - 1) != Directions.UP)
                    keyQueue.add(Directions.DOWN);
            }
            else if(currentDir != Directions.UP)
                keyQueue.add(Directions.DOWN);
        }
    }
    public static void updates(){
        if(keyQueue.size() > 0){
            currentDir = keyQueue.get(0);
            keyQueue.remove(0);
        }
        SnakeSegment oldFirstSegment = segments.get(0);
        segments.add(0, new SnakeSegment(oldFirstSegment.x + currentDir.xChange, oldFirstSegment.y + currentDir.yChange));
        int indexToRemove = segments.size() - 1;
        for(; indexToRemove > size - 1; indexToRemove = segments.size() - 1)
            segments.remove(indexToRemove);
        segments.get(0).checkKillCollisions();
        for(int i = 0; i < segments.size(); i++){
            SnakeSegment ss = segments.get(0);
            ss.checkTargetCollision();
        }
    }
    public static void main(String[] args){
        window = new Window();
        for(int i = 0; i < 5; i++)
            segments.add(new SnakeSegment(30, 30));
        Timer t = new Timer();
        t.schedule(new Loop(), 0, 50);
    }
}
class Loop extends TimerTask {
    @Override public void run(){
        Snake.updates();
        Snake.window.render();
    }
}
class SnakeSegment {
    final int height = 10;
    final int width = 10;
    int x, y;
    public SnakeSegment(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void draw(Graphics g){
        g.setColor(Color.GREEN);
        g.fillRect(x * 10, y * 10, width, height);
    }
    public void checkKillCollisions(){
        boolean lose = false;
        for(int i = 1; i < Snake.segments.size(); i++){
            if(Snake.segments.get(i).x == x && Snake.segments.get(i).y == y)
                lose = true;
        }
        if(x >= 60)
            x -= 60;
        if(x < 0)
            x += 60;
        if(y < 0)
            y += 60;
        if(y >= 60)
            y -= 60;
        if(lose)
            Snake.gameOver();
    }
    public void checkTargetCollision(){
        if(x == Snake.target.x && y == Snake.target.y){
            Snake.target = new Target();
            Snake.size += 3;
        }
    }
}
class Target {
    int x, y;
    public Target(){
        boolean valid = false;
        while(!valid){
            boolean inSnake = false;
            x = (int) Math.floor(Math.random() * 60);
            y = (int) Math.floor(Math.random() * 60);
            for(SnakeSegment ss : Snake.segments)
                if(ss.x == x && ss.y == y)
                    inSnake = true;
            valid = !inSnake;
        }
    }
    public void draw(Graphics g){
        g.setColor(Color.red);
        g.fillRect(x * 10, y * 10, 10, 10);
    }
}
class WindowContent extends JPanel {
    public WindowContent(){
        super();
    }
    @Override public void paint(Graphics G){
        super.paint(G);
        Graphics2D g = (Graphics2D) G;
        G.setColor(Color.white);
        G.fillRect(0, 0, 600, 600);
        Snake.segments.stream().forEach((ss) -> {
            ss.draw(G);
        });
        Snake.target.draw(G);
        revalidate();
    }
    @Override public void setSize(int x, int y){
        setPreferredSize(new Dimension(x,y));
    }
}
class Window extends JFrame {
    int sizeX = 600;
    int sizeY = 600;
    WindowContent inside = new WindowContent();
    public Window(){
        super("Snake");
        inside.setSize(sizeX, sizeY);
        add(inside);
        pack();
        setDefaultCloseOperation(3);
        setVisible(true);
        setFocusable(true);
        addKeyListener(new KeyListener(){
            @Override public void keyPressed(KeyEvent e){
                Snake.keyPressed(e.getKeyChar());
            }
            @Override public void keyReleased(KeyEvent e){}
            @Override public void keyTyped(KeyEvent e){}
        });
    }
    public void render(){
        inside.repaint();
        inside.revalidate();
        inside.updateUI();
    }
    public void popUp(String str){
        JOptionPane.showMessageDialog(this, str);
    }
}
enum Directions {
    UP    (0, -1),
    LEFT  (-1, 0),
    RIGHT (1, 0),
    DOWN  (0, 1);
    public final int xChange, yChange;
    Directions(int xChange, int yChange){
        this.xChange = xChange;
        this.yChange = yChange;
    }
}
