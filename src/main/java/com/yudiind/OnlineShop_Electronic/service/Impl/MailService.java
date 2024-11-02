package com.yudiind.OnlineShop_Electronic.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("shop.electronic212@gmail.com");    // Atur alamat email pengirim

        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String toEmail, String resetCode){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("shop.electronic212@gmail.com");
        message.setSubject("Password Reset Request");       // judul pesan di email user
        message.setText("Use the following code to reset your password: " + resetCode);

        mailSender.send(message);
    }

    /**
     *  INI KALAU PAKAI reCAPTCHA, THYMELEAF dan HTML
    private final SpringTemplateEngine thymeleafTemplateEngine;
    @Value("${spring.mail.username}")
    private String username;
    @Value(("${hostname}"))
    private String hostname;

    @Autowired
    public MailService(JavaMailSender mailSender, SpringTemplateEngine thymeleafTemplateEngine) {
        this.mailSender = mailSender;
        this.thymeleafTemplateEngine = thymeleafTemplateEngine;
    }

    public void sendMessageHtml(String to,
                                String subject,
                                String template,
                                Map<String, Object> attributes){

       ExecutorService executor = Executors.newFixedThreadPool(10);
       executor.execute(()->{
           attributes.put("url", "http://" + hostname);

           Context thymeleafContext = new Context();
           thymeleafContext.setVariables(attributes);

           String htmlBody = thymeleafTemplateEngine.process("email/" + template, thymeleafContext);

           // Menggunakan Template Email (Opsional)
           // Jika Anda ingin menggunakan template HTML untuk email, Anda bisa menggunakan MimeMessage dan MimeMessageHelper untuk membuat email dengan format HTML.
           MimeMessage message = mailSender.createMimeMessage();
           try {
               MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
               helper.setFrom(username);
               helper.setTo(to);
               helper.setSubject(subject);
               helper.setText(htmlBody, true);
               mailSender.send(message);
           }catch (MessagingException e){
               throw new RuntimeException(e);
           }
       });
       executor.shutdown();;
    }
    */
}
