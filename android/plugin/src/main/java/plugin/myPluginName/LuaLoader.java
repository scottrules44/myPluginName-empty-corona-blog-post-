//
//  LuaLoader.java
//  TemplateApp
//
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

// This corresponds to the name of the Lua library,
// e.g. [Lua] require "plugin.library"
package plugin.likeButton;


import com.ansca.corona.CoronaActivity;
import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeListener;
import com.ansca.corona.CoronaRuntimeTask;
import com.ansca.corona.CoronaRuntimeTaskDispatcher;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;
import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import java.io.File;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.camera2.params.Face;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Implements the Lua interface for a Corona plugin.
 * <p>
 * Only one instance of this class will be created by Corona for the lifetime of the application.
 * This instance will be re-used for every new Corona activity that gets created.
 */
@SuppressWarnings("WeakerAccess")
public class LuaLoader implements JavaFunction, CoronaRuntimeListener {
	/** Lua registry ID to the Lua function to be called when the ad request finishes. */
	private int fListener;

	/** This corresponds to the event name, e.g. [Lua] event.name */
	private static final String EVENT_NAME = "imageCrop";


	/**
	 * Creates a new Lua interface to this plugin.
	 * <p>
	 * Note that a new LuaLoader instance will not be created for every CoronaActivity instance.
	 * That is, only one instance of this class will be created for the lifetime of the application process.
	 * This gives a plugin the option to do operations in the background while the CoronaActivity is destroyed.
	 */
	@SuppressWarnings("unused")
	public LuaLoader() {
		// Initialize member variables.
		fListener = CoronaLua.REFNIL;

		// Set up this plugin to listen for Corona runtime events to be received by methods
		// onLoaded(), onStarted(), onSuspended(), onResumed(), and onExiting().
		CoronaEnvironment.addRuntimeListener(this);
	}

	/**
	 * Called when this plugin is being loaded via the Lua require() function.
	 * <p>
	 * Note that this method will be called every time a new CoronaActivity has been launched.
	 * This means that you'll need to re-initialize this plugin here.
	 * <p>
	 * Warning! This method is not called on the main UI thread.
	 * @param L Reference to the Lua state that the require() function was called from.
	 * @return Returns the number of values that the require() function will return.
	 *         <p>
	 *         Expected to return 1, the library that the require() function is loading.
	 */
	@Override
	public int invoke(LuaState L) {
		// Register this plugin into Lua with the following functions.
		NamedJavaFunction[] luaFunctions = new NamedJavaFunction[] {
			new newButton(),
		};
		String libName = L.toString( 1 );
		L.register(libName, luaFunctions);


		// Returning 1 indicates that the Lua require() function will return the above Lua library.
		return 1;
	}

	/**
	 * Called after the Corona runtime has been created and just before executing the "main.lua" file.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been loaded/initialized.
	 *                Provides a LuaState object that allows the application to extend the Lua API.
	 */
	@Override
	public void onLoaded(CoronaRuntime runtime) {
		// Note that this method will not be called the first time a Corona activity has been launched.
		// This is because this listener cannot be added to the CoronaEnvironment until after
		// this plugin has been required-in by Lua, which occurs after the onLoaded() event.
		// However, this method will be called when a 2nd Corona activity has been created.

	}

	/**
	 * Called just after the Corona runtime has executed the "main.lua" file.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been started.
	 */
	@Override
	public void onStarted(CoronaRuntime runtime) {

	}

	/**
	 * Called just after the Corona runtime has been suspended which pauses all rendering, audio, timers,
	 * and other Corona related operations. This can happen when another Android activity (ie: window) has
	 * been displayed, when the screen has been powered off, or when the screen lock is shown.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been suspended.
	 */
	@Override
	public void onSuspended(CoronaRuntime runtime) {
	}

	/**
	 * Called just after the Corona runtime has been resumed after a suspend.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that has just been resumed.
	 */
	@Override
	public void onResumed(CoronaRuntime runtime) {
	}

	/**
	 * Called just before the Corona runtime terminates.
	 * <p>
	 * This happens when the Corona activity is being destroyed which happens when the user presses the Back button
	 * on the activity, when the native.requestExit() method is called in Lua, or when the activity's finish()
	 * method is called. This does not mean that the application is exiting.
	 * <p>
	 * Warning! This method is not called on the main thread.
	 * @param runtime Reference to the CoronaRuntime object that is being terminated.
	 */
	@Override
	public void onExiting(CoronaRuntime runtime) {
		// Remove the Lua listener reference.
		CoronaLua.deleteRef( runtime.getLuaState(), fListener );
		fListener = CoronaLua.REFNIL;
	}


