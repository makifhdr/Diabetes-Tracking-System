package application;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;



//App password: aldm trtw ntpf rqdy
public class MailSender {
	public static void sendEmail(Kullanici kullanici) {
        final String fromEmail = "makifhidir2861@gmail.com"; // kendi mail adresin
        final String fromPassword = "aldm trtw ntpf rqdy"; // Gmail uygulama şifresi

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail, "Diyabet Takip Sistemi"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(kullanici.getE_posta()));
            msg.setSubject("Şifreniz Oluşturuldu");

            String content = "Merhaba Sayın " + kullanici.getAd() + " " + kullanici.getSoyad() + ",\n\nSisteme giriş şifreniz: " + kullanici.getSifre() + "\n\nİyi günler dileriz.";
            msg.setText(content);

            Transport.send(msg);
            System.out.println("Mail gönderildi!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
