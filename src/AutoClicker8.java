import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/*
Similar to No 1 but with optimisations: rather than checking if the pixel's color is equal to black or offBlack, just
test that the red value is less than 20. The way the click height increases is different too.
 */

public class AutoClicker8 {

    static Robot robot;
    static final int[] channels = {564,645,731,803};
    static final int detectHeight = 389;
    static int clickHeight = 389;

    static final Color SGBlue = new Color(54,174,216);
    static final Color firstButtonBlue = new Color(0,161,199);
    static final long timeLimit = 320; //ms

    public static void run() {

        try{
            robot = new Robot();
            robot.setAutoDelay(50);
            robot.setAutoWaitForIdle(true);
        }catch(AWTException e){}

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

        int ticker = 0;
        while(true){
            channel = detectBlackPixel(channel);
            mouseMoveAndClick(channel, clickHeight);
            ticker++;
            if(ticker % 100 == 0) {
                clickHeight += 8;
            }
        }
    }

    private static int detectBlackPixel(int exclude) {
        long start = System.currentTimeMillis();
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
                    if (robot.getPixelColor(channel, detectHeight).getRed() < 20) {
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
