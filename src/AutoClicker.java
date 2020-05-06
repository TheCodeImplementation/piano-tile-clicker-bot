import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/*
loop through each channel. if a dark pixel is detected, click (uses 1 robot)
 */

public class AutoClicker {

    static Robot robot;
    static final int[] channels = {553,575,698,806};
    static final int clickHeight = 500;

    static final Color SGBlue = new Color(54,174,216);
    static final Color firstButtonBlue = new Color(0,161,199);
    static final Color offBlack = new Color(15,20,19);
    static final long timeLimit = 320; //ms

    public static void run() {

        try{robot = new Robot();}catch(AWTException e){}
        robot.setAutoDelay(50);
        robot.setAutoWaitForIdle(false);

        //wait for tile click to be maximized
        while(!robot.getPixelColor(10,10).equals(SGBlue)){
            //poll
        }
        try{Thread.sleep(2000);}catch(InterruptedException e){}

        //click first tile
        for(int channel : channels){
            if(robot.getPixelColor(channel, clickHeight).equals(firstButtonBlue)){
                mouseMoveAndClick(channel, clickHeight);
                break;
            }
        }

        int channel = -1;


        while(true){
            channel = detectBlackPixel(channel);
            mouseMoveAndClick(channel, clickHeight);
        }
    }

    private static int detectBlackPixel(int exclude) {
        long start = System.currentTimeMillis();
        Color color;
        while(true) {
            //terminate if wait too long
            if(System.currentTimeMillis() - start > timeLimit){
                System.out.println("terminate");
                System.exit(0);
            }
            for (int channel : channels) {
                if(channel == exclude){
                    //skip-do noting
                }else {
                    color = robot.getPixelColor(channel, clickHeight);
                    if (color.equals(Color.black) || color.equals(offBlack)) {
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
