package application;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HastaEkranlari {
	
	private static final int WINDOW_WIDTH = 1200;
	private static final int WINDOW_HEIGHT = 800;
	
	private static HastaDAO hastaDAO = new HastaDAO();
	
	private static BelirtiDAO belirtiDAO = new BelirtiDAO();
	
	private static DiyetDAO diyetDAO = new DiyetDAO();
	
	private static EgzersizDAO egzersizDAO = new EgzersizDAO();
	
	private static OlcumDAO olcumDAO = new OlcumDAO();
	
	private static InsulinDAO insulinDAO = new InsulinDAO();
	
	private static CreateCell createCell = new CreateCell();
	
	private static OneriHesaplama oneriHesaplama = new OneriHesaplama();
		
	public static void hastaGirisEkraniGoster(Stage primaryStage, Scene mainScene) {
			
			VBox hastaEkranRoot = new VBox();
			
			Label kullaniciAdiLabel = new Label("Hasta T.C. Kimlik:");
	    	
	    	TextField kullaniciAdiField = new TextField();
	    	
	    	kullaniciAdiField.setMaxWidth(200);
	    	
	    	Label sifreLabel = new Label("Şifre:");
	    	
	    	TextField sifreField = new TextField();
	    	
	    	sifreField.setMaxWidth(200);
	    	
	    	Button girisButonu = new Button("Giriş");
	    	
	    	girisButonu.setOnAction(e -> hastaGiris(kullaniciAdiField.getText(), sifreField.getText(), primaryStage));
	    	
	    	Button geriButonu = new Button("Geri");
	    	
	    	geriButonu.setOnAction(e -> primaryStage.setScene(mainScene));
			
	    	hastaEkranRoot.getChildren().addAll(kullaniciAdiLabel, kullaniciAdiField, sifreLabel, sifreField, girisButonu, geriButonu);
	    	
	    	hastaEkranRoot.setScaleX(3);
	    	hastaEkranRoot.setScaleY(3);
	    	hastaEkranRoot.setAlignment(Pos.CENTER);
	    	
	    	Scene hastaLogin = new Scene(hastaEkranRoot, WINDOW_WIDTH, WINDOW_HEIGHT);
	    	
	    	primaryStage.setScene(hastaLogin);
		}
	
	private static void hastaGiris(String tc_kimlik, String sifre, Stage primaryStage) {
			
			if(hastaDAO.girisYap(tc_kimlik, sifre)) {
				Alert alert = new Alert(AlertType.INFORMATION);
				
				alert.setTitle("Giriş Başarılı!");
				alert.setHeaderText("Devam etmek için butona basın");
			    alert.showAndWait();
			    
			    hastaEkrani(hastaDAO.getHasta(tc_kimlik), primaryStage);
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				
				alert.setTitle("Giriş Hatası");
				alert.setHeaderText("Kullanıcı adı veya şifre hatalı.\nLütfen bilgilerinizi kontrol edin ve tekrar deneyin.");
			    alert.showAndWait();
			}
			
		}
	
	private static void hastaEkrani(Hasta hasta, Stage primaryStage) {

		//asdqwe23
		
		String hasta_tc = hasta.getTc_kimlik_no();
		
		Button kanSekeriGirButon = new Button("Kan şekeri ölçümü gir");
		
		kanSekeriGirButon.setOnAction(e ->{
			Stage inputStage = new Stage();

			DatePicker datePicker = new DatePicker();
			datePicker.setPromptText("Tarih giriniz");
			
	        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, 12);
	        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, 0);

	        hourSpinner.setEditable(true);
	        minuteSpinner.setEditable(true);

	        Button btn = new Button("Ölçümü onayla");
	        Label outputLabel = new Label();
	        
	        TextField miktarField = new TextField();
	        miktarField.setPromptText("Ölçüm miktarını yazınız");

	        btn.setOnAction(ev -> {
	        	try {
					Double olcumMiktari = Double.parseDouble(!miktarField.getText().isEmpty() ? miktarField.getText() : null);
					Date date = Date.valueOf(datePicker.getValue()); 
					int hour = hourSpinner.getValue();
					
					Olcum olcum = new Olcum(hasta, date, olcumMiktari, Olcum_Zamani.fromTimestamp(hour));
					
					boolean ekledi = olcumDAO.hastaOlcumEkle(hasta_tc, olcum);

					if (ekledi) {
					    
					    Alert alert = new Alert(AlertType.INFORMATION);
						
					    inputStage.close();
					    if(Olcum_Zamani.fromTimestamp(hour).equals(Olcum_Zamani.GECERSIZ)) {
					    	alert.setHeaderText("Ölçüm başarıyla eklendi\nNot: Girdiğiniz vakit, geçerli vakitlerin dışında");
					    }else {
							alert.setHeaderText("Ölçüm başarıyla eklendi");					    	
					    }
					    
					    alert.showAndWait();
					    
					}else if(!ekledi) {
						outputLabel.setStyle("-fx-text-fill: red;");
					    outputLabel.setText("Bu ölçüm vakti daha önce girilmiş!");
					}
					else {
						outputLabel.setStyle("-fx-text-fill: red;");
					    outputLabel.setText("Lütfen geçerli değerler girin.");
						}
				} catch (Exception e1) {
					outputLabel.setStyle("-fx-text-fill: red;");
				    outputLabel.setText("Lütfen geçerli değerler girin.");
				}
	        });

	        HBox timeBox = new HBox(10, new Label("Saat:"), hourSpinner, new Label("Dakika:"), minuteSpinner);
	        VBox root2 = new VBox(10, timeBox, miktarField, datePicker, btn, outputLabel);
	        root2.setStyle("-fx-padding: 20");
	        
	        inputStage.setScene(new Scene(root2));
	        inputStage.setTitle("Kan Şekeri Girme");
	        inputStage.show();
		});
		
		Label clockLabel = new Label();
        clockLabel.setStyle("-fx-font-size: 17px; -fx-text-fill: white;");
        clockLabel.setAlignment(Pos.TOP_RIGHT);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY HH:mm");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), e2 -> {
                	LocalDateTime now2 = LocalDateTime.now();
                    clockLabel.setText(now2.format(formatter));
                }),
                new KeyFrame(Duration.seconds(1)) // her saniye güncelle
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        HBox topRightBox = new HBox(clockLabel);
        topRightBox.setAlignment(Pos.CENTER_RIGHT);
        topRightBox.setStyle("-fx-background-color: #333;");
        topRightBox.setPadding(new Insets(5, 10, 5, 10));
        				
		Date now = Date.valueOf(LocalDateTime.now().toLocalDate());
		
		Label outputLabel = new Label();
		
		Diyet currentDiyet = diyetDAO.getCurrentDiyet(hasta_tc, now);
		
		Label diyetBilgi = new Label();
		
		Label diyetBildiri = new Label();
		
		if(diyetDAO.checkDiyetTakip(hasta_tc, now)) {
			diyetBildiri.setText(createCell.textFormatter("Bugün diyet bildirisi yaptınız."));
		}else {
			diyetBildiri.setText(createCell.textFormatter("Bugün diyet bildirisi yapmadınız."));
		}
		
		Button diyetUygulandi = null;
		
		if(currentDiyet != null) {
			diyetBilgi.setText("Şu an ki diyetiniz: " + currentDiyet.getTarihAraligi().toString() + "\n" + createCell.textFormatter(currentDiyet.getDiyet_Turu().getAciklama()));
			diyetUygulandi = new Button(createCell.textFormatter("Diyet bildirisinde bulun"));
			
			diyetUygulandi.setOnAction(e ->{
				outputLabel.setText("");
				
				Label header = new Label(createCell.textFormatter("Bugün doktorunuzun tanımladığı " + currentDiyet.getDiyet_Turu().toString() + " diyetini uyguladınız mı?"));
				
				Button evetButton = new Button("Evet");
				Button hayirButton = new Button("Hayır");
				
				evetButton.setOnAction(e2 -> {
					try {
						diyetDAO.hastaDiyetGuncelle(hasta_tc, now, true);
						outputLabel.setStyle("-fx-text-fill: green;");
						outputLabel.setText("Diyet başarıyla güncellendi");
					} catch (SQLException e1) {
						outputLabel.setStyle("-fx-text-fill: red;");
						outputLabel.setText("Diyet güncelleme işlemi başarısız");
						e1.printStackTrace();
					}
				});
				
				hayirButton.setOnAction(e2 -> {
					try {
						diyetDAO.hastaDiyetGuncelle(hasta_tc, now, false);
						outputLabel.setStyle("-fx-text-fill: green;");
						outputLabel.setText("Diyet başarıyla güncellendi");
					} catch (SQLException e1) {
						outputLabel.setStyle("-fx-text-fill: red;");
						outputLabel.setText("Diyet güncelleme işlemi başarısız");
						e1.printStackTrace();
					}
				});
				
				VBox root = new VBox(header, evetButton, hayirButton, outputLabel);
				root.setAlignment(Pos.CENTER);
				Stage inputStage = new Stage();
				inputStage.setScene(new Scene(root));
				inputStage.show();
			
			});
		}else {
			diyetBilgi.setText("Doktorunuz bugün için herhangi bir diyet tanımlamamış");
		}
		
		Egzersiz currentEgzersiz = egzersizDAO.getCurrentEgzersiz(hasta_tc, now);
		
		Label egzersizBilgi = new Label();
		
		Label egzersizBildiri = new Label();
		
		if(egzersizDAO.checkEgzersizTakip(hasta_tc, now)) {
			egzersizBildiri.setText("Bugün egzersiz bildirisi yaptınız.");
		}else {
			egzersizBildiri.setText("Bugün egzersiz bildirisi yapmadınız.");
		}
		
		Button egzersizUygulandi = null;
		
		if(currentEgzersiz != null) {
			egzersizBilgi.setText("Şu an ki egzersiziniz: " + currentEgzersiz.getTarihAraligi().toString() + "\n" + createCell.textFormatter(currentEgzersiz.getEgzersiz_Turu().getAciklama()));
			egzersizUygulandi = new Button(createCell.textFormatter("Egzersiz bildirisinde bulun"));
			
			egzersizUygulandi.setOnAction(e ->{
				outputLabel.setText("");
				
				Label header = new Label(createCell.textFormatter("Bugün doktorunuzun tanımladığı " + currentEgzersiz.getEgzersiz_Turu().toString() + " egzersizini uyguladınız mı?"));
				
				Button evetButton = new Button("Evet");
				Button hayirButton = new Button("Hayır");
				
				evetButton.setOnAction(e2 -> {
					try {
						egzersizDAO.hastaEgzersizGuncelle(hasta_tc, now, true);
						outputLabel.setStyle("-fx-text-fill: green;");
						outputLabel.setText("Egzersiz başarıyla güncellendi");
					} catch (SQLException e1) {
						outputLabel.setStyle("-fx-text-fill: red;");
						outputLabel.setText("Egzersiz güncelleme işlemi başarısız");
						e1.printStackTrace();
					}
				});
				
				hayirButton.setOnAction(e2 -> {
					try {
						egzersizDAO.hastaEgzersizGuncelle(hasta_tc, now, false);
						outputLabel.setStyle("-fx-text-fill: green;");
						outputLabel.setText("Egzersiz başarıyla güncellendi");
					} catch (SQLException e1) {
						outputLabel.setStyle("-fx-text-fill: red;");
						outputLabel.setText("Egzersiz güncelleme işlemi başarısız");
						e1.printStackTrace();
					}
				});
				
				VBox root = new VBox(header, evetButton, hayirButton, outputLabel);
				root.setAlignment(Pos.CENTER);
				Stage inputStage = new Stage();
				inputStage.setScene(new Scene(root));
				inputStage.show();
			
			});
		}else {
			egzersizBilgi.setText("Doktorunuz bugün için herhangi bir egzersiz tanımlamamış");
		}
		
		HBox olcumHBox = setOlcumGrid(hasta, now);
						
		Button cikisButon = new Button("Çıkış");
		
		cikisButon.setOnAction(e ->{
			primaryStage.close();
		});

        Label headerLabel = new Label("Hoşgeldiniz Sayın " + hasta.getAd() + " " + hasta.getSoyad());
		
		HBox hRoot = new HBox();
		
		if(diyetUygulandi != null) {
			hRoot.getChildren().addAll(diyetBilgi, diyetUygulandi, diyetBildiri);
		}else {
			hRoot.getChildren().add(diyetBilgi);
		}
		
		if(egzersizUygulandi != null) {
			hRoot.getChildren().addAll(egzersizBilgi, egzersizUygulandi, egzersizBildiri);
		}else {
			hRoot.getChildren().add(egzersizBilgi);
		}
		
		Label belirtiLabel = new Label();
		
		String belirtilerString = "Belirtileriniz:\n";
		
		List<Belirti> belirtiler = belirtiDAO.getHastaBelirtiler(hasta_tc);
		
		if(belirtiler.isEmpty()) {
			belirtilerString += "Doktorunuz herhangi bir belirti tanımlamamış.";
		}else {
			for(Belirti belirti : belirtiler) {
				belirtilerString += belirti.getBelirti_adi() + "\n";
			}
		}
		
		belirtiLabel.setText(belirtilerString);
				
		Button insulinOneri = new Button("Insülin önerisini gör");
		
		insulinOneri.setOnAction(e -> {
			List<Olcum> bugunOlcumler = olcumDAO.getHastaTarihOlcumleri(hasta_tc, now);
			StringBuilder text = new StringBuilder("Bugün insülin önerisi: " + (int)oneriHesaplama.getInsulinOnerisi(hasta_tc, now) + " mL");

			Set<Olcum_Zamani> girilenZamanlar = bugunOlcumler.stream()
			    .map(Olcum::getOlcum_Zamani)
			    .collect(Collectors.toSet());

			for (Olcum_Zamani zaman : Olcum_Zamani.values()) {
			    if (zaman != Olcum_Zamani.GECERSIZ && !girilenZamanlar.contains(zaman)) {
			        text.append("\nÖlçüm eksik! Ortalama alınırken ").append(zaman).append(" ölçümü hesaba katılmadı.");
			    }
			    else if(zaman != Olcum_Zamani.GECERSIZ) {
			    	text.append("\nOrtalama alınırken ").append(zaman).append(" ölçümü hesaba katıldı.");
			    }
			}
			
			if(bugunOlcumler.size() <= 3) {
				text.append("\nYetersiz veri! Ortalama hesaplaması güvenilir değildir.");
			}
			
			Button insulinGirisi = new Button("Insülin dozu girişi yap");
			
			Label labelInsulin = new Label(text.toString());
			
			VBox oneriRoot = new VBox(labelInsulin, insulinGirisi);
			oneriRoot.setAlignment(Pos.CENTER);
			
			insulinGirisi.setOnAction(e1 -> {
				TextField degerGirisi = new TextField();
				degerGirisi.setMaxWidth(200);
				
				Button onayla = new Button("Onayla");
				
				Label outputLabel2 = new Label();
				
				onayla.setOnAction(e23 ->{
					try {
						insulinDAO.insulinEkle(new Insulin(hasta_tc, now, Double.parseDouble(degerGirisi.getText())));
						outputLabel2.setStyle("-fx-text-fill: green;");
						outputLabel2.setText("Insülin ekleme başarılı");
					}catch(Exception e5) {
						outputLabel2.setStyle("-fx-text-fill: red;");
						outputLabel2.setText("Insülin ekleme başarısız");
						e5.printStackTrace();
					}
				});
				
				
				oneriRoot.getChildren().addAll(degerGirisi, onayla, outputLabel2);
			});
			
			Stage oneriStage = new Stage();
			oneriStage.setScene(new Scene(oneriRoot, 800, 400));
			oneriStage.show();
		});
		
		GridPane grid2 = new GridPane();
		
		HBox root2 = new HBox();
        
        List<Diyet> diyetler = diyetDAO.getHastaDiyetler(hasta_tc);
		
		if(!diyetler.isEmpty()) {
			Label tarihLabel = new Label("Tarih");
			tarihLabel.setId("cell");
			Label egzersizTurLabel = new Label("Diyet Türü");
			egzersizTurLabel.setId("cell");
			Label uygulandiLabel = new Label("Uygulandı mı?");
			uygulandiLabel.setId("cell");
			
			grid2.add(tarihLabel, 0, 0);
			grid2.add(egzersizTurLabel, 1, 0);
			grid2.add(uygulandiLabel, 2, 0);
			
			grid2.setHgap(20);
			grid2.setVgap(10);
			
			PieChart pieChart = new PieChart();
	        pieChart.setTitle("Diyet Uygulama Oranı");
			
	        int uygulanan = 0;
	        int uygulanmayan = 0;
	        
			for(Diyet diyet : diyetler) {
				
				LocalDate current = diyet.getTarihAraligi().getBaslangic().toLocalDate();
			    LocalDate end = diyet.getTarihAraligi().getBitis().toLocalDate();
		        
			    int index = 0;

			    while (!current.isAfter(end)) {
			        Date sqlDate = Date.valueOf(current);

			        int row = index % 10;
			        int column = index / 10;
					
			        Label evetLabel = new Label("Evet");
					evetLabel.setId("evetLabel");
					
					Label hayirLabel = new Label("Hayır");
					hayirLabel.setId("hayirLabel");
					
					Label tarihString = new Label(sqlDate.toString());
					tarihString.setId("cell");
					Label diyetTurString = new Label(diyet.getDiyet_Turu().toString());
					diyetTurString.setId("cell");

			        grid2.add(tarihString, column * 3, row + 1);
			        grid2.add(diyetTurString, column * 3 + 1, row + 1);
					grid2.add(diyetDAO.checkDiyetUygulandiTakip(hasta_tc, sqlDate) ? evetLabel : hayirLabel, column * 3 + 2, row + 1);
			        
			        if(diyetDAO.checkDiyetUygulandiTakip(hasta_tc, sqlDate))
			        	uygulanan++;
			        else
			        	uygulanmayan++;
			        
			        current = current.plusDays(1);
			        index++;
			    }
			}
			
			double toplam = uygulanan + uygulanmayan;
			
			pieChart.getData().addAll(
					new PieChart.Data("Uygulandı (%" + String.format("%.2f", ((double)uygulanan * 100.0 / toplam))  + ")", (double)uygulanan),
		            new PieChart.Data("Uygulanmadı (%" + String.format("%.2f", ((double)uygulanmayan * 100.0 / toplam))  + ")", (double)uygulanmayan)
		        );
			
			root2.getChildren().addAll(grid2, pieChart);
		}else {
			root2.getChildren().add(new Label("Diyet kaydı bulunamadı"));
		}
		
		List<Insulin> insulinler = insulinDAO.getInsulinler(hasta_tc);
		
		GridPane gridInsulin = new GridPane();
		gridInsulin.setHgap(20);
		gridInsulin.setVgap(10);
		
		if(!insulinler.isEmpty()) {
			Label tarihLabel = new Label("Tarih");
			tarihLabel.setId("cell");
			Label degerLabel = new Label("Miktar");
			degerLabel.setId("cell");
			
			gridInsulin.add(tarihLabel, 0, 0);
			gridInsulin.add(degerLabel, 1, 0);
			
			for(Insulin insulin : insulinler) {
				int row = gridInsulin.getRowCount();
				
				Label tarihString = new Label(insulin.getTarih().toString());
				tarihString.setId("cell");
				Label degerString = new Label(String.valueOf(insulin.getMiktar()));
				degerString.setId("cell");
				
				gridInsulin.add(tarihString, 0, row);
				gridInsulin.add(degerString, 1, row);
			}
		}else {
			gridInsulin.add(new Label("Insülin girişi hiç yapılmadı"), 0, 0);
		}
		
		
		GridPane grid1 = new GridPane();
		
		HBox root1 = new HBox();
        
        List<Egzersiz> egzersizler = egzersizDAO.getHastaEgzersizler(hasta_tc);
		
		if(!egzersizler.isEmpty()) {
			Label tarihLabel = new Label("Tarih");
			tarihLabel.setId("cell");
			Label egzersizTurLabel = new Label("Egzersiz Türü");
			egzersizTurLabel.setId("cell");
			Label uygulandiLabel = new Label("Uygulandı mı?");
			uygulandiLabel.setId("cell");
			
			grid1.add(tarihLabel, 0, 0);
			grid1.add(egzersizTurLabel, 1, 0);
			grid1.add(uygulandiLabel, 2, 0);
			
			grid1.setHgap(20);
			grid1.setVgap(10);
			
			PieChart pieChart1 = new PieChart();
	        pieChart1.setTitle("Egzersiz Uygulama Oranı");
			
	        int uygulanan1 = 0;
	        int uygulanmayan1 = 0;
			
			for(Egzersiz egzersiz : egzersizler) {
				
				LocalDate current = egzersiz.getTarihAraligi().getBaslangic().toLocalDate();
			    LocalDate end = egzersiz.getTarihAraligi().getBitis().toLocalDate();

			    int index = 0;

			    while (!current.isAfter(end)) {
			        Date sqlDate = Date.valueOf(current);

			        int row = index % 20;
			        int column = index / 20;
					
			        Label evetLabel = new Label("Evet");
					evetLabel.setId("evetLabel");
					
					Label hayirLabel = new Label("Hayır");
					hayirLabel.setId("hayirLabel");
					
					Label tarihString = new Label(sqlDate.toString());
					tarihString.setId("cell");
					Label egzersizTurString = new Label(egzersiz.getEgzersiz_Turu().toString());
					egzersizTurString.setId("cell");

			        grid1.add(tarihString, column * 3, row + 1);
			        grid1.add(egzersizTurString, column * 3 + 1, row + 1);
					grid1.add(egzersizDAO.checkEgzersizUygulandiTakip(hasta_tc, sqlDate) ? evetLabel : hayirLabel, column * 3 + 2, row + 1);
			        
			        if(egzersizDAO.checkEgzersizUygulandiTakip(hasta_tc, sqlDate))
			        	uygulanan1++;
			        else
			        	uygulanmayan1++;

			        current = current.plusDays(1);
			        index++;
			    }
			}
			
			int toplam1 = uygulanan1 + uygulanmayan1;
			
			pieChart1.getData().addAll(
					new PieChart.Data("Uygulandı (%" + String.format("%.2f", ((double)uygulanan1 * 100.0 / toplam1))  + ")", (double)uygulanan1),
		            new PieChart.Data("Uygulanmadı (%" + String.format("%.2f", ((double)uygulanmayan1 * 100.0 / toplam1))  + ")", (double)uygulanmayan1)
		        );
			
			root1.getChildren().addAll(grid1, pieChart1, gridInsulin);
		}else {
			root1.getChildren().add(new Label("Egzersiz kaydı bulunamadı"));
		}
		
		olcumHBox.setAlignment(Pos.CENTER);
		
		VBox root = new VBox(topRightBox, headerLabel, kanSekeriGirButon);
		
		hRoot.setSpacing(20);
		root.setSpacing(20);
		hRoot.setAlignment(Pos.CENTER);
		root2.setAlignment(Pos.CENTER);
		root1.setAlignment(Pos.CENTER);
		root.getChildren().addAll(olcumHBox, root2, root1, hRoot, belirtiLabel, insulinOneri, cikisButon);
		
		root.setAlignment(Pos.TOP_CENTER);
		
		StackPane stackPane = new StackPane(root);
		stackPane.setAlignment(Pos.TOP_CENTER);
		
		ScrollPane scrollPane = new ScrollPane(stackPane);
		scrollPane.setFitToWidth(true);
		Scene hastaScene = new Scene(scrollPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		hastaScene.getStylesheets().add(getStyleSheet());
		
		primaryStage.setScene(hastaScene);
	}
	
	private static String getStyleSheet() {
		String fileName = "/style/style.css";
        var url = HastaEkranlari.class.getResource(fileName);
        if (url == null) {
            throw new IllegalArgumentException("CSS file not found: " + fileName);
        }
        return url.toExternalForm();
    } 
	
	private static HBox setOlcumGrid(Hasta hasta, Date now) {
		GridPane grid = new GridPane();
		
		HBox olcumRoot = new HBox();
		
		List<Olcum> olcumler = olcumDAO.getHastaTarihOlcumleri(hasta.getTc_kimlik_no(), now);
		
		if(!olcumler.isEmpty()) {
			olcumler.sort(Comparator.comparingInt(o -> o.getOlcum_Zamani().ordinal()));
			
			if(!olcumler.isEmpty()) {			
				grid.add(new Label("Ölçüm Vakti"), 0, 0);
				grid.add(new Label("Ölçüm Değeri"), 1, 0);
				
				for(Olcum olcum : olcumler) {
					int row = grid.getRowCount();
					
					grid.add(new Label(olcum.getOlcum_Zamani().toString()), 0, row);
					grid.add(new Label(olcum.getMiktar() + " mg/dL"), 1, row);
				}
			}else {
				grid.add(new Label("Bugün ölçüm girmediniz"), 0, 0);
			}
			
			grid.add(new Label("Bugünkü ölçüm ort: " + String.format("%.2f", hasta.getGunOrtalama(now))), 0, grid.getRowCount());
			grid.setHgap(20);
			grid.setVgap(10);
			grid.setAlignment(Pos.CENTER);
			
			CategoryAxis xAxis = new CategoryAxis();
			NumberAxis yAxis = new NumberAxis();

			LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
			lineChart.setTitle("Kan Şekeri Takibi");
			xAxis.setLabel("Zaman");
			yAxis.setLabel("Miktar");

			XYChart.Series<String, Number> seri = new XYChart.Series<>();

			for (Olcum olcum : olcumler) {
			    String zamanStr = olcum.getOlcum_Zamani().toString();
			    seri.getData().add(new XYChart.Data<>(zamanStr, olcum.getMiktar()));
			}

			lineChart.getData().add(seri);
			
			olcumRoot.getChildren().addAll(grid, lineChart);
		}else {
			olcumRoot.getChildren().add(new Label("Ölçüm kaydı bulunamadı"));
		}
		
		return olcumRoot;
	}
}
