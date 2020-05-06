import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/*
click and detect height will rise to the top-most point but the click height will drift down over time
 */

public class AutoClicker9 {

    static Robot robot;
    static final int[] channels = {553,575,698,806};
    static final int topMostHeight = 80;
    static int clickHeight = topMostHeight;
    static final int bottomMostHeight = 760;

    static final Color SGBlue = new Color(54,174,216);
    static final long timeLimit = 1000; //ms

    public static void run() {

        try{robot = new Robot();}catch(AWTException e){}

        //wait for tile click to be maximized
        while(!robot.getPixelColor(10,10).equals(SGBlue)){
            //poll
        }
        try{Thread.sleep(2000);}catch(InterruptedException e){}

        //click first 3 tiles
        int variableHeight = 570;
        for(int i = 0; i < 3; i++){
            for(int channel : channels){
                if(robot.getPixelColor(channel, variableHeight).getRed() < 20){
                    mouseMoveAndClick(channel, variableHeight);
                    variableHeight -= 200;
                    break;
                }
            }
        }

        int channel = -1;
        int score = 0;
        while(true){
            channel = detectBlackPixel(channel);
            mouseMoveAndClick(channel, clickHeight);
            switch(++score){
                case 1000:
                    clickHeight += 30;
                    break;
                case 1500:
                    clickHeight += 30;
                    break;
                case 2000:
                    clickHeight += 10;
                    break;
                case 2500:
                    clickHeight += 10;
                    break;
            }
        }
    }

    private static int detectBlackPixel(int exclude) {
        long start = System.currentTimeMillis();
        while(true) {
            //terminate if wait too long
            if(System.currentTimeMillis() - start > timeLimit){
                System.out.println("terminate");
                robot.mouseMove(300,300); //just so I have a visual queue that it has terminated.
                System.exit(0);
            }
            for (int channel : channels) {
                if(channel == exclude){
                    //skip-do noting
                }else {
                    if (robot.getPixelColor(channel, topMostHeight).getRed() < 20) {
                        return channel;
                    }
                }
            }
        }
    }

    private static void mouseMoveAndClick(int x, int y){
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}