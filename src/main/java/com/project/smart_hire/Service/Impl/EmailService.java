
package com.project.smart_hire.Service.Impl;

import com.project.smart_hire.Entity.Interview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendInterviewInvite(String to, Interview interview, String inviteLink) throws Exception {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("SmartHire – Interview Invitation");
        helper.setFrom("no-reply@smarthire.com");

        // ----------  FIXED PLACEHOLDERS ----------
        String html = """
        <h2>Hello %s,</h2>
        <p>You have been invited for a <strong>%s</strong> interview.</p>
        <p><strong>Job:</strong> %s</p>
        <p><strong>Scheduled:</strong> %s</p>
        <p><strong>Meeting Link:</strong> <a href="%s">%s</a></p>
        <p>Click below to access the SmartHire interview platform:</p>
        <p style="margin:20px 0;">
            <a href="%s" style="background:#6366f1;color:#fff;padding:12px 24px;
                                   text-decoration:none;border-radius:6px;">
                Join Interview Platform
            </a>
        </p>
        <hr/>
        <small>SmartHire – AI Powered Recruitment</small>
        """.formatted(
                interview.getCandidateName(),
                interview.getType(),
                interview.getJobTitle(),
                interview.getScheduledAt(),
                interview.getMeetingLink(), interview.getMeetingLink(),
                inviteLink
        );

        helper.setText(html, true);
        mailSender.send(mime);
    }
}