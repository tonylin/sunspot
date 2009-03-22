package org.sunspotworld;

public class SetTimerState implements IState {
    private StartApplication app;
    
    private int minutesTillAlarm = 0;

    SetTimerState(StartApplication app){
        this.app = app;
    }
    
    public void switch1(){
        minutesTillAlarm = ( minutesTillAlarm + 1 ) % 9;
        displayMinutesInLed();
    }

    public void switch2(){
        // Return to clock mode
        app.changeState( app.STATE_CLOCK );
    }

    public void run(){
        minutesTillAlarm = 0;                
        System.out.println( "I'm now setting the TIMER!!" );
        for ( int i = 0; i < 8; i++ ){
            app.leds[i].setRGB( 255, 255, 255 );
            app.leds[i].setOn();
        }
    }

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
