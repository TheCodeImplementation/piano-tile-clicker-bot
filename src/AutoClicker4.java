import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.concurrent.LinkedBlockingDeque;

/*
DIDN'T WORK: 4 threads for each channel. each thread continually tests is channel's pixels for a dark one then clicks.
There's an additional thread for a safety/termination test.
 */

public class AutoClicker4 {

    private static Robot robot = null;
    static LinkedBlockingDeque<Integer> tiles = new LinkedBlockingDeque<>(5);
    static final int[] channelsDetect = {564,645,731,803};
    static final int clickHeight = 530;

    static final Color SGBlue = new Color(54,174,216);
    static final Color firstButtonBlue = new Color(0,161,199);
    static final Color offBlack = new Color(15,20,19);
    static final long timeLimit = 1000; //ms
    static long timer;

    public static void run() {

        timer = System.currentTimeMillis();

        try {robot = new Robot();} catch (AWTException e) {}

        //wait for tile click to be maximized
        while(!robot.getPixelColor(10,10).equals(SGBlue)){
            //try again in half a second
            try{Thread.sleep(500);}catch(InterruptedException e){}
        }
        try{Thread.sleep(2000);}catch(InterruptedException e){}

        Thread threads[] = new Thread[5];
        for(int i = 0 ; i < threads.length-1; i++){
            threads[i] = getGuard(channelsDetect[i]);
        }

        //add safety thread
        threads[4] = new Thread( () -> {
            while(true){
                System.out.println(System.currentTimeMillis() - timer);
                if(System.currentTimeMillis() - timer > timeLimit){
                    System.out.println("terminate");
                    System.exit(0);
                }
            }
        });

        threads[0].run();
        threads[1].run();
        threads[2].run();
        threads[3].run();
        threads[4].run();
        for(Thread thread : threads){
            thread.run();
        }

    }

    private static Robot robotFactory() {
        Robot robot = null;
        try {robot = new Robot();} catch (AWTException e) {}
        return robot;
    }

    private static Thread getGuard(int x){
        Robot robot = robotFactory();
        return new Thread( () -> {
            while(true){
                Color color = robot.getPixelColor(x, clickHeight);
                if(color.equals(Color.black) || color.equals(offBlack) || color.equals(firstButtonBlue)){
                    mouseMoveAndClick(robot, x, clickHeight);
                    timer = System.currentTimeMillis();
                }
            }
        });
    }

    private static void mouseMoveAndClick(Robot robot, int x, int y){
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

}
