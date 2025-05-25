package application;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

	public static void main(String[] args) {
        launch(args);
    }
	
	private final int WINDOW_WIDTH = 1200;
	private final int WINDOW_HEIGHT = 800;
	
	VBox girisEkranRoot = new VBox();
	
	Scene mainScene = new Scene(girisEkranRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
	
	private DoktorDAO doktorDAO = new DoktorDAO();
	
	private CreateCell createCell = new CreateCell();
	
	private File droppedImageFile;
    
    private Image image;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
        Button hastaGirisButonu = new Button("Hasta girişi");
        	
        hastaGirisButonu.setOnAction(e -> HastaEkranlari.hastaGirisEkraniGoster(primaryStage, mainScene));
        	
        Button doktorGirisButonu = new Button("Doktor girişi");
        	
        doktorGirisButonu.setOnAction(e -> DoktorEkranlari.doktorGirisEkraniGoster(primaryStage, mainScene));
        
        Button yeniDoktor = new Button("Sisteme yeni doktor ekle");
        
        yeniDoktor.setOnAction(e -> doktorEkle());
        	
        girisEkranRoot.setSpacing(20);
        	
        girisEkranRoot.getChildren().addAll(hastaGirisButonu, doktorGirisButonu, yeniDoktor);
        	
        girisEkranRoot.setScaleX(3);
        girisEkranRoot.setScaleY(3);
        girisEkranRoot.setAlignment(Pos.CENTER);
        
        primaryStage.setScene(mainScene);
        primaryStage.show();
	}
	
	private void doktorEkle() {

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
				Doktor doktor = new Doktor(tcKimlik.getText(), sifre.getText(), ad.getText(), soyad.getText(), Date.valueOf(datePicker.getValue()), eposta.getText(), 
						image,cbCinsiyet.getValue());

				doktorDAO.doktorEkle(doktor);
				MailSender.sendEmail(doktor);
				outPutLabel.setStyle("-fx-text-fill: green;");
				outPutLabel.setText("Başarılı!");
			} catch(SQLException ee) {
				outPutLabel.setStyle("-fx-text-fill: red;");
				outPutLabel.setText(createCell.textFormatter("Bu doktor daha önce kaydedilmiş!"));
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
		inputStage.setTitle("Yeni Doktor Ekleme");
		inputStage.show();
	}		
	
}
