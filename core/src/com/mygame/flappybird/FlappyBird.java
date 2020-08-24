package com.mygame.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture bg;
	Texture[] birds ;
	int flapstate=0;
	float birdY=0;
	float velocity=0;
	int gameState=0;
	int gravity=2;
	Texture toptube;
	Texture bottomtube;
	float gap=400;

	Random randomGenrator;
	float tubeVelocity=4;
	int numberOftube=4;
	float[] tubeX = new float[numberOftube];
	float[] tubeOfset = new float[numberOftube];
	float distanceBetweentubes=4;
	Circle birdShape = new Circle();
	//ShapeRenderer shapeRenderer;
	Rectangle[] toptubeRectangle;
	Rectangle[] bottomtubeRectangle;
	int score=0;
	int scoringtube=0;
	BitmapFont font;
	Texture gameover;
	private Music bgmusic;
	Sound dieSound;
	Sound pointsound;
	long id;
	@Override
	public void create () {
		batch = new SpriteBatch();
		bg = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] =new  Texture("bird.png");
		birds[1] =new Texture("bird2.png");
		gameover = new Texture("gameover.png");
		bgmusic =Gdx.audio.newMusic(Gdx.files.internal("level1flappybird.ogg")) ;
		bgmusic.setLooping(true);
		bgmusic.play();
		bgmusic.setVolume(0.5f);

		 dieSound = Gdx.audio.newSound(Gdx.files.internal("diesound.wav"));
		pointsound = Gdx.audio.newSound(Gdx.files.internal("pointsound.wav"));
		toptube = new Texture("toptube.png");
		bottomtube = new Texture("bottomtube.png");
		randomGenrator = new Random();
		distanceBetweentubes = Gdx.graphics.getWidth()/2+140;
		toptubeRectangle = new Rectangle[numberOftube];
		bottomtubeRectangle = new Rectangle[numberOftube];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		startGame();
		id = dieSound.play();

		//shapeRenderer = new ShapeRenderer();



	}
	public void startGame(){
		birdY = Gdx.graphics.getHeight()/2-birds[flapstate].getHeight()/2;
		for(int i=0;i<numberOftube;i++){
			tubeOfset[i] = (randomGenrator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap);
			tubeX[i] = (Gdx.graphics.getWidth() / 2 - toptube.getWidth()/2)+Gdx.graphics.getWidth()+i*distanceBetweentubes;
			toptubeRectangle[i] = new Rectangle();
			bottomtubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		/* for changing the bird image*/
		if (flapstate == 0) {
			flapstate = 1;
		} else {
			flapstate = 0;
		}
		/* changing game state*/
		if(gameState==0){

			if (Gdx.input.justTouched()) {gameState=1;}
		}
		if(gameState==1) {

			dieSound.stop(id);
			/* for scoredetection*/
			if(tubeX[scoringtube]<Gdx.graphics.getWidth()/2){
				score++;
				pointsound.play();
				if(scoringtube<numberOftube-1){
					scoringtube++;
				}else{
					scoringtube=0;
				}

			}

			//Gdx.app.log("SCOREEEEEEEEEEEE",String.valueOf(score));
			/* for genrating tube in random order*/
			for(int i=0;i<numberOftube;i++) {
				if(tubeX[i]<-toptube.getWidth()){

					tubeX[i] += numberOftube*distanceBetweentubes;
					tubeOfset[i] = (randomGenrator.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap);
				}else{
					tubeX[i] = tubeX[i]-tubeVelocity;
				}

				batch.draw(toptube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOfset[i]);
				batch.draw(bottomtube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOfset[i]);
				toptubeRectangle[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOfset[i],toptube.getWidth(),toptube.getHeight());
				bottomtubeRectangle[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOfset[i],bottomtube.getWidth(),bottomtube.getHeight());
			}

            if(Gdx.input.justTouched()){
                velocity = -30;

            }
			/* for bird jumping*/
            if(birdY > 0 ){
			velocity = velocity+gravity;
			birdY -= velocity;
            }else {
            	gameState=2;
			}
			if (Gdx.input.justTouched()) {
				velocity++;
				birdY += velocity;
			}

		}else if(gameState==0){

			if (Gdx.input.justTouched()) {gameState=1;}

		}else if(gameState==2){
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);
			if (Gdx.input.justTouched()) {
				gameState=1;
				startGame();
				score=0;
				scoringtube=0;
				velocity=0;
				dieSound.play();
				dieSound.setLooping(id,true);




			}
		}
		font.draw(batch,String.valueOf(score),100,200);
        batch.draw(birds[flapstate], Gdx.graphics.getWidth() / 2 - birds[flapstate].getWidth() / 2, birdY);
        batch.end();
		birdShape.set(Gdx.graphics.getWidth()/2,birdY+birds[flapstate].getHeight()/2,birds[flapstate].getWidth()/2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdShape.x,birdShape.y,birdShape.radius);
        for(int i=0;i<numberOftube;i++) {
         //   shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOfset[i],toptube.getWidth(),toptube.getHeight());
           // shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeOfset[i],bottomtube.getWidth(),bottomtube.getHeight());
       		if(Intersector.overlaps(birdShape,toptubeRectangle[i]) || Intersector.overlaps(birdShape,bottomtubeRectangle[i])){
       			//Gdx.app.log("Overlap ", " YESSSSSSSSSSSSSS");
				gameState=2;
			}
        }
		//shapeRenderer.end();



	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bg.dispose();
		bgmusic.dispose();
	}
}
