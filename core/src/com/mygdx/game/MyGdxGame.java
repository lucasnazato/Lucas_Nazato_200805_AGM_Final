package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	Texture img;

	// Variaveis para todas as texturas a serem usadas no jogo
	private SpriteBatch batch;
	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Texture telaInicial;
	private Texture moedaDourada;
	private Texture moedaPrata;

	// Colisores do jogo
	private ShapeRenderer shapeRenderer;
	private Circle circuloPassaro;
	private Rectangle retanguloCanoCima;
	private Rectangle retanguloCanoBaixo;
	private Circle circuloMoedaDourada;
	private Circle circuloMoedaPrata;

	// Definir variaveis de posicao, pontuacao etc
	private float larguraDispositivo;
	private float alturaDispositivo;
	private float variacao = 0;
	private float gravidade = 2;
	private float posicaoInicialVerticalPassaro = 0;
	private float posicaoCanoHorizontal;
	private float posicaoCanoVertical;
	private float espacoEntreCanos;
	private Random random;
	private int pontos = 0;
	private int pontuacaoMaxima = 0;
	private boolean passouCano = false;
	private int estadoJogo = 0;
	private float posicaoHorizontalPassaro = 0;
	private float posicaoHorizontalMoedaDourada = 0;
	private float posicaoVerticalMoedaDourada = 0;
	private float posicaoHorizontalMoedaPrata = 0;
	private float posicaoVerticalMoedaPrata = 0;

	// Criar fontes para os textos do jogo
	BitmapFont textoPontuacao;
	BitmapFont textoReiniciar;
	BitmapFont textoMelhorPontuacao;

	// Criar sons para o jogo
	Sound somVoando;
	Sound somColisao;
	Sound somPontuacao;

	// Preferencias para guardar as preferencias
	Preferences preferencias;

	// Criar camera virtual do jogo
	private OrthographicCamera camera;
	private Viewport viewport;
	private final float VIRTUAL_WIDTH = 720;
	private final float VIRTUAL_HEIGHT = 1280;

	// Chamar os metodos para inicializar as texturas e objetos
	@Override
	public void create () {

		inicializarTexturas();
		inicializarObjetos();
	}

	// Chama todos os metodos que precisam rodar durante o jogo
	@Override
	public void render () {

		// Limpar as texturas
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Chamar os metodos que precisam rodar continuamente
		verificarEstadoJogo();
		validarPontos();
		desenharTexturas();
		detectarColisoes();
	}

	// Descarta os recursos do batch e img
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	// Mudar o tamanho para ficar do tamanho da tela
	@Override
	public void resize(int width, int height){
		viewport.update(width, height);
	}

	// Inicializar as texturas a serem utilizadas no jogo
	private void inicializarTexturas() {
		// Array com as texturas do player
		passaros = new Texture[3];
		passaros[0] = new Texture("angrybird_1.png");
		passaros[1] = new Texture("angrybird_2.png");
		passaros[2] = new Texture("angrybird_1.png");

		// textura dos objetos do jogo
		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo_maior.png");
		canoTopo = new Texture("cano_topo_maior.png");
		gameOver = new Texture("game_over.png");
		telaInicial = new Texture("AngryBird_screen.png");
		moedaDourada = new Texture("goldcoin.png");
		moedaPrata = new Texture("silvercoin.png");
	}

	// Inicializar os objetos a serem usados no jogo
	private void inicializarObjetos() {
		batch = new SpriteBatch();
		random = new Random();

		// Definir a largura e altura do dispositivo, posicao do passaro e do cano e espaco entre os canos
		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;
		posicaoInicialVerticalPassaro = alturaDispositivo / 2;
		posicaoCanoHorizontal = larguraDispositivo;
		espacoEntreCanos = 350;

		// Definir posicao da moeda dourada
		posicaoHorizontalMoedaDourada = random.nextInt((int) larguraDispositivo / 2);
		posicaoVerticalMoedaDourada = random.nextInt((int) alturaDispositivo);

		// Definir posicao da moeda prata
		posicaoHorizontalMoedaPrata = random.nextInt((int) larguraDispositivo / 2);
		posicaoVerticalMoedaPrata = random.nextInt((int) alturaDispositivo);

		// Inicializar os textos de pontuacao
		textoPontuacao = new BitmapFont();
		textoPontuacao.setColor(Color.WHITE);
		textoPontuacao.getData().setScale(10);

		// Inicializar os textos de reiniciar
		textoReiniciar = new BitmapFont();
		textoReiniciar.setColor(Color.GREEN);
		textoReiniciar.getData().setScale(2);

		// Inicializar os textos de melhor pontuacao
		textoMelhorPontuacao = new BitmapFont();
		textoMelhorPontuacao.setColor(Color.RED);
		textoMelhorPontuacao.getData().setScale(2);

		// Inicializar os colisores do passaro, cano e moedas
		shapeRenderer = new ShapeRenderer();
		circuloPassaro = new Circle();
		retanguloCanoBaixo = new Rectangle();
		retanguloCanoCima = new Rectangle();
		circuloMoedaDourada = new Circle();
		circuloMoedaPrata = new Circle();

		// Inicializar os sons
		somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
		somColisao = Gdx.audio.newSound(Gdx.files.internal("som_batida.wav"));
		somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

		// Criar preferencias
		preferencias = Gdx.app.getPreferences("flappyBird");
		pontuacaoMaxima = 	preferencias.getInteger("pontuacaoMaxima", 0);

		// Inicializar nova camera
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
	}

	// Metodo para verificar em qual estado o jogo esta
	private void verificarEstadoJogo() {

		// Variavel para verificar se o player tocou na tela
		boolean toqueTela = Gdx.input.justTouched();

		// Se o jogador estiver no estado 0 ("idle") e tocar na tela iniciar o jogo, adicionar altura e passar para o estado 1
		if (estadoJogo == 0) {
			if (toqueTela) {
				gravidade = -15;
				estadoJogo = 1;
				somVoando.play();
			}
		// Se o jogador estiver no estado 1...
		}else if(estadoJogo == 1) {
			// Se o jogador tocar na tela adicionar altura e tocar som
			if (toqueTela) {
				gravidade = -15;
				somVoando.play();
			}

			// Movimentar o cano na horizontal
			posicaoCanoHorizontal -= Gdx.graphics.getDeltaTime() * 200;

			// Se o cano sair da tela, voltar o cano para o outro lado e variar a altura dele
			if (posicaoCanoHorizontal < -canoTopo.getWidth()) {
				posicaoCanoHorizontal = larguraDispositivo;
				posicaoCanoVertical = random.nextInt(400) - 200;
				passouCano = false;
			}

			// Se o passaro estiver dentro da tela e tocar na tela, mudar a sua posicao vertical
			if (posicaoInicialVerticalPassaro > 0 || toqueTela) {
				posicaoInicialVerticalPassaro = posicaoInicialVerticalPassaro - gravidade;
				gravidade++;
			}

			// Movimentar a moeda dourada na horizontal
			posicaoHorizontalMoedaDourada -= Gdx.graphics.getDeltaTime() * 200;

			// Se a moeda sair da tela, mudar a posicao da moeda para um ponto aleatorio
			if (posicaoHorizontalMoedaDourada <= -moedaDourada.getWidth()){
				posicaoHorizontalMoedaDourada = larguraDispositivo;
				posicaoVerticalMoedaDourada = random.nextInt((int) alturaDispositivo);
			}

			// Movimentar a moeda prata na horizontal
			posicaoHorizontalMoedaPrata -= Gdx.graphics.getDeltaTime() * 200;

			// Se a moeda sair da tela, mudar a posicao da moeda para um ponto aleatorio
			if (posicaoHorizontalMoedaPrata <= -moedaPrata.getWidth()){
				posicaoHorizontalMoedaPrata = larguraDispositivo;
				posicaoVerticalMoedaPrata = random.nextInt((int) alturaDispositivo);
			}

		// Se acontecer um game over, atualizar  a pontuacao maxima e mudar a posicao do personagem
		}else if (estadoJogo == 2){
			if (pontos > pontuacaoMaxima){
				pontuacaoMaxima = pontos;
				preferencias.putInteger("pontuacaoMaxima", pontuacaoMaxima);
				preferencias.flush();
			}
			posicaoHorizontalPassaro -= Gdx.graphics.getDeltaTime()*500;

			// Se tocar na tela, voltar para o estado 0 (resetar as variaveis e reiniciar o jogo)
			if (toqueTela){
				estadoJogo = 0;
				pontos = 0;
				gravidade = 0;
				posicaoHorizontalPassaro = 0;
				posicaoInicialVerticalPassaro = alturaDispositivo / 2;
				posicaoCanoHorizontal = larguraDispositivo;
			}
		}
	}

	// Metodo para dar pontos ao player quando passar pelos canos
	private void validarPontos() {

		// Se o player passar pelo cano, aumentar a pontuacao
		if (posicaoCanoHorizontal < 50 - passaros[0].getWidth()){
			if (!passouCano){
				pontos++;
				passouCano = true;
				somPontuacao.play();
			}
		}

		variacao = Gdx.graphics.getDeltaTime() + 10;

		// Varivel para escolher qual sprite do player sera desenhada na tela
		if (variacao > 3)
			variacao = 0;
	}

	// Desenhar todas as texturas do jogo
	private void desenharTexturas() {

		// Definir a matrix usada pelo batch
		batch.setProjectionMatrix(camera.combined);

		// Iniciar o batch para desenhar as texturas
		batch.begin();

		// Desenhar oos itens do jogo (tela de fundo, player, canos, texto de pontuacao e moedas)
		batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
		batch.draw(passaros[ (int) variacao],
			50 + posicaoHorizontalPassaro, posicaoInicialVerticalPassaro);
		batch.draw(canoBaixo, posicaoCanoHorizontal,
			alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical);
		batch.draw(canoTopo, posicaoCanoHorizontal,
			alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical);
		textoPontuacao.draw(batch, String.valueOf(pontos), larguraDispositivo / 2,
				alturaDispositivo - 110);
		batch.draw(moedaDourada, posicaoHorizontalMoedaDourada, posicaoVerticalMoedaDourada);
		batch.draw(moedaPrata, posicaoHorizontalMoedaPrata, posicaoVerticalMoedaPrata);

		// Desenhar a tela de inicio
		if (estadoJogo == 0){
			batch.draw(telaInicial, 0, 0, larguraDispositivo, alturaDispositivo);
		}

		// Desenha a tela de game over e texto instruindo o player
		if (estadoJogo == 2){
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth()/2,
					alturaDispositivo / 2);
			textoReiniciar.draw(batch,
					"Toque para reiniciar", larguraDispositivo / 2 - 140,
					alturaDispositivo / 2 - gameOver.getHeight() / 2);
			textoMelhorPontuacao.draw(batch,
					"Seu recorde é: "+pontuacaoMaxima+" pontos",
					larguraDispositivo / 2 - 140, alturaDispositivo / 2 - gameOver.getHeight());
		}

		// Finalizar o batch
		batch.end();
	}

	// Detectar as colisões do jogo
	private void detectarColisoes() {

		// Circulo de colisao para o player
		circuloPassaro.set(
				50 + posicaoHorizontalPassaro + passaros[0].getWidth() / 2,
				posicaoInicialVerticalPassaro + passaros[0].getHeight() / 2,
				passaros[0].getWidth() / 2
		);

		// Retangulo de colisao para o cano de cima
		retanguloCanoBaixo.set(
				posicaoCanoHorizontal,
				alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + posicaoCanoVertical,
				canoBaixo.getWidth(), canoBaixo.getHeight()
		);

		// Retangulo de colisao para o cano de baixo
		retanguloCanoCima.set(
				posicaoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + posicaoCanoVertical,
				canoTopo.getWidth(), canoTopo.getHeight()
		);


		// Circulo de colisao da moeda dourada
		circuloMoedaDourada.set(
				posicaoHorizontalMoedaDourada + moedaDourada.getWidth() / 2,
				posicaoVerticalMoedaDourada + moedaDourada.getHeight() / 2,
				moedaDourada.getWidth() / 2
		);

		// Circulo de colisao da moeda prata
		circuloMoedaPrata.set(
				posicaoHorizontalMoedaPrata + moedaPrata.getWidth() / 2,
				posicaoVerticalMoedaPrata + moedaPrata.getHeight() / 2,
				moedaPrata.getWidth() / 2
		);

		// Variaveis bool para detectar colisões entre o player e os canos e moedas
		boolean colidiuCanoCima = Intersector.overlaps(circuloPassaro, retanguloCanoCima);
		boolean colidiuCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);
		boolean colidiuMoedaDourada = Intersector.overlaps(circuloPassaro, circuloMoedaDourada);
		boolean colidiuMoedaPrata = Intersector.overlaps(circuloPassaro, circuloMoedaPrata);

		// Se o player colidir com o cano de cima ou de baixo e se estiver no estado 1, tocar som de colisao e ir para o estado 2
		if (colidiuCanoCima || colidiuCanoBaixo){
			if (estadoJogo == 1){
				somColisao.play();
				estadoJogo = 2;
			}
		}

		// Se colidir com a moeda prata, tocar som de colisao, somar 5 pontos e escolher uma novo localizaçao para a moeda
		if (colidiuMoedaPrata){
			pontos += 5;
			somPontuacao.play();

			posicaoHorizontalMoedaPrata = larguraDispositivo;
			posicaoVerticalMoedaPrata = random.nextInt((int) alturaDispositivo);
		}

		// Se colidir com a moeda dourada, tocar som de colisao, somar 10 pontos e escolher uma novo localizaçao para a moeda
		if (colidiuMoedaDourada){
			pontos += 10;
			somPontuacao.play();

			posicaoHorizontalMoedaDourada = larguraDispositivo;
			posicaoVerticalMoedaDourada = random.nextInt((int) alturaDispositivo);
		}
	}
}
