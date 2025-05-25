package application;

import java.sql.Date;
import java.text.Collator;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CreateCell {
	
	private HastaDAO hastaDAO = new HastaDAO();
	
	private DiyetDAO diyetDAO = new DiyetDAO();
	
	private EgzersizDAO egzersizDAO = new EgzersizDAO();
	
	private OlcumDAO olcumDAO = new OlcumDAO();
	
	private BelirtiDAO belirtiDAO = new BelirtiDAO();
	
	private UyariDAO uyariDAO = new UyariDAO();
	
	public String textFormatter(String text) {
		
        String[] words = text.split(" ");
        
        if(text.length() > 100 || words.length > 2) {

	        int mid = (int) Math.ceil(words.length / 2.0);
	
	        String firstLine = String.join(" ", Arrays.copyOfRange(words, 0, mid));
	        String secondLine = String.join(" ", Arrays.copyOfRange(words, mid, words.length));
	        
	        if(firstLine.length() > 100 && firstLine.contains(" ")) {
	        	firstLine = textFormatter(firstLine);
	        }
	        if(secondLine.length() > 100 && secondLine.contains(" ")) {
	        	secondLine = textFormatter(secondLine);
	        }
	        
	        return firstLine + "\n" + secondLine;
        }
        else if(text.length() > 20 || words.length > 2) {
        	int mid = (int) Math.ceil(words.length / 2.0);
        	
	        String firstLine = String.join(" ", Arrays.copyOfRange(words, 0, mid));
	        String secondLine = String.join(" ", Arrays.copyOfRange(words, mid, words.length));
	        
	        if(firstLine.length() > 20 && firstLine.contains(" ")) {
	        	firstLine = textFormatter(firstLine);
	        }
	        if(secondLine.length() > 20 && secondLine.contains(" ")) {
	        	secondLine = textFormatter(secondLine);
	        }
	        
	        return firstLine + "\n" + secondLine;
        }
        else {
        	return text;
        }
	}
	   
    private String getStyleSheet() {
    	String fileName = "/style/style.css";
        var url = CreateCell.class.getResource(fileName);
        if (url == null) {
            throw new IllegalArgumentException("CSS file not found: " + fileName);
        }
        return url.toExternalForm();
    } 
	
	private StackPane createImageCell(Image image) {
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(150);
        imageView.setPreserveRatio(true);
        
        return new StackPane(imageView);
    }
	
	private StackPane createStringCell(String string) {
		Rectangle background = new Rectangle(150, 94);
		background.setArcWidth(30);
        background.setArcHeight(30);
        background.setFill(Color.BLACK);
		
		Label stringLabel = new Label(string);
		stringLabel.setMaxHeight(300);
		stringLabel.setMaxWidth(300);
		stringLabel.setAlignment(Pos.CENTER);
		stringLabel.setStyle("-fx-font-family: 'Times New Roman'; -fx-text-fill: white;");
		
		return new StackPane(background, stringLabel);
	}
	
	private StackPane createDiyetCell(String hasta_tc, Date tarih) {
		Diyet diyet = diyetDAO.getCurrentDiyet(hasta_tc, tarih);
		
		if(diyet != null) {
			return createStringCell(diyet.getDiyet_Turu().toString() + "\n" + diyet.getTarihAraligi().toString());
		}
		else {
			return createStringCell("Mevcut diyet\ntanımlanmamış");
		}
	}
	
	private StackPane createEgzersizCell(String hasta_tc, Date tarih) {
		Egzersiz egzersiz = egzersizDAO.getCurrentEgzersiz(hasta_tc, tarih);
		
		if(egzersiz != null) {
			return createStringCell(egzersiz.getEgzersiz_Turu().toString() + " " + egzersiz.getTarihAraligi().toString());
		}
		else {						
			return createStringCell("Mevcut egzersiz\ntanımlanmamış");
		}
	}
	
	private Node createOlcumCell(String hasta_tc, Date tarih) {
		List<Olcum> hastaOlcumleri = olcumDAO.getHastaTarihOlcumleri(hasta_tc, tarih);
		
		GridPane olcumGrid = new GridPane();
		
		olcumGrid.setHgap(20);
		
		if(!hastaOlcumleri.isEmpty()) {
			for(Olcum olcum : hastaOlcumleri) {
				
				Label olcumZamaniLabel = new Label();
				olcumZamaniLabel.setStyle("-fx-text-fill: white;");
				
				Label olcumDegeriLabel = new Label();
				olcumDegeriLabel.setStyle("-fx-text-fill: white;");
				int row = olcumGrid.getRowCount();
				
				olcumZamaniLabel.setText(olcum.getOlcum_Zamani().toString());
				olcumDegeriLabel.setText(String.valueOf(olcum.getMiktar()));
				
				olcumGrid.add(olcumZamaniLabel, 0, row);
				olcumGrid.add(olcumDegeriLabel, 1, row);
				olcumGrid.setAlignment(Pos.CENTER);
			}
		}
		else {						
			Label outputLabel = new Label("Hasta bugün\nölçüm girmedi");
			outputLabel.setStyle("-fx-text-fill: white;");
			outputLabel.setAlignment(Pos.CENTER);
			return outputLabel;
		}
		
		return olcumGrid;
		
	}
	
	private VBox createEklemeCell(String hasta_tc) {
		Hasta hasta = hastaDAO.getHasta(hasta_tc);
		
		Button egzersizEkle = new Button("Hastaya\negzersiz ekle");
		
		Label outputLabelEgzersiz = new Label();
		
		egzersizEkle.setOnAction(e ->{
			
			ComboBox<Egzersiz_Turu> cbEgzersiz = new ComboBox<>();
			cbEgzersiz.getItems().addAll(Egzersiz_Turu.values());
			cbEgzersiz.setPromptText("Lütfen egzersiz seçiniz:");
			
			Button onayla = new Button("Onayla");
			
			Stage inputStage = new Stage();
			
			DatePicker baslangicTarih = new DatePicker();
			baslangicTarih.setPromptText("Başlangıç tarihi");
			
			DatePicker bitisTarih = new DatePicker();
			bitisTarih.setPromptText("Bitiş tarihi");				
			
			onayla.setOnAction(e2 -> {
				try {
					egzersizDAO.hastaEgzersizEkle( new Egzersiz(hasta, 
							new TarihAraligi(Date.valueOf(baslangicTarih.getValue()), Date.valueOf(bitisTarih.getValue())), cbEgzersiz.getValue()));
					
					outputLabelEgzersiz.setStyle("-fx-text-fill: green;");
					outputLabelEgzersiz.setText("Egzersiz ekleme başarılı!");
					inputStage.close();
				} catch (Exception e1) {
					outputLabelEgzersiz.setStyle("-fx-text-fill: red;");
					outputLabelEgzersiz.setText(textFormatter("Lütfen egzersiz türü seçin ya da eklediğiniz tarihi kontrol edin"));
				}
			});
			
			outputLabelEgzersiz.setText("");
			
			Scene scene = new Scene(new VBox(cbEgzersiz, baslangicTarih, bitisTarih, onayla, outputLabelEgzersiz), 400 ,200);
			scene.getStylesheets().add(getStyleSheet());
			
			inputStage.setScene(scene);
			inputStage.show();
		});
		
		Label outputLabelDiyet = new Label();
		
		Button diyetEkle = new Button("Hastaya\ndiyet ekle");
		
		diyetEkle.setOnAction(e ->{
			
			ComboBox<Diyet_Turu> cbDiyet = new ComboBox<>();
			cbDiyet.getItems().addAll(Diyet_Turu.values());
			cbDiyet.setPromptText("Lütfen diyet seçiniz:");
			
			Button onayla = new Button("Onayla");
			
			DatePicker baslangicTarih = new DatePicker();
			baslangicTarih.setPromptText("Başlangıç tarihi");
			
			DatePicker bitisTarih = new DatePicker();
			bitisTarih.setPromptText("Bitiş tarihi");
			
			onayla.setOnAction(e2 -> {
				try {
					diyetDAO.hastaDiyetEkle(new Diyet(hasta, 
											new TarihAraligi(Date.valueOf(baslangicTarih.getValue()), Date.valueOf(bitisTarih.getValue())), cbDiyet.getValue()));
					
					outputLabelDiyet.setStyle("-fx-text-fill: green;");
					outputLabelDiyet.setText("Diyet ekleme başarılı!");
				} catch (Exception e1) {
					outputLabelDiyet.setStyle("-fx-text-fill: red;");
					outputLabelDiyet.setText(textFormatter("Lütfen diyet türü seçin ya da eklediğiniz tarihi kontrol edin"));
				}
			});
			
			outputLabelDiyet.setText("");
			
			Stage inputStage = new Stage();
			
			Scene scene = new Scene(new VBox(cbDiyet, baslangicTarih, bitisTarih, onayla, outputLabelDiyet), 400 ,200);
			scene.getStylesheets().add(getStyleSheet());
			
			inputStage.setScene(scene);
			inputStage.show();
		});
		
		Button belirtiEkle = new Button("Hastaya\nbelirti ekle");
		
		belirtiEkle.setOnAction(e ->{
			Label header = new Label("Belirtiyi yazdıktan ekle butonuna basınız");
			
			Label outputLabelBelirti = new Label();
			
			TextField belirtiField = new TextField();
			belirtiField.setPromptText("Belirti adı yazın");
			
			Button ekleButon = new Button("Ekle");
			ekleButon.setOnAction(e1 ->{
				
				boolean mevcut = false;
				
				for(Belirti belirti : belirtiDAO.getHastaBelirtiler(hasta_tc)) {
					if(belirti.getBelirti_adi().toUpperCase().equals(belirtiField.getText().toUpperCase())) {
						mevcut = true;
					}
				}
				
				if(!mevcut) {
					try {				
						belirtiDAO.hastaBelirtiEkle(hasta_tc, new Belirti(hasta, belirtiField.getText()));
						outputLabelBelirti.setStyle("-fx-text-fill: green;");
						outputLabelBelirti.setText("Belirti ekleme başarılı");
					} catch (Exception e12) {
						outputLabelBelirti.setStyle("-fx-text-fill: red;");
						outputLabelBelirti.setText("Belirti ekleme başarısız");
						e12.printStackTrace();
					}
				}else {
					outputLabelBelirti.setStyle("-fx-text-fill: red;");
					outputLabelBelirti.setText("Bu belirti zaten eklenmiş");
				}
			});
			
			outputLabelBelirti.setText("");
			
			Stage inputStage = new Stage();
			
			Scene scene = new Scene(new VBox(header, belirtiField, ekleButon, outputLabelBelirti), 400 ,200);
			scene.getStylesheets().add(getStyleSheet());
			
			inputStage.setScene(scene);
			inputStage.show();
		});
		
		return new VBox(egzersizEkle, diyetEkle, belirtiEkle);
	}
	
	private StackPane createBelirtiCell(String hasta_tc){
		List<Belirti> belirtiler = belirtiDAO.getHastaBelirtiler(hasta_tc);
		
		String string = "";
		
		if(belirtiler.isEmpty()) {
			string = "Belirti tanımlanmamış";
		}else {
			for(Belirti belirti : belirtiler) {
				string += belirti.getBelirti_adi() + "\n";
			}
		}
						
		return createStringCell(string);
	}
	
	public StackPane createUyariCell(String hasta_tc, Date tarih) {
		
		Rectangle background = new Rectangle(300, 94);
		background.setArcWidth(30);
        background.setArcHeight(30);
        background.setFill(Color.BLACK);
		
		Label stringLabel = new Label();
		stringLabel.setMaxHeight(300);
		stringLabel.setMaxWidth(300);
		stringLabel.setAlignment(Pos.CENTER);
		stringLabel.setStyle("-fx-font-family: 'Times New Roman'; -fx-text-fill: white;");
		
		Uyari uyari = uyariDAO.getCurrentUyari(hasta_tc, tarih);
		
		if(uyari != null) {
		
		stringLabel.setText(uyari.getUyariTipi() + ": \n" + uyari.getUyariMesaji());
		}else {
			stringLabel.setText("Uyarı eklemesi\nsırasında hata oluştu");
		}
		
		return new StackPane(background, stringLabel);
	}
	
	private StackPane createOneriCell(String hastaTC, Date tarih) {
		Oneri oneri = OneriHesaplama.getOneri(olcumDAO.getHastaTarihOlcumleri(hastaTC, tarih), belirtiDAO.getHastaBelirtiler(hastaTC));
		
		if(oneri != null) {
			return createStringCell("Diyet: " + oneri.getDiyetTuru().toString() + "\nEgzersiz: " + oneri.getEgzersizTuru().toString());
		}else {
			return createStringCell("Öneri oluşturmak için\nyeterli bilgi yok");
		}
	}
	
	private GridPane createArsivCell(String hastaTC) {
		
		List<Olcum> olcumler = olcumDAO.getHastaOlcumleri(hastaTC);
		
		Button olcumButon = new Button("Geçmiş ölçümleri\nincele");
		
		olcumButon.setOnAction(e -> {
			GridPane grid = new GridPane();
			
			HBox olcumRoot = new HBox();
			
			if(!olcumler.isEmpty()) {
				Label tarihLabel = new Label("Tarih");
				tarihLabel.setId("cell");
				Label vakitLabel = new Label("Vakit");
				vakitLabel.setId("cell");
				Label degerLabel = new Label("Değer");
				degerLabel.setId("cell");
				
				grid.add(tarihLabel, 0, 0);
				grid.add(vakitLabel, 1, 0);
				grid.add(degerLabel, 2, 0);
				
				grid.setHgap(20);
				grid.setVgap(10);
				
				for(Olcum olcum : olcumler) {
					int newRow = grid.getRowCount() + 1;
					
					Label tarihString = new Label(olcum.getTarih().toString());
					tarihString.setId("cell");
					Label vakitString = new Label(olcum.getOlcum_Zamani().toString());
					vakitString.setId("cell");
					Label degerString = new Label(String.valueOf(olcum.getMiktar()));
					degerString.setId("cell");
					
					grid.add(tarihString, 0, newRow);
					grid.add(vakitString, 1, newRow);
					grid.add(degerString, 2, newRow);
				}
				
				Map<Date, List<Olcum>> tarihBazliGrup = olcumler.stream()
				        .filter(o -> o.getOlcum_Zamani() != Olcum_Zamani.GECERSIZ)
				        .collect(Collectors.groupingBy(o -> o.getTarih()));
				
				Map<Date, Double> gunlukOrtalamalar = new TreeMap<>();
				
				for(Map.Entry<Date, List<Olcum>> entry : tarihBazliGrup.entrySet()) {
					 Date tarih = entry.getKey();
					 List<Olcum> oGunOlcumler = entry.getValue();

				        Map<Olcum_Zamani, Olcum> farkliOgeler = oGunOlcumler.stream()
				            .collect(Collectors.toMap(
				                Olcum::getOlcum_Zamani,
				                Function.identity()
				            ));

				        double ortalama = farkliOgeler.values().stream()
				            .mapToDouble(Olcum::getMiktar)
				            .average()
				            .orElse(0.0);

				        gunlukOrtalamalar.put(tarih, ortalama);
				}
				
				CategoryAxis xAxis = new CategoryAxis();
				NumberAxis yAxis = new NumberAxis();

				LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
				lineChart.setTitle("Kan Şekeri Takibi");
				xAxis.setLabel("Tarih");
				yAxis.setLabel("Miktar");

				XYChart.Series<String, Number> seri = new XYChart.Series<>();

				for (Map.Entry<Date, Double> entry : gunlukOrtalamalar.entrySet()) {
				    seri.getData().add(new XYChart.Data<>( entry.getKey().toString() + "\n" + 
				    				(egzersizDAO.getCurrentEgzersiz(hastaTC, entry.getKey()) != null ? egzersizDAO.getCurrentEgzersiz(hastaTC, entry.getKey()) 
				    				 : new Egzersiz(null, null, Egzersiz_Turu.YOK)).getEgzersiz_Turu().toString() + "\n" +
				    				 (diyetDAO.getCurrentDiyet(hastaTC, entry.getKey()) != null ? diyetDAO.getCurrentDiyet(hastaTC, entry.getKey()) 
						    				 : new Diyet(null, null, Diyet_Turu.YOK)).getDiyet_Turu().toString(), entry.getValue()));
				}
				
				lineChart.getData().add(seri);
				
				olcumRoot.getChildren().addAll(grid, lineChart);
			}else {
				olcumRoot.getChildren().add(new Label("Ölçüm kaydı bulunamadı"));
			}
			
			grid.setAlignment(Pos.CENTER);
			Stage arsivStage = new Stage();
			Scene scene = new Scene(olcumRoot);
			scene.getStylesheets().add(getStyleSheet());
			
			arsivStage.setScene(scene);
			arsivStage.show();
		});
		
		Button diyetButon = new Button("Geçmiş diyetleri\nincele");
		
		List<Diyet> diyetler = diyetDAO.getHastaDiyetler(hastaTC);
		
		diyetButon.setOnAction(e -> {
			GridPane grid = new GridPane();
			
			HBox root = new HBox();
			
			if(!diyetler.isEmpty()) {
				Label tarihLabel = new Label("Tarih");
				tarihLabel.setId("cell");
				Label egzersizTurLabel = new Label("Diyet Türü");
				egzersizTurLabel.setId("cell");
				Label uygulandiLabel = new Label("Uygulandı mı?");
				uygulandiLabel.setId("cell");
				
				grid.add(tarihLabel, 0, 0);
				grid.add(egzersizTurLabel, 1, 0);
				grid.add(uygulandiLabel, 2, 0);
				
				grid.setHgap(20);
				grid.setVgap(10);
				
				PieChart pieChart = new PieChart();
		        pieChart.setTitle("Diyet Uygulama Oranı");
				
		        int uygulanan = 0;
		        int uygulanmayan = 0;
		        
			    int index = 0;
		        
				for(Diyet diyet : diyetler) {
					
					LocalDate current = diyet.getTarihAraligi().getBaslangic().toLocalDate();
				    LocalDate end = diyet.getTarihAraligi().getBitis().toLocalDate();

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
						Label diyetTurString = new Label(diyet.getDiyet_Turu().toString());
						diyetTurString.setId("cell");

				        grid.add(tarihString, column * 3, row + 1);
				        grid.add(diyetTurString, column * 3 + 1, row + 1);
				        grid.add(diyetDAO.checkDiyetUygulandiTakip(hastaTC, sqlDate) ? evetLabel : hayirLabel, column * 3 + 2, row + 1);
				        
				        if(diyetDAO.checkDiyetUygulandiTakip(hastaTC, sqlDate))
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
				
				root.getChildren().addAll(grid, pieChart);
			}else {
				root.getChildren().add(new Label("Diyet kaydı bulunamadı"));
			}
			
			grid.setAlignment(Pos.CENTER);
			Stage arsivStage = new Stage();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getStyleSheet());
			arsivStage.setScene(scene);
			arsivStage.show();
		});
		
		Button egzersizButon = new Button("Geçmiş egzersizleri\nincele");
		
		List<Egzersiz> egzersizler = egzersizDAO.getHastaEgzersizler(hastaTC);
		
		egzersizButon.setOnAction(e -> {
			GridPane grid = new GridPane();
			
			HBox root = new HBox();
			
			if(!egzersizler.isEmpty()) {
				Label tarihLabel = new Label("Tarih");
				tarihLabel.setId("cell");
				Label egzersizTurLabel = new Label("Egzersiz Türü");
				egzersizTurLabel.setId("cell");
				Label uygulandiLabel = new Label("Uygulandı mı?");
				uygulandiLabel.setId("cell");
				
				grid.add(tarihLabel, 0, 0);
				grid.add(egzersizTurLabel, 1, 0);
				grid.add(uygulandiLabel, 2, 0);
				
				grid.setHgap(20);
				grid.setVgap(10);
				
				PieChart pieChart = new PieChart();
		        pieChart.setTitle("Egzersiz Uygulama Oranı");
				
		        int uygulanan = 0;
		        int uygulanmayan = 0;
		        
			    int index = 0;
				
				for(Egzersiz egzersiz : egzersizler) {
					
					LocalDate current = egzersiz.getTarihAraligi().getBaslangic().toLocalDate();
				    LocalDate end = egzersiz.getTarihAraligi().getBitis().toLocalDate();

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

				        grid.add(tarihString, column * 3, row + 1);
				        grid.add(egzersizTurString, column * 3 + 1, row + 1);
				        grid.add(egzersizDAO.checkEgzersizUygulandiTakip(hastaTC, sqlDate) ? evetLabel : hayirLabel, column * 3 + 2, row + 1);
				        
				        if(egzersizDAO.checkEgzersizUygulandiTakip(hastaTC, sqlDate))
				        	uygulanan++;
				        else
				        	uygulanmayan++;

				        current = current.plusDays(1);
				        index++;
				    }
				}
				
				int toplam = uygulanan + uygulanmayan;
				
				pieChart.getData().addAll(
						new PieChart.Data("Uygulandı (%" + String.format("%.2f", ((double)uygulanan * 100.0 / toplam))  + ")", (double)uygulanan),
			            new PieChart.Data("Uygulanmadı (%" + String.format("%.2f", ((double)uygulanmayan * 100.0 / toplam))  + ")", (double)uygulanmayan)
			        );
				
				root.getChildren().addAll(grid, pieChart);
			}else {
				root.getChildren().add(new Label("Egzersiz kaydı bulunamadı"));
			}
			
			grid.setAlignment(Pos.CENTER);
			Stage arsivStage = new Stage();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getStyleSheet());
			arsivStage.setScene(scene);
			arsivStage.show();
		});
		
		Button uyariButon = new Button("Geçmiş uyarıları\nincele");
		
		List<Uyari> uyarilar = uyariDAO.getUyarilar(hastaTC);
		
		uyariButon.setOnAction(e ->{
			GridPane grid = new GridPane();
			
			if(!uyarilar.isEmpty()) {
				
				Label tarihLabel = new Label("Tarih");
				tarihLabel.setId("cell");
				Label uyariLabel = new Label("Uyarı Tipi");
				uyariLabel.setId("cell");
				
				grid.add(tarihLabel, 0, 0);
				grid.add(uyariLabel, 1, 0);
				
				grid.setHgap(20);
				grid.setVgap(10);
				
				for(Uyari uyari : uyarilar) {
					int row = grid.getRowCount();
					
					Label tarihString = new Label(uyari.getTarih().toString());
					tarihString.setId("cell");
					Label uyariString = new Label(uyari.getUyariTipi());
					uyariString.setId("cell");
					
					grid.add(tarihString, 0, row);
					grid.add(uyariString, 1, row);
				}
			}else {
				grid.add(new Label("Uyarı kaydı bulunamadı"), 0, 0);
			}
			
			Stage arsivStage = new Stage();
			Scene scene = new Scene(grid);
			scene.getStylesheets().add(getStyleSheet());
			arsivStage.setScene(scene);
			arsivStage.show();
		});
		
		GridPane grid = new GridPane();
		
		grid.add(olcumButon, 0, 0);
		grid.add(diyetButon, 1, 0);
		grid.add(egzersizButon, 0, 1);
		grid.add(uyariButon, 1, 1);
		
		return grid;
	}
	
	public void createCells(GridPane grid, List<Hasta> hastaListe, Date tarih) {
		
		String[] headers = {"Profil Resmi","Isim","Cinsiyet","Bugünkü Şeker\nÖlçümleri","Mevcut Diyet","Mevcut Egzersiz","Belirtiler","Öneri","Uyarı","                                      "};
		
		for (int i = 0; i < headers.length; i++) {       	
			Label label = new Label(headers[i]);
			if(headers[i].equals("Bugünkü Şeker\nÖlçümleri")) {
				label.setOnMouseClicked(e -> {
					hastaListe.sort(Comparator.comparingDouble(hasta -> hasta.getGunOrtalama(tarih)));
					grid.getChildren().clear();
					createCells(grid, hastaListe, tarih);
				});
			}
			if(headers[i].equals("Mevcut Diyet")) {
				label.setOnMouseClicked(e -> {
					Comparator<Hasta> comparator = Comparator.comparing(
						    h -> {
						        Diyet d = h.getCurrentDiyet(tarih);
						        return d != null ? d.getDiyet_Turu().ordinal() : Integer.MAX_VALUE;
						    }
						);
					hastaListe.sort(comparator);
					grid.getChildren().clear();
					createCells(grid, hastaListe, tarih);
				});
			}
			if(headers[i].equals("Mevcut Egzersiz")) {
				label.setOnMouseClicked(e -> {
					Comparator<Hasta> comparator = Comparator.comparing(
						    h -> {
						        Egzersiz e2 = h.getCurrentEgzersiz(tarih);
						        return e2 != null ? e2.getEgzersiz_Turu().ordinal() : Integer.MAX_VALUE;
						    }
						);
					hastaListe.sort(comparator);
					grid.getChildren().clear();
					createCells(grid, hastaListe, tarih);
				});
			}
			label.setAlignment(Pos.CENTER);
			label.setStyle("-fx-font-family: 'Times New Roman'; -fx-font-size: 17; -fx-text-fill: white;");
			grid.add(label, i + 1, 0);
		}
		
		Collator collator = Collator.getInstance(Locale.forLanguageTag("tr-TR"));
		collator.setStrength(Collator.PRIMARY);

		hastaListe.sort((h1, h2) -> collator.compare(h1.getAd().toLowerCase(), h2.getAd().toLowerCase()));
		
		for(Hasta hasta : hastaListe) {
			String hastaTC = hasta.getTc_kimlik_no();
						
			UyariOlustur.setUyari(hastaTC);
			
			int newRow = grid.getRowCount();
			
			grid.add(createEklemeCell(hastaTC), 0, newRow);
			grid.add(createImageCell(hasta.getProfil_resmi()), 1, newRow);
			grid.add(createStringCell(hasta.getAd() + " " + hasta.getSoyad()), 2, newRow);
			grid.add(createStringCell(hasta.getCinsiyet().toString()), 3, newRow);
			grid.add(createOlcumCell(hastaTC, tarih), 4, newRow);
			grid.add(createDiyetCell(hastaTC, tarih), 5, newRow);
			grid.add(createEgzersizCell(hastaTC, tarih), 6, newRow);
			grid.add(createBelirtiCell(hastaTC), 7, newRow);
			grid.add(createOneriCell(hastaTC, tarih), 8, newRow);
			grid.add(createUyariCell(hastaTC, tarih), 9, newRow);
			grid.add(createArsivCell(hastaTC), 10, newRow);
		}
	}
}
