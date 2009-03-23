package org.sunspotworld;

/**
 * This class represents the "Set timer/alarm" state of the clock.
 * The user can set the alarm between 0 (off) or 8 minutes from that moment.
 */
public class SetTimerState implements IState {
    private StartApplication app;
    
    private int minutesTillAlarm = 0;

    /**
     * Constructor
     * @param app
     */
    SetTimerState(StartApplication app){
        this.app = app;
    }
    
    /**
     * Increase the alarm timer by 1.
     */
    public void switch1(){
        minutesTillAlarm = ( minutesTillAlarm + 1 ) % 9;
        displayMinutesInLed();
    }

    /**
     * Go back to clock mode.
     */
    public void switch2(){
        
        app.changeState( app.STATE_CLOCK );
    }

    /**
     * Entry point for the Alarm mode. Resets the alarm to 0 (off)
     */
    public void run(){
        minutesTillAlarm = 0;                
        System.out.println( "I'm now setting the TIMER!!" );
        for ( int i = 0; i < 8; i++ ){
            app.leds[i].setRGB( 255, 255, 255 );
            app.leds[i].setOn();
        }
    }

    /**
     * Displays the number of minutes using 8 leds.
     * Each Led (from left to right) represents one minute
     */
    private void displayMinutesInLed(){
        System.out.println( "Alarm will ring in: " +minutesTillAlarm );
        for ( int i = 0; i < 8; i++ ){
            if ( i + 1 == minutesTillAlarm ){
                app.leds[i].setRGB(0,255,0);
                app.leds[i].setOn();
            }
            else {
                app.leds[i].setOff();
            }
        }
    }
    
}
