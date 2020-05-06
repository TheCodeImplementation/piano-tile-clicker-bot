import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingDeque;

/*
Same as 1 but the click heigh increases as time goes on.
 */

public class AutoClicker5 {

    private static Robot robot = null;
    static final int[] channels = {564,645,731,803};
    static final int detectHeight = 388;
    static int clickHeight = 388;

    static final Color SGBlue = new Color(54,174,216);
    static final Color firstButtonBlue = new Color(0,161,199);
    static final Color offBlack = new Color(15,20,19);
    static final long timeLimit = 1000; //ms
    static long timer;

    public static void run() {
        try {robot = new Robot();} catch (AWTException e) {}

        Color color;

        //wait for tile click to be maximized
        while(!robot.getPixelColor(10,10).equals(SGBlue)){
            //poll
        }
        try{Thread.sleep(2000);}catch(InterruptedException e){}

        //click first button
        for(int channel : channels){
            color = robot.getPixelColor(channel, clickHeight);
            if(color.equals(firstButtonBlue)){
                mouseMoveAndClick(channel , clickHeight);
            }
        }

        int counter = 0;
        int lastChannel = -1;
        timer = System.currentTimeMillis();
        while(true){
            switch(counter){
                case 500:
                    clickHeight += 10;
                    break;
            }
            if(System.currentTimeMillis() - timer > timeLimit) {
                System.out.println("terminated");
                System.exit(0);
            }

            for(int channel : channels){
                if(channel == lastChannel){
                    continue;
                }
                color = robot.getPixelColor(channel, detectHeight);
                if(color.equals(Color.black) || color.equals(offBlack)){
                    mouseMoveAndClick(channel , clickHeight);
                    timer = System.currentTimeMillis();
                    lastChannel = channel;
                    counter++;
                    break;
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
