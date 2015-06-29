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
    static int count = 5;
    public static void keyPressed(char key){
        if(key == 'w')
            keyQueue.add(Directions.UP);
        if(key == 'a')
            keyQueue.add(Directions.LEFT);
        if(key == 'd')
            keyQueue.add(Directions.RIGHT);
        if(key == 's')
            keyQueue.add(Directions.DOWN);
    }
    public static void updates(){
        if(keyQueue.size() > 0){
            currentDir = keyQueue.get(0);
            keyQueue.remove(0);
        }
        SnakeSegment oldFirstSegment = segments.get(0);
        segments.add(0, new SnakeSegment(oldFirstSegment.x + currentDir.xChange, oldFirstSegment.y + currentDir.yChange));
        segments.remove(segments.size() - 1);
        segments.get(0).checkKillCollisions();
        for(int i = 0; i < segments.size(); i++){
            SnakeSegment ss = segments.get(0);
            ss.checkTargetCollision();
        }
    }
    public static void main(String[] args) throws Exception{
        window = new Window();
        for(int i = 0; i < count; i++){
            segments.add(new SnakeSegment(30, 30));
        }
        Timer t = new Timer();
        t.schedule(new Loop(), 0, 100);
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
    public void draw(Graphics2D g){
        g.setColor(Color.GREEN);
        g.fillRect(x * 10, y * 10, width, height);
    }
    public void checkKillCollisions(){
        boolean lose = false;
        for(int i = 1; i < Snake.segments.size(); i++){
            if(Snake.segments.get(i).x == x && Snake.segments.get(i).y == y)
                lose = true;
        }
        if(x >= 60 || x < 0 || y >= 60 || y < 0)
            lose = true;
        if(lose)
            Snake.window.popUp("You Lose");
    }
    public void checkTargetCollision(){
        if(x == Snake.target.x && y == Snake.target.y){
            Snake.segments.add(new SnakeSegment(Snake.segments.get(Snake.segments.size() - 1).x - Snake.currentDir.xChange, Snake.segments.get(Snake.segments.size() - 1).y - Snake.currentDir.yChange));
            Snake.target = new Target();
        }
    }
}
class Target {
    int x, y;
    public Target(){
        x = (int) Math.floor(Math.random() * 60);
        y = (int) Math.floor(Math.random() * 60);
    }
    public void draw(Graphics2D g){
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
        Snake.segments.stream().forEach((ss) -> {
            ss.draw((Graphics2D) G);
        });
        Snake.target.draw((Graphics2D) G);
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
