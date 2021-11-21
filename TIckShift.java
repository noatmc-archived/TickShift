package com.gamesense.client.module.modules.exploits;

import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.EntityLivingBase;

/*
* @author hausemasterissue
* @since 6/11/2021
*/

// this declares the module, usually you will do a constructor method with a super keyword
@Module.Declaration(name = "TickShift", category = Category.Exploits)
public class TickShift extends Module {
	 
    // the option to disable after the tickshift happens	
    BooleanSetting disable = registerBoolean("Disable", true);	
    // the amount of ticks to wait until the module auto-disables
    IntegerSetting disableTicks = registerInteger("DisableTicks", 26, 1, 100);
    // the option to only turn on timer when the player moves	
    BooleanSetting movementEnable = registerBoolean("MovementEnable", true);		
    // the amount of ticks the player needs to stand still
    IntegerSetting enableTicks = registerInteger("EnableTicks", 30, 1, 100);
    // the game tick speed multiplier
    DoubleSetting multiplier = registerDouble("Multiplier", 3.0, 1.0, 10.0);
	
    private int ticksPassed = 0;
    private int ticksStill = 0;
    private boolean playerMoving;
    private boolean timerOn = false;
	
    // the method that gets called once every game update (1 tick)
    public void onUpdate() {
	 // make sure we dont have timer on
	 if(timerOn == false) {
		// if they are moving remove 1 from the amount of ticks they are standing still for
		if(isMoving(mc.player)) {
			// prevent the amount of ticks still to go below 0
			if(ticksStill >= 1) {
				ticksStill--;	
			}
			
	 	} else if (!isMoving(mc.player)) {
	      		// if they arent increase it by 1
	      		ticksStill++;	 
	 	} 
	 }
	 
	 
	 // if the amount of ticks the player is standing still is equal to the amount of ticks for the timer to start do it
	 if(ticksStill >= enableTicks.getValue()) {
		 // set timer on to true which stops the movement checks
		 timerOn = true;
		 // change how the module enables based on the movementenable setting
		 if(movementEnable.getValue()) {
			// if the player is pressing any movement keys enable the timer 
			if(mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
			 	// change the client side tick speed to something greater 
				mc.timer.tickLength = ((float) (50.0 / multiplier.getValue())); 
				// increase the number of ticks passed 
				ticksPassed++; 
		 	}	  
		 } else {
			// enable the timer no matter what 
			mc.timer.tickLength = ((float) (50.0 / multiplier.getValue())); 
			ticksPassed++; 		 
		 }
		 
		 
	 }
	    
        // if the amount of ticks passed is greater than or equal to the amount of ticks that need to pass for the module to disable, disable the module (duh)
	if(ticksPassed >= disableTicks.getValue()) {
	    	ticksPassed = 0;
            	// decide whether to disable or reset the module
		if(disable.getValue()) {
			// disable the module
			disable();
		} else {
			// reset the module
			reset();	
		}
		
	 }
    }
	
    // the method that checks if an entity is moving	
    public static boolean isMoving(EntityLivingBase entity) {
        return entity.moveForward != 0 || entity.moveStrafing != 0;
    }		
	  
    // the method that gets called when the module is disabled
    public void onDisable() {
        // set the client tick length back to the default
	timerOn = false;
	ticksStill = 0;
    	mc.timer.tickLength = 50f;    
    }
	
    // the method that gets called on reset	
    public void reset() {
	timerOn = false;
	ticksStill = 0;
    	mc.timer.tickLength = 50f;   
    }
 
    // the method that gets called to update the hud info	
    public String getHudInfo() {
	return "[" + ChatFormatting.WHITE + String.valueOf(ticksStill) + ChatFormatting.GRAY + "]"; 
    }
 

}
