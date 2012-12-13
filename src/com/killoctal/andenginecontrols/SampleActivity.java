package com.killoctal.andenginecontrols;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.killoctal.andenginecontrols.scrollablemenu.ScrollableMenuControl;
import com.killoctal.andenginecontrols.scrollablemenu.ScrollableMenuItem;


public class SampleActivity extends SimpleBaseGameActivity {
	/// Caméra principale
    public Camera mCamera;
    public Scene mScene;
    
    
    /// L'instance de l'Activity
    private static SampleActivity mActivity;
    
	public static SampleActivity getActivity() { return mActivity; }
	    
	public SampleActivity() {
		mActivity= this;
		// TODO Stub du constructeur généré automatiquement
	}

	public EngineOptions onCreateEngineOptions() {
		// Création d'une caméra de la taille de l'écran
				mCamera = new Camera(0, -0,
						getWindowManager().getDefaultDisplay().getWidth(),
						getWindowManager().getDefaultDisplay().getHeight()
						);
				
				// Création des options
		        EngineOptions engineOptions = new EngineOptions(true,
		        		ScreenOrientation.LANDSCAPE_FIXED, 
		        		new FillResolutionPolicy(),
		        		mCamera
		        		);
		        
		        return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		// TODO Stub de la méthode généré automatiquement
		
	}

	@Override
	protected Scene onCreateScene() {
		mScene = new Scene();
		
		mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		ScrollableMenuControl tmpMenu = new ScrollableMenuControl(0,0,100,500, 100, 50, getVertexBufferObjectManager(), mScene);
		mScene.attachChild(tmpMenu);
		
		tmpMenu.setColor(1, 0, 0);
		
		tmpMenu.addItemRow(new ScrollableMenuItem(getVertexBufferObjectManager()) {
			@Override
			public void onClick(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				setColor(0, 1, 0);
			}
		});
		
		ScrollableMenuItem tmp = new ScrollableMenuItem(getVertexBufferObjectManager()) {
			@Override
			public void onClick(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				setColor(0, 0, 0);
			}
		};
		tmp.setHeight(140);
		tmpMenu.addItemRow(tmp);
		
		for(int i=0 ; i< 20;i++)
		{
			tmpMenu.addItemRow(new ScrollableMenuItem(getVertexBufferObjectManager()) {
				@Override
				public void onClick(TouchEvent pceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
					setColor(1, 0, 1);
				}
			});
		}
		
		tmpMenu.updateMenu();
		return mScene;
	}

}
