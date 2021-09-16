import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Game {
    public static void main(String[] ar){
        game_Frame fms = new game_Frame();
    }
}

class game_Frame extends JFrame implements KeyListener, Runnable{

    int f_width ;
    int f_height ;

    int x, y;

    int[] cx ={0, 0, 0};
    int bx = 0;

    boolean KeyUp = false;
    boolean KeyDown = false;
    boolean KeyLeft = false;
    boolean KeyRight = false;
    boolean KeySpace = false;

    int cnt;

    int player_Speed;
    int missile_Speed;
    int fire_Speed;
    int enemy_speed;
    int player_Status = 0;

    int game_Score;
    int player_Hitpoint;



    Thread th;

    Toolkit tk = Toolkit.getDefaultToolkit();

    Image[] Player_img;

    Image BackGround_img;
    Image[] Cloud_img;
    Image[] Explo_img;

    Image Missile_img;
    Image Enemy_img;

    ArrayList Missile_List = new ArrayList();
    ArrayList Enemy_List = new ArrayList();
    ArrayList Explosion_List = new ArrayList();


    Image buffImage;
    Graphics buffg;

    Missile ms;
    Enemy en;

    Explosion ex;

    game_Frame(){
        init();
        start();

        setTitle("슈팅 게임");
        setSize(f_width, f_height);

        Dimension screen = tk.getScreenSize();

        int f_xpos = (int)(screen.getWidth() / 2 - f_width / 2);
        int f_ypos = (int)(screen.getHeight() / 2 - f_height / 2);

        setLocation(f_xpos, f_ypos);
        setResizable(false);
        setVisible(true);
    }
    public void init(){
        x = 100;
        y = 100;
        f_width = 1200;
        f_height = 600;

        Missile_img = new ImageIcon("Missile.png").getImage();
        Enemy_img = new ImageIcon("enemy.png").getImage();


        Player_img = new Image[5];
        for(int i = 0 ; i < Player_img.length ; ++i){
            Player_img[i] =
                    new ImageIcon("f15k_" + i + ".png").getImage();
        }


        BackGround_img = new ImageIcon("background.png").getImage();

        Cloud_img = new Image[3];
        for(int i = 0 ; i <Cloud_img.length ; ++i){
            Cloud_img[i] =
                    new ImageIcon("cloud_" + i + ".png").getImage();
        }

        Explo_img = new Image[3];
        for (int i = 0; i < Explo_img.length ; ++i ){
            Explo_img[i] =
                    new ImageIcon("explo_" + i + ".png").getImage();
        }

        game_Score = 0;
        player_Hitpoint = 3;

        player_Speed = 5;
        missile_Speed = 11;
        fire_Speed = 15;
        enemy_speed = 7;

    }
    public void start(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);

