import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.LinkedBlockingDeque;

/*
Wait till the deque contains 5 tiles and then click 4 of them at once.
highest: 135
SOMETIMES WORKS. SOMETIMES APPEARS TO DO NOTHING.
 */

public class AutoClicker7 {

    private static Robot robot = null;
    static LinkedBlockingDeque<Integer> tiles = new LinkedBlockingDeque<>(5);
    static final int[] channels = {564,645,731,803};
    static final int initialDetectHeight = 86;
    static final int[] blockHeights = {763,570,380,190};


    private static final Color SGBlue = new Color(54,174,216);
    private static final Color firstButtonBlue = new Color(0,161,199);
    private static final Color offBlack = new Color(15,20,19);

    public static void run() {

        try {robot = new Robot();} catch (AWTException e) {}
        robot.setAutoDelay(1);

        //thread 1 - check for black tiles
        Thread t1 = new Thread( () -> {

            long start;
            Color color;
            int exclude = -1;

            //find the first 3
            int variableHeight = 570;
            for(int i = 0; i < 3; i++){
                for(int channel : channels){
                    color = robot.getPixelColor(channel, variableHeight);
                    if(color.equals(Color.black) || color.equals(offBlack) || color.equals(firstButtonBlue)){
                        tiles.add(channel);
                        exclude = channel;
                        variableHeight -= 200;
                        break;
                    }
                }
            }

            while(true) {
//                start = System.currentTimeMillis();
                innerwhile :
                while(true){
                    for (int channel : channels) {
                        if(channel == exclude){
                            //skip-do noting
                        }else {
                            if (robot.getPixelColor(channel, initialDetectHeight).getRed() < 20) {
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
            //click the first 3
            while(true){
                if(tiles.size() == 3){
                    for(int i = 1; i <= 3; i++){
                        mouseMoveAndClick( tiles.remove(), blockHeights[i]);
                    }
                    break;
                }
            }
            int clickHeight = 763;
            while(true){
                if(tiles.size() == 5){
                    for(int i = 0; i < 4; i++) {
                        mouseMoveAndClick( tiles.remove(), (clickHeight - (190*i)));
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

    private static void mouseMoveAndClick(int x, int y){
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

}
