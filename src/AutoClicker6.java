import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingDeque;

/*
Amalgamation of 2 and 3. Thread 2 knows what channel the block is coming on but not when, so it continually checks.
When it gets it, it move to the next channel. The difference is the height at which it clicks increases over time.
 */

public class AutoClicker6 {

    private static Robot robot = null;
    static LinkedBlockingDeque<Integer> tiles = new LinkedBlockingDeque<>(5);
    static final int[] channelsDetect = {564,645,731,803};
    static final int initialDetectHeight = 388;
    static final int secondDetectHeight = 388;
    static final int maxClickHeight = 571;
    static final int minClickHeight = 394;

    static final Color SGBlue = new Color(54,174,216);
    static final Color firstButtonBlue = new Color(0,161,199);
    static final Color offBlack = new Color(15,20,19);
    static final long timeLimit = 1000; //ms

    public static void run() {

        try {robot = new Robot();} catch (AWTException e) {}

        //thread 1 - check for black tiles
        Thread t1 = new Thread( () -> {
            Robot robot = null;
            try {robot = new Robot();} catch (AWTException e) {}

            long start;
            Color color;
            int exclude = -1;

            //click first tile
            for(int channel : channelsDetect){
                if(robot.getPixelColor(channel, initialDetectHeight).equals(firstButtonBlue)){
                    tiles.add(channel);
//                    exclude = channel;
                    break;
                }
            }
            //and the second second
            for(int channel : channelsDetect){
                if(robot.getPixelColor(channel, 379).equals(Color.black) ||
                        robot.getPixelColor(channel, 379).equals(offBlack)){
                    tiles.add(channel);
                    exclude = channel;
                    break;
                }
            }

            while(true) {
                start = System.currentTimeMillis();
                innerwhile :
                while(true){
                    //terminate if wait too long
                    if(System.currentTimeMillis() - start > timeLimit){
                        System.out.println("terminate");
                        System.exit(0);
                    }
                    for (int channel : channelsDetect) {
                        if(channel == exclude){
                            //skip-do noting
                        }else {
                            color = robot.getPixelColor(channel, initialDetectHeight);
                            if (color.equals(Color.black) || color.equals(offBlack)) {
                                tiles.add(channel);
                                exclude = channel;
                                break innerwhile;
                            }
                        }
                    }
                }
            }
        });

        //thread 2 click on the tiles
        Thread t2 = new Thread( () -> {
            int count = 0;
            int clickHeight = secondDetectHeight;
            int channel;
            while(true){
                if(count == 110)
                    clickHeight = secondDetectHeight + 10;
                else if(count == 270)
                    clickHeight = secondDetectHeight + 20;
//                else if(count == 400)
//                    clickHeight = detectHeight + 30;
//                else if(count == 500)
//                    clickHeight = detectHeight + 35;
                if(tiles.size() > 0){
                    channel = tiles.remove();
                    while(true){
                        Color color = robot.getPixelColor(channel, secondDetectHeight);
                        if(color.equals(Color.black) || color.equals(offBlack) || color.equals(firstButtonBlue)){
                            mouseMoveAndClick(robot, channel, clickHeight );
                            count++;
                            break;
                        }
                    }
                }
            }
        });

        //wait for tile click to be maximized
        while(!robot.getPixelColor(10,10).equals(SGBlue)){
            //try again in half a second
            try{Thread.sleep(500);}catch(InterruptedException e){}
        }
        try{Thread.sleep(2000);}catch(InterruptedException e){}

        t1.start();
        t2.start();
    }

    private static void mouseMoveAndClick(Robot robot, int x, int y){
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

}
