import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingDeque;

/*
Multi-threaded: one thread loops through each channel detecting dark pixels. When found, it adds the channel's x
coordinate to a queue. Thread 2 waits 'till the queue has elements and then removes and click them. The height at
which it clicks increases over time.
 */

public class AutoClicker2 {

    private static Robot robot = null;
    static LinkedBlockingDeque<Integer> tiles = new LinkedBlockingDeque<>(5);
    static final int[] channelsDetect = {564,645,731,803};
    static final int detectHeight = 388;

    static final Color SGBlue = new Color(54,174,216);
    static final Color firstButtonBlue = new Color(0,161,199);
    static final Color offBlack = new Color(15,20,19);
    static final long timeLimit = 1000; //ms

    public static void run() {

        try {robot = new Robot();} catch (AWTException e) {}
        robot.setAutoDelay(40);

        //thread 1 - check for black tiles
        Thread t1 = new Thread( () -> {
            Robot robot = null;
            try {robot = new Robot();} catch (AWTException e) {}

            long start;
            Color color;
            int exclude = -1;

            //click first tile
            for(int channel : channelsDetect){
                if(robot.getPixelColor(channel, detectHeight).equals(firstButtonBlue)){
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
                            color = robot.getPixelColor(channel, detectHeight);
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
            int modifiedHeight = detectHeight;
            while(true){
                if(count == 110)
                    modifiedHeight = detectHeight + 10;
                else if(count == 400)
                    modifiedHeight = detectHeight + 30;
                else if(count == 500)
                    modifiedHeight = detectHeight + 35;
                if(tiles.size() > 0){
                    mouseMoveAndClick(robot, tiles.remove(), modifiedHeight );
                    count++;
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
