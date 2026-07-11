package com.edu.elearning.service.impl.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // PASSWORD RESET
    // ─────────────────────────────────────────────────────────────
    public String buildResetEmail(String resetLink) {
        return """
        <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
          <div style="max-width:600px;margin:auto;background:white;padding:20px;border-radius:10px;">
            <h2 style="color:#333;">Password Reset Request</h2>
            <p>Hello,</p>
            <p>We received a request to reset your password. Click the button below to proceed:</p>
            <div style="text-align:center;margin:30px 0;">
              <a href="%s" style="background:#4CAF50;color:white;padding:12px 25px;
                 text-decoration:none;border-radius:5px;display:inline-block;font-weight:bold;">
                Reset Password
              </a>
            </div>
            <p style="color:#777;font-size:14px;">This link will expire in 15 minutes.</p>
            <hr>
            <p style="font-size:12px;color:#aaa;">If you did not request this, ignore this email.</p>
          </div>
        </div>
        """.formatted(resetLink);
    }

    // ─────────────────────────────────────────────────────────────
    // WELCOME EMAIL (sent on user registration)
    // ─────────────────────────────────────────────────────────────
    public String buildWelcomeEmail(String fullName) {
        return """
        <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
          <div style="max-width:600px;margin:auto;background:white;padding:20px;border-radius:10px;">
            <h2 style="color:#333;">Welcome, %s! 🎉</h2>
            <p>Thank you for registering on our e-learning platform.</p>
            <p>Your account has been successfully created and you can now log in
               and start exploring courses right away.</p>
            <div style="text-align:center;margin:30px 0;">
              <a href="#" style="background:#4CAF50;color:white;padding:12px 25px;
                 text-decoration:none;border-radius:5px;display:inline-block;font-weight:bold;">
                Go to Dashboard
              </a>
            </div>
            <hr>
            <p style="font-size:12px;color:#aaa;">
              If you did not create this account, please contact our support team immediately.
            </p>
          </div>
        </div>
        """.formatted(fullName);
    }
    // ─────────────────────────────────────────────────────────────
// COURSE ENROLLMENT CONFIRMED
// ─────────────────────────────────────────────────────────────
    public String buildEnrollmentEmail(String fullName, String courseName, LocalDateTime enrolledDate) {
        return """
        <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
          <div style="max-width:600px;margin:auto;background:white;padding:20px;border-radius:10px;">
            <div style="text-align:center;margin-bottom:20px;">
              <h1 style="color:#2e7d32;margin:0;">✅ Enrollment Confirmed</h1>
            </div>
            <p>Hello <strong>%s</strong>,</p>
            <p>You have successfully enrolled in:</p>
            <div style="background:#f0f4f8;border-left:4px solid #1a237e;padding:14px 18px;
                        border-radius:4px;margin:16px 0;">
              <strong style="font-size:16px;">%s</strong><br>
              <span style="color:#777;font-size:13px;">Enrolled on: %s</span>
            </div>
            <p>Your progress starts at 0%%. Log in anytime to pick up where you left off.</p>
            <div style="text-align:center;margin:30px 0;">
              <a href="#" style="background:#4CAF50;color:white;padding:12px 25px;
                 text-decoration:none;border-radius:5px;display:inline-block;font-weight:bold;">
                Start Learning
              </a>
            </div>
            <hr>
            <p style="font-size:12px;color:#aaa;">
              If you did not enroll in this course, please contact our support team.
            </p>
          </div>
        </div>
        """.formatted(fullName, courseName, enrolledDate.format(DISPLAY_FMT));
    }
    // ─────────────────────────────────────────────────────────────
    // BOOKING CONFIRMED (sent to customer)
    // ─────────────────────────────────────────────────────────────
    public String buildBookingConfirmedEmail(
            String fullName,
            String orderId,
            String sessionRows,
            double totalAmount,
            String paymentMethod,
            String paymentStatus,
            String checkoutUrl,
            LocalDateTime expiresAt) {

        String paymentBlock = switch (paymentMethod) {
            case "CASH" -> """
                <div style="background:#fff8e1;border-left:4px solid #FFC107;padding:12px 16px;
                            border-radius:4px;margin-top:16px;">
                  <strong>💵 Cash Payment</strong><br>
                  Please pay at the counter before your session starts.<br>
                  <span style="color:#e65100;">Payment deadline: %s</span>
                </div>
            """.formatted(expiresAt != null ? expiresAt.format(DISPLAY_FMT) : "—");

            case "ONLINE" -> """
                <div style="background:#e8f5e9;border-left:4px solid #4CAF50;padding:12px 16px;
                            border-radius:4px;margin-top:16px;">
                  <strong>💳 Online Payment</strong> — Status: <strong>%s</strong><br>
                  Complete your payment within 15 minutes:
                  <div style="text-align:center;margin:14px 0;">
                    <a href="%s" style="background:#4CAF50;color:white;padding:10px 22px;
                       text-decoration:none;border-radius:5px;display:inline-block;font-weight:bold;">
                      Pay Now
                    </a>
                  </div>
                  <span style="color:#777;font-size:13px;">
                    Link expires at: %s
                  </span>
                </div>
            """.formatted(paymentStatus,
                    checkoutUrl != null ? checkoutUrl : "#",
                    expiresAt != null ? expiresAt.format(DISPLAY_FMT) : "—");

            default -> "";
        };

        return """
            <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
              <div style="max-width:640px;margin:auto;background:white;padding:28px;border-radius:10px;">

                <div style="text-align:center;margin-bottom:24px;">
                  <h1 style="color:#2e7d32;margin:0;">✅ Registration Confirmed</h1>
                  <p style="color:#555;margin-top:6px;">Welcome to Onboard <strong>%s</strong>!</p>
                </div>

                <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;width:40%%;border:1px solid #e0e0e0;">Order ID</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;font-family:monospace;">%s</td>
                  </tr>
                </table>

                <h3 style="color:#333;margin-top:24px;">📋 Sessions Booked</h3>
                <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                  <thead>
                    <tr style="background:#1a237e;color:white;">
                      <th style="padding:10px;text-align:left;">Session</th>
                      <th style="padding:10px;text-align:center;">Adults</th>
                      <th style="padding:10px;text-align:center;">Children</th>
                      <th style="padding:10px;text-align:right;">Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    %s
                  </tbody>
                  <tfoot>
                    <tr style="background:#f5f5f5;font-weight:bold;">
                      <td colspan="3" style="padding:10px;border-top:2px solid #1a237e;">Grand Total</td>
                      <td style="padding:10px;text-align:right;border-top:2px solid #1a237e;">
                        LKR %.2f
                      </td>
                    </tr>
                  </tfoot>
                </table>

                %s

                <hr style="margin-top:28px;border:none;border-top:1px solid #e0e0e0;">
                <p style="font-size:12px;color:#aaa;text-align:center;">
                  If you did not make this booking, please contact us immediately.
                </p>
              </div>
            </div>
        """.formatted(fullName, orderId, sessionRows, totalAmount, paymentBlock);
    }
}