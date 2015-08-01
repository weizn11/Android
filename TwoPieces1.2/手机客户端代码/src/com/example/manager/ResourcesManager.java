package com.example.manager;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.example.activity.GameActivity;

/**
 * 资源的加载和卸载(图片，字体，声音)
 * @author 张国栋
 *
 */

public class ResourcesManager {
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	public Engine engine;
	public GameActivity activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	public Font font;
	
	public Sound spash_sound;
	public Sound button_sound;
	
	public ITextureRegion splash_region;
	public ITextureRegion menu_background_region;
	public ITextureRegion menu_player_region;
	public ITextureRegion menu_players_region;
	public ITextureRegion menu_play_net_region;
	public ITextureRegion menu_exit_region;
	public ITextureRegion menu_about_region;
	public ITextureRegion menu_sound_region;
	public ITextureRegion menu_web_region;
	public ITextureRegion menu_help_region;
	
	public ITextureRegion level_background_region;
	public ITextureRegion level_two_region;
	public ITextureRegion level_four_region;
	public ITextureRegion level_nine_region;
	public ITextureRegion level_back_region;	
	
	public ITextureRegion game_checkerboard_region;
	public ITextureRegion game_gameBackground_region;
	public ITextureRegion game_tuichu_region;
	public ITextureRegion game_huiqi_region;
	public ITextureRegion game_fanhui_region;
	public ITextureRegion game_xinju_region;
	public ITextureRegion game_ti_region;
	public TiledTextureRegion game_chessShow_region;
	public TiledTextureRegion game_nowMove_region;
	public TiledTextureRegion game_chess_black_region;
	public TiledTextureRegion game_chess_white_region;
	
	public ITextureRegion game_point_region;
	
	private BitmapTextureAtlas splashTextureAtlas;
	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas levelMenuTextureAtlas;
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	
	
	public void loadMenuResoures(){
		loadMenuGraphics();
		loadMenuAudio();
	}
	private void loadFonts(){
		FontFactory.setAssetBasePath("font/");
		final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(),
				1024,1024,TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture,
				  activity.getAssets(),"Plok.ttf", 50, true, Color.BLACK, 0, Color.BLACK);
		font.load();
	}
	
	private void loadMenuAudio() {
		
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2048, 2048,
				TextureOptions.DEFAULT);//BILINEAR
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
						"background.png");

		menu_player_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"play.png");
		menu_players_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"options.png");
		menu_play_net_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"net.png");
		menu_exit_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"exit.png");
		
		menu_about_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"about.png");
		menu_web_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"web.png");
		menu_help_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"help.png");
		menu_sound_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, 
				"sound.png");
		
		try {
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, 
					BitmapTextureAtlas>(0,1,0));
			this.menuTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	//卸载菜单资源
	public void unloadMenuTextures(){
		menuTextureAtlas.unload();
	}
	
	//加载菜单资源
	public void loadMenuTextures(){
		menuTextureAtlas.load();
	}
	
	//加载关卡资源
	public void loadLevelResources(){
		loadLevelGraphics();
	}
	
	private void loadLevelGraphics(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/level/");
		levelMenuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024,
				TextureOptions.DEFAULT);//BILINEAR
		level_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelMenuTextureAtlas, activity, 
				"level_background.png");
		level_two_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelMenuTextureAtlas, activity, 
				"two.png");
		
		level_four_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelMenuTextureAtlas, activity, 
				"four.png");
		
		level_nine_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelMenuTextureAtlas, activity, 
				"nine.png");
		level_back_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelMenuTextureAtlas, activity, 
				"back.png");
		
		try {
			this.levelMenuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, 
					BitmapTextureAtlas>(0,1,0));
			this.levelMenuTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	//卸载关卡资源
	public void unloadLevelMenuTextures(){
		levelMenuTextureAtlas.unload();
	}
	
	//加载关卡资源
	public void loadLevelMenuTextures(){
		levelMenuTextureAtlas.load();
	}
	
	//加载游戏资源
	public void loadGameResources(){
		loadGameGraphics();
	}

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024,
				TextureOptions.DEFAULT);//BILINEAR
		game_huiqi_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"huiqi.png");
		game_tuichu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"tuichu.png");
		game_fanhui_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"fanhui.png");
		game_xinju_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"xinju.png");
		game_ti_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"ti.png");
		game_gameBackground_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"gameBackground.png");
		
		game_point_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, 
				"point.png");
		
		game_nowMove_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "nowMove.png", 2, 2);
		game_chess_black_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "chess_black.png", 2, 2);
		game_chess_white_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "chess_white.png", 2, 2);
		game_chessShow_region =BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "chessShow.png", 6, 1);
		try {
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, 
					BitmapTextureAtlas>(0,1,0));
			this.gameTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}
	
	//卸载游戏资源
	public void unloadGameTextures(){
		
	}
	
	public void loadSplashScreen(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png",
				0,0);
		splashTextureAtlas.load();
		
		loadFonts();
		loadAudio();
	}
	
	private void loadAudio() {
		try {
			spash_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), this.activity, "mfx/start.ogg");
			button_sound = SoundFactory.createSoundFromAsset(activity.getSoundManager(), this.activity, "mfx/ben.ogg");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void unloadSplashScreen(){
		splashTextureAtlas.unload();
		splash_region = null;
	}
	
	public static void prepareManager(Engine engine, GameActivity activity, Camera camera,
			VertexBufferObjectManager vbom){
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	public static ResourcesManager getInstance(){
		return INSTANCE;
	}

}
