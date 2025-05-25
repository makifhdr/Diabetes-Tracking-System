package application;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ImageAndBase64Op {
	public static String imageToBase64(Image image) throws IOException {
			
			if (image.getPixelReader() == null) {
	            System.err.println("Geçersiz veya yüklenmemiş Image nesnesi!");
	            return null;
	        }
            // Convert JavaFX Image to BufferedImage
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            
            if (bufferedImage == null) {
                System.err.println("BufferedImage oluşturulamadı!");
                return null;
            }

            // Write BufferedImage to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            // Encode to Base64
            return Base64.getEncoder().encodeToString(imageBytes);

    }
	
	public static Image base64ToImage(String base64Image) {
		try {
            // Decode the Base64 string into bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            // Convert bytes into an InputStream and create JavaFX Image
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            return new Image(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
}
