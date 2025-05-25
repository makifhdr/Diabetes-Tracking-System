package application;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DoktorEkranlari {
		
	private static final int WINDOW_WIDTH = 1200;
	private static final int WINDOW_HEIGHT = 800;
	
	private static HastaDAO hastaDAO = new HastaDAO();
	
	private static DoktorDAO doktorDAO = new DoktorDAO();
	
	private static CreateCell createCell = new CreateCell();
	
	public static void doktorGirisEkraniGoster(Stage primaryStage, Scene mainScene) {
			
			VBox hastaEkranRoot = new VBox();
			
			Label kullaniciAdiLabel = new Label("Doktor T.C. Kimlik:");
	    	
	    	TextField kullaniciAdiField = new TextField();
	    	
	    	kullaniciAdiField.setMaxWidth(200);
	    	
	    	Label sifreLabel = new Label("Şifre:");
	    	
	    	TextField sifreField = new TextField();
	    	
	    	sifreField.setMaxWidth(200);
	    	
	    	Button girisButonu = new Button("Giriş");
	    	
	    	girisButonu.setOnAction(e -> doktorGiris(kullaniciAdiField.getText(), sifreField.getText(), primaryStage));
	    	
	    	Button geriButonu = new Button("Geri");
	    	
	    	geriButonu.setOnAction(e -> primaryStage.setScene(mainScene));
			
	    	hastaEkranRoot.getChildren().addAll(kullaniciAdiLabel, kullaniciAdiField, sifreLabel, sifreField, girisButonu, geriButonu);
	    	
	    	hastaEkranRoot.setScaleX(3);
	    	hastaEkranRoot.setScaleY(3);
	    	hastaEkranRoot.setAlignment(Pos.CENTER);
	    	
	    	Scene doktorLogin = new Scene(hastaEkranRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
	    	
	    	primaryStage.setScene(doktorLogin);
		}

	private static void doktorGiris(String tc_kimlik, String sifre, Stage primaryStage) {
		if(doktorDAO.girisYap(tc_kimlik, sifre)) {
			Alert alert = new Alert(AlertType.INFORMATION);
	
			alert.setTitle("Giriş Başarılı!");
			alert.setHeaderText("Devam etmek için butona basın");
		    alert.showAndWait();
		    
		    doktorEkrani(doktorDAO.getDoktor(tc_kimlik), primaryStage);
		}else {
			Alert alert = new Alert(AlertType.ERROR);
			
			alert.setTitle("Giriş Hatası");
		    alert.setHeaderText("Kullanıcı adı veya şifre hatalı.\nLütfen bilgilerinizi kontrol edin ve tekrar deneyin.");
		    alert.showAndWait();
		}
	}
	
	private static File droppedImageFile;
    
    private static Image image;
    
	
    private static void doktorEkrani(Doktor doktor, Stage primaryStage) {
		
		Button hastaEkle = new Button("Yeni hasta ekle");
		
		String doktor_tc = doktor.getTc_kimlik_no();

		GridPane grid = new GridPane();
                
        Date now = Date.valueOf(LocalDateTime.now().toLocalDate());
		
		hastaEkle.setOnAction(e -> {
			Stage inputStage = new Stage();
			
			Label outPutLabel = new Label();
			
			TextField tcKimlik = new TextField();
			Label tcKimlikLabel = new Label("T.C. Kimlik:");
			tcKimlik.setMaxWidth(200);
			
			TextField sifre = new TextField();
			Label sifreLabel = new Label("Şifre:");
			sifre.setMaxWidth(200);
			
			TextField ad = new TextField();
			Label adLabel = new Label("Ad:");
			ad.setMaxWidth(200);
			
			TextField soyad = new TextField();
			Label soyadLabel = new Label("Soyad:");
			soyad.setMaxWidth(200);
						
			TextField eposta = new TextField();
			Label epostaLabel = new Label("E-posta:");
			eposta.setMaxWidth(200);
			
			ImageView imageView = new ImageView();
	        imageView.setFitWidth(200);
	        imageView.setFitHeight(200);
	        imageView.setPreserveRatio(true);

	        StackPane dropArea = new StackPane();
	        dropArea.setMaxWidth(200);
	        dropArea.setMaxHeight(200);
	        dropArea.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
	        Label dropLabel = new Label("Fotoğrafı buraya sürükleyin");
	        dropArea.getChildren().add(dropLabel);
            dropArea.getChildren().add(imageView);

	        dropArea.setOnDragOver(event -> {
	            if (event.getGestureSource() != dropArea &&
	                event.getDragboard().hasFiles()) {
	                event.acceptTransferModes(TransferMode.COPY);
	            }
	            event.consume();
	        });

	        dropArea.setOnDragDropped(event -> {
	            Dragboard db = event.getDragboard();
	            if (db.hasFiles()) {
	                droppedImageFile = db.getFiles().get(0);
	                if (droppedImageFile.exists() && droppedImageFile.canRead()) {
	                    image = new Image(droppedImageFile.toURI().toString(), false);
	                    imageView.setImage(image);
	                }
	            }
	            event.setDropCompleted(true);
	            event.consume();
	        });
			
			ComboBox<Cinsiyet> cbCinsiyet = new ComboBox<>();
			cbCinsiyet.getItems().addAll(Cinsiyet.values());
			cbCinsiyet.setPromptText("Cinsiyet Seçin");
			
			Button ekleButon = new Button("Onayla");
			
			DatePicker datePicker = new DatePicker();
			datePicker.setPromptText("Doğum tarihi");
            
			ekleButon.setOnAction(e1 -> {
				
				try {
					Hasta hasta = new Hasta(tcKimlik.getText(), sifre.getText(), ad.getText(), soyad.getText(), Date.valueOf(datePicker.getValue()), eposta.getText(), 
							image, cbCinsiyet.getValue());

					hastaDAO.hastaEkle(hasta, doktor);
					MailSender.sendEmail(hasta);
					outPutLabel.setStyle("-fx-text-fill: green;");
					outPutLabel.setText("Başarılı!");
					
					grid.getChildren().clear();
					createCell.createCells(grid, doktorDAO.getDoktorHastalari(doktor_tc), Date.valueOf(now.toLocalDate()));
				} catch(SQLException ee) {
					outPutLabel.setStyle("-fx-text-fill: red;");
					outPutLabel.setText(createCell.textFormatter("Bu hasta daha önce kaydedilmiş!"));
					ee.printStackTrace();
				}
				catch (Exception e2) {
					outPutLabel.setStyle("-fx-text-fill: red;");
					outPutLabel.setText(createCell.textFormatter("Lütfen tüm bilgileri doğru girdiğinizden emin olun!"));
				}
			});
			
			VBox labelVBox = new VBox(tcKimlikLabel, sifreLabel, adLabel, soyadLabel, epostaLabel);			
			labelVBox.setAlignment(Pos.CENTER);
			labelVBox.setSpacing(10);
			
			VBox textFieldVBox = new VBox(tcKimlik, sifre, ad, soyad, eposta);
			textFieldVBox.setAlignment(Pos.CENTER);
			
			VBox otherVBox = new VBox(datePicker, cbCinsiyet, ekleButon, outPutLabel);
			otherVBox.setAlignment(Pos.CENTER);
			
			HBox ekleRoot = new HBox(dropArea, labelVBox, textFieldVBox, otherVBox);
			
			ekleRoot.setAlignment(Pos.CENTER);
			ekleRoot.setScaleX(1.5);
			ekleRoot.setScaleY(1.5);
			ekleRoot.setSpacing(10);
			
			inputStage.setScene(new Scene(ekleRoot, WINDOW_WIDTH, WINDOW_HEIGHT));
			inputStage.setTitle("Yeni Hasta Ekleme");
			inputStage.show();
		});
		hastaEkle.setScaleX(1.1);
		hastaEkle.setScaleY(1.1);
		
		//abcd123
		grid.setHgap(16.5);
		grid.setVgap(10);
		
		List<Hasta> hastalar = doktorDAO.getDoktorHastalari(doktor_tc);
		
		createCell.createCells(grid, hastalar, Date.valueOf(now.toLocalDate()));
		
		grid.setAlignment(Pos.CENTER);
		ScrollPane scrollPane = new ScrollPane(grid);
		scrollPane.setPrefWidth(1000);
		
		Button yenileButonu = new Button("Yenile");
		
		yenileButonu.setOnAction(ee ->{
			grid.getChildren().clear();
			createCell.createCells(grid, doktorDAO.getDoktorHastalari(doktor_tc), Date.valueOf(now.toLocalDate()));
		});
		yenileButonu.setScaleX(1.1);
		yenileButonu.setScaleY(1.1);
		
		Label clockLabel = new Label();
        clockLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: white;");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e2 -> {
                	LocalDateTime now2 = LocalDateTime.now();
                    clockLabel.setText(now2.format(formatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        Button cikisButon = new Button("Çıkış");
		
		cikisButon.setOnAction(e ->{
			primaryStage.close();
		});
		
		Label header = new Label("Hastalarım");
		header.setStyle("-fx-text-fill: white;");
		header.setScaleX(1.1);
		header.setScaleY(1.1);
        
        Label headerLabel = new Label("Hoşgeldiniz Dr. " + doktor.getAd() + " " + doktor.getSoyad());
        headerLabel.setStyle("-fx-text-fill: white;");
        headerLabel.setScaleX(1.1);
        headerLabel.setScaleY(1.1);
        
        HBox topRightBox = new HBox( clockLabel);
        topRightBox.setAlignment(Pos.TOP_RIGHT);
        topRightBox.setStyle("-fx-background-color: #333;");
        topRightBox.setPadding(new Insets(5, 10, 5, 10));
        
        HBox butonlarHBox = new HBox(hastaEkle, yenileButonu, cikisButon);
        butonlarHBox.setSpacing(20);
        butonlarHBox.setAlignment(Pos.CENTER);

		grid.setStyle("-fx-background-color: gray;");
		scrollPane.setStyle("-fx-background-color: gray;");
		VBox root = new VBox(topRightBox, headerLabel, header, scrollPane, butonlarHBox);
		root.setStyle("-fx-background-color: gray;");
		root.setAlignment(Pos.CENTER);
		
		Scene doktorScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		primaryStage.setScene(doktorScene);
	}
}