		@SuppressWarnings("unused")
	private class newButton implements NamedJavaFunction {
		@Override
		public String getName() {
			return "newButton";
		}
		@Override
		public int invoke(LuaState L) {


			L.getField(1, "x");
			final int x = L.toInteger(-1);
			L.pop(1);
			L.getField(1, "y");
			final  int y = L.toInteger(-1);
			L.pop(1);
			final LikeView likeView = new LikeView(CoronaEnvironment.getCoronaActivity());
			Point myPoint = CoronaEnvironment.getCoronaActivity().convertCoronaPointToAndroidPoint(x, y);
			L.getField(1, "isBoxCount");
			boolean isBoxCount = false;
			if (L.isBoolean(-1) && L.toBoolean(-1) == true){
				likeView.setLikeViewStyle(LikeView.Style.BOX_COUNT);
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(200, 100);
				params.leftMargin = (myPoint.x -(params.width/2));
				params.topMargin = (myPoint.y -(params.height/2));
				likeView.setLayoutParams(params);
				isBoxCount = true;
			}else{
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 50);
				params.leftMargin = (myPoint.x -(params.width/2));
				params.topMargin = (myPoint.y -(params.height/2));
				likeView.setLayoutParams(params);
			}
			final boolean myIsBoxCount = isBoxCount;
			L.pop(1);
            L.getField(1, "id");
            likeView.setObjectIdAndType(L.toString(-1), LikeView.ObjectType.PAGE);
            L.pop(1);

			Runnable activityRunnable = new Runnable() {

				@Override
				public void run() {
					final FrameLayout rootLayout = (FrameLayout) CoronaEnvironment.getCoronaActivity().getWindow().getDecorView().findViewById(android.R.id.content);
					rootLayout.addView(likeView);
				}
			};

			CoronaEnvironment.getCoronaActivity().runOnUiThread( activityRunnable );
            L.pushJavaObject(likeView);
            L.newTable();
            L.pushJavaFunction(new JavaFunction() {
                @Override
                public int invoke(LuaState luaState) {
                    if (luaState.toString(2).equals("x")){
                        luaState.pushNumber(x);
                        return 1;
                    }

                    if (luaState.toString(2).equals("y")){
                        luaState.pushNumber(y);
                        return 1;
                    }

                    else if(luaState.toString(2).equals("destroy")){
                        luaState.pushJavaFunction(new JavaFunction() {
                            @Override
                            public int invoke(LuaState l) {
                                ((ViewGroup) likeView.getParent()).removeView(likeView);
                                return 0;
                            }
                        });
                        return 1;
                    }
                    else if(luaState.toString(2).equals("getSize")){
                        luaState.pushJavaFunction(new JavaFunction() {
                            @Override
                            public int invoke(LuaState l) {
                                l.getGlobal("display");
                                l.pushString("actualContentHeight");
                                l.getTable(-2);

                                double actualHeight = l.toNumber(-1);
                                l.getGlobal("display");
                                l.pushString("actualContentWidth");
                                l.getTable(-2);
                                double actualWidth = l.toNumber(-1);

                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                CoronaEnvironment.getCoronaActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int heightDevice = displayMetrics.heightPixels;
                                int widthDevice = displayMetrics.widthPixels;
                                com.ansca.corona.CoronaActivity activity =
                                        com.ansca.corona.CoronaEnvironment.getCoronaActivity();
								if (myIsBoxCount == true){
									l.pushNumber((actualWidth/widthDevice)*50);
									l.pushNumber((actualWidth/widthDevice)*150);
								}else{
									l.pushNumber((actualWidth/widthDevice)*50);
									l.pushNumber((actualWidth/widthDevice)*100);
								}

                                return 2;
                            }
                        });
                        return 1;
                    }

                    else{
                        return 0;
                    }

                }
            });
            L.setField(-2, "__index");
            L.pushJavaFunction(new JavaFunction() {
                @Override
                public int invoke(LuaState luaState) {
                    if(luaState.toString(2).equals("x")){
                        Point myPoint = CoronaEnvironment.getCoronaActivity().convertCoronaPointToAndroidPoint(luaState.toInteger(3), 0);
                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) likeView.getLayoutParams();
                        params.leftMargin = (myPoint.x -(params.width/2));


                        likeView.setLayoutParams(params);
                        return 0;
                    }
                    if(luaState.toString(2).equals("y")){
                        Point myPoint = CoronaEnvironment.getCoronaActivity().convertCoronaPointToAndroidPoint(0, luaState.toInteger(3));
                        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams) likeView.getLayoutParams();
                        params.topMargin = (myPoint.y -(params.height/2));


                        likeView.setLayoutParams(params);
                        return 0;
                    }
                    return 0;
                }
            });
            L.setField(-2, "__newindex");
            L.setMetatable(-2);
			return 1;
		}
	}

}