        th = new Thread(this);
        th.start();

    }

    public void run(){
        try{
            while(true){
                KeyProcess();
                EnemyProcess();
                MissileProcess();

                ExplosionProcess();

                repaint();

                Thread.sleep(20);
                cnt ++;
            }
        }catch (Exception e){}
    }

    public void MissileProcess(){
        if ( KeySpace ){
            player_Status = 1;


            if( ( cnt % fire_Speed ) == 0){

                ms = new Missile(x+150, y+30, missile_Speed);

                Missile_List.add(ms);
            }
        }

        for ( int i = 0 ; i < Missile_List.size() ; ++i){
            ms = (Missile) Missile_List.get(i);
            ms.move();
            if ( ms.x > f_width - 20 ){
                Missile_List.remove(i);
            }

            for (int j = 0 ; j < Enemy_List.size(); ++ j){
                en = (Enemy) Enemy_List.get(j);

                if (Crash(ms.x, ms.y, en.x, en.y, Missile_img, Enemy_img)) {

                    Missile_List.remove(i);
                    Enemy_List.remove(j);

                    game_Score += 10;

                    ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2 , 0);

                    Explosion_List.add(ex);

                }
            }
        }
    }

    public void EnemyProcess(){

        for (int i = 0 ; i < Enemy_List.size() ; ++i ){
            en = (Enemy)(Enemy_List.get(i));
            en.move();
            if(en.x < -200){
                Enemy_List.remove(i);
            }

            if(Crash(x, y, en.x, en.y, Player_img[0], Enemy_img)){
    player_Hitpoint --;
                Enemy_List.remove(i);
                game_Score += 10;

                ex = new Explosion(en.x + Enemy_img.getWidth(null) / 2, en.y + Enemy_img.getHeight(null) / 2, 0 );

                Explosion_List.add(ex);

                ex = new Explosion(x, y, 1 );

                Explosion_List.add(ex);


            }
        }
        if ( cnt % 200 == 0 ){
            en = new Enemy(f_width + 100, 100, enemy_speed);
            Enemy_List.add(en);
            en = new Enemy(f_width + 100, 200, enemy_speed);
            Enemy_List.add(en);
            en = new Enemy(f_width + 100, 300, enemy_speed);
            Enemy_List.add(en);
            en = new Enemy(f_width + 100, 400, enemy_speed);
            Enemy_List.add(en);
            en = new Enemy(f_width + 100, 500, enemy_speed);
            Enemy_List.add(en);

        }
    }

    public void ExplosionProcess(){
        // 폭발 이펙트 처리용 메소드

        for (int i = 0 ;  i < Explosion_List.size(); ++i){
            ex = (Explosion) Explosion_List.get(i);
            ex.effect();


        }
    }


    public boolean Crash(int x1, int y1, int x2, int y2, Image img1, Image img2){


        boolean check = false;

        if ( Math.abs( ( x1 + img1.getWidth(null) / 2 )
                - ( x2 + img2.getWidth(null) / 2 ))
                < ( img2.getWidth(null) / 2 + img1.getWidth(null) / 2 )
                && Math.abs( ( y1 + img1.getHeight(null) / 2 )
                - ( y2 + img2.getHeight(null) / 2 ))
                < ( img2.getHeight(null)/2 + img1.getHeight(null)/2 ) ){


            check = true;
        }else{ check = false;}

        return check;

    }


    public void paint(Graphics g){
        buffImage = createImage(f_width, f_height);
        buffg = buffImage.getGraphics();

        update(g);
    }

    public void update(Graphics g){

        Draw_Background();
        Draw_Player();

        Draw_Enemy();
        Draw_Missile();

        Draw_Explosion();
        Draw_StatusText();

        g.drawImage(buffImage, 0, 0, this);
    }

    public void Draw_Background(){


        buffg.clearRect(0, 0, f_width, f_height);


        if ( bx > - 3500){


            buffg.drawImage(BackGround_img, bx, 0, this);
            bx -= 1;


        }else { bx = 0; }


        for (int i = 0;  i < cx.length ; ++i){

            if ( cx[i] < 1400){
                cx[i] += 5 + i * 3 ;
            } else { cx[i] = 0; }

            buffg.drawImage(Cloud_img[i], 1200 - cx[i], 50+i*200,  this);

        }
    }

    public void Draw_Player(){

        switch (player_Status){

            case 0 : // 평상시
                if((cnt / 5 %2) == 0){
                    buffg.drawImage(Player_img[1], x, y, this);

                }else { buffg.drawImage(Player_img[2], x, y, this); }

                break;

            case 1 : // 미사일발사
                if((cnt / 5 % 2) == 0){
                    buffg.drawImage(Player_img[3], x, y, this);
                }else { buffg.drawImage(Player_img[4], x, y, this); }

                player_Status = 0;


                break;

            case 2 :
                break;

        }

    }

    public void Draw_Missile(){
        for (int i = 0 ; i < Missile_List.size()  ; ++i){
            ms = (Missile) (Missile_List.get(i));
            buffg.drawImage(Missile_img, ms.x, ms.y, this);
        }
    }

    public void Draw_Enemy(){
        for (int i = 0 ; i < Enemy_List.size() ; ++i ){
            en = (Enemy)(Enemy_List.get(i));
            buffg.drawImage(Enemy_img, en.x, en.y, this);
        }
    }

    public void Draw_Explosion(){


        for (int i = 0 ; i < Explosion_List.size() ; ++i ){
            ex = (Explosion)Explosion_List.get(i);


            if (ex.damage == 0){

                if ( ex.ex_cnt < 7  ) {
                    buffg.drawImage( Explo_img[0], ex.x -
                            Explo_img[0].getWidth(null) / 2, ex.y -
                            Explo_img[0].getHeight(null) / 2, this);
                }else if ( ex.ex_cnt < 14 ) {
                    buffg.drawImage(Explo_img[1], ex.x -
                            Explo_img[1].getWidth(null) / 2, ex.y -
                            Explo_img[1].getHeight(null) / 2, this);
                }else if ( ex.ex_cnt < 21 ) {
                    buffg.drawImage(Explo_img[2], ex.x -
                            Explo_img[2].getWidth(null) / 2, ex.y -
                            Explo_img[2].getHeight(null) / 2, this);
                }else if( ex.ex_cnt > 21 ) {
                    Explosion_List.remove(i);
                    ex.ex_cnt = 0;

                }
            }else {
                if ( ex.ex_cnt < 7  ) {
                    buffg.drawImage(Explo_img[0], ex.x + 120,
                            ex.y + 15, this);
                }else if ( ex.ex_cnt < 14 ) {
                    buffg.drawImage(Explo_img[1], ex.x + 60,
                            ex.y + 5, this);
                }else if ( ex.ex_cnt < 21 ) {
                    buffg.drawImage(Explo_img[0], ex.x + 5,
                            ex.y + 10, this);
                }else if( ex.ex_cnt > 21 ) {
                    Explosion_List.remove(i);
                    ex.ex_cnt = 0;

                }
            }
        }
    }

    public void Draw_StatusText(){

        buffg.setFont(new Font("Defualt", Font.BOLD, 20));


        buffg.drawString("SCORE : " + game_Score, 1000, 70);


        buffg.drawString("HitPoint : " + player_Hitpoint, 1000, 90);


        buffg.drawString("Missile Count : " + Missile_List.size(), 1000, 110);


        buffg.drawString("Enemy Count : " + Enemy_List.size(), 1000, 130);

    }
    public void KeyProcess(){
        if(KeyUp == true) {
            if( y > 20 ) y -= 5;


            player_Status = 0;

        }

        if(KeyDown == true) {
            if( y+ Player_img[0].getHeight(null) < f_height ) y += 5;


            player_Status = 0;

        }

        if(KeyLeft == true) {
            if ( x > 0 ) x -= 5;

            player_Status = 0;

        }

        if(KeyRight == true) {
            if ( x + Player_img[0].getWidth(null) < f_width ) x += 5;


            player_Status = 0;

        }
    }

    public void keyPressed(KeyEvent e){

        switch(e.getKeyCode()){
            case KeyEvent.VK_UP :
                KeyUp = true;
                break;
            case KeyEvent.VK_DOWN :
                KeyDown = true;
                break;
            case KeyEvent.VK_LEFT :
                KeyLeft = true;
                break;
            case KeyEvent.VK_RIGHT :
                KeyRight = true;
                break;

            case KeyEvent.VK_SPACE :
                KeySpace = true;
                break;
        }
    }
    public void keyReleased(KeyEvent e){

        switch(e.getKeyCode()){
            case KeyEvent.VK_UP :
                KeyUp = false;
                break;
            case KeyEvent.VK_DOWN :
                KeyDown = false;
                break;
            case KeyEvent.VK_LEFT :
                KeyLeft = false;
                break;
            case KeyEvent.VK_RIGHT :
                KeyRight = false;
                break;

            case KeyEvent.VK_SPACE :
                KeySpace = false;
                break;

        }
    }
    public void keyTyped(KeyEvent e){}

}

class Missile{
    int x;
    int y;

    int speed;

    Missile(int x, int y, int speed) {
        this.x = x;
        this.y = y;

        this.speed = speed;


    }
    public void move(){
        x += speed;
    }
}

class Enemy{
    int x;
    int y;

    int speed;

    Enemy(int x, int y, int speed ) {
        this.x = x;
        this.y = y;

        this.speed = speed;


    }
    public void move(){
        x -= speed;
    }
}

class Explosion{


    int x;
    int y;
    int ex_cnt;
    int damage;

    Explosion(int x, int y, int damage){
        this.x = x;
        this.y = y;
        this.damage = damage;
        ex_cnt = 0;
    }
    public void effect(){
        ex_cnt ++;
    }
}
