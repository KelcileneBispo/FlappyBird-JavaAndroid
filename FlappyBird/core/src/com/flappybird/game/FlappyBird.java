package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
		private SpriteBatch batch;
		private Texture[] passaros;
		private Texture fundo;
		private Texture canoBaixo;
		private Texture canoTopo;
		private Texture gameOver;
		private Random numeroRandomico;
		private BitmapFont fonte;
		private BitmapFont mensagem;
		private Circle passaroCirculo;
		private Rectangle retanguloCanoTopo;
		private Rectangle retanguloCanoBaixo;



		//Atributos de configuracao

		private float larguraDispositivo;
		private float alturaDispositivo;
		private int estadoJogo = 0;
		private int pontuacao = 0;

		private float variacao = 0;
		private float velocidadeQueda = 0;
		private float posicaoInicialVertical;
		private float posicaoMovimentoCanoHorizontal;
		private float espacosEntreCanos;
		private float deltaTime;
		private float alturaEntreCanosRandomica;
		private boolean marcouPonto;

		//Cameras
		private OrthographicCamera camera;
		private Viewport viewport;
		private final float VIRTUAL_WIDTH = 760;
		private final float VIRTUAL_HEIGHT = 1024;

	@Override
	public void create () {

		batch = new SpriteBatch();
		numeroRandomico = new Random();
		passaroCirculo = new Circle();

		fonte = new  BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);

		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(3);

		passaros = new Texture[3];
		passaros[0] = new Texture("passaro1.png");
		passaros[1] = new Texture("passaro2.png");
		passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");


		//*********Configurando camera*********
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT /2 , 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVertical = alturaDispositivo / 2;
		posicaoMovimentoCanoHorizontal = larguraDispositivo - 100;
		espacosEntreCanos = 300;
	}

	@Override
	public void render () {

		camera.update();

		//limpeza do frame
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

		deltaTime = Gdx.graphics.getDeltaTime();
		variacao += deltaTime * 10;
		if (variacao > 2) variacao = 0;

		if(estadoJogo == 0){  // O jogo nao esta iniciado

			if (Gdx.input.justTouched()){
				estadoJogo = 1;
			}


		}else { // jogo iniciado

            //velocidade da queda do passaro e n??o deixa o passaro cair da tela
		    velocidadeQueda++;

            if (posicaoInicialVertical > 30 || velocidadeQueda < 0)
                posicaoInicialVertical =    posicaoInicialVertical - velocidadeQueda;

		    if(estadoJogo == 1){

				posicaoMovimentoCanoHorizontal -= deltaTime * 320;

				if (Gdx.input.justTouched()) {
					velocidadeQueda = -20;
				}

				//verifica se o cano saiu totalmente da tela
				if (posicaoMovimentoCanoHorizontal < -canoTopo.getHeight()) {
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
					alturaEntreCanosRandomica = numeroRandomico.nextInt(400) - 200;
					marcouPonto = false;
				}

				//verifica a pontuacao
				if (posicaoMovimentoCanoHorizontal < 120){
					if (!marcouPonto){
						pontuacao++;
						marcouPonto = true;
					}
				}

            }else{// Tela GameOver

				if(Gdx.input.justTouched()){

					estadoJogo = 0;
					pontuacao = 0;
					velocidadeQueda = 0;
					posicaoInicialVertical = alturaDispositivo / 2;
					posicaoMovimentoCanoHorizontal = larguraDispositivo;
				}
			}

		}

		//Configurando dados de projecao camera
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		batch.draw(fundo,0,0,larguraDispositivo ,alturaDispositivo );
		batch.draw(canoTopo, posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 + espacosEntreCanos /2 + alturaEntreCanosRandomica);
		batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacosEntreCanos /2 + alturaEntreCanosRandomica);
		batch.draw(passaros [(int)variacao], 120, posicaoInicialVertical);
		fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

		if(estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
			mensagem.draw(batch, "Toque para reiniciar !!",larguraDispositivo / 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2 );
		}

		batch.end();

		passaroCirculo.set(120 + passaros[0].getHeight() / 2,
				posicaoInicialVertical + passaros[0].getHeight() /2,
				passaros[0].getWidth() / 2);

		retanguloCanoBaixo = new Rectangle(
			posicaoMovimentoCanoHorizontal,
			alturaDispositivo / 2 - canoBaixo.getHeight() - espacosEntreCanos /2 + alturaEntreCanosRandomica,
			canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		retanguloCanoTopo = new Rectangle(
				posicaoMovimentoCanoHorizontal,
				alturaDispositivo / 2 + espacosEntreCanos /2 + alturaEntreCanosRandomica,
				canoTopo.getWidth(), canoTopo.getHeight()
		);


		//Testa a colisao

		if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || (Intersector.overlaps(passaroCirculo, retanguloCanoTopo))
				|| posicaoInicialVertical <= 30 || posicaoInicialVertical >= alturaDispositivo ){
			estadoJogo = 2;
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
}
