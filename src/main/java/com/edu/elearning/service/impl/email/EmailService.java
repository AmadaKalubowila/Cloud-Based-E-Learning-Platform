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
    // BOOKING CONFIRMED  (sent to customer)
    // ─────────────────────────────────────────────────────────────
    public String buildBookingConfirmedEmail(
            String fullName, String orderId,
            String sessionRows,          // pre-built HTML <tr> rows
            double totalAmount,
            String paymentMethod,
            String paymentStatus,
            LocalDateTime expiresAt,
            String checkoutUrl) {

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
                  <h1 style="color:#2e7d32;margin:0;">✅ Booking Confirmed</h1>
                  <p style="color:#555;margin-top:6px;">Thank you for your booking, <strong>%s</strong>!</p>
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
                %s
              </div>
            </div>
        """.formatted(fullName, orderId, sessionRows, totalAmount, paymentBlock);
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT CONFIRMED  (sent to customer)
    // ─────────────────────────────────────────────────────────────
    public String buildPaymentConfirmedEmail(
            String fullName, String orderId,
            double amount, String paymentMethod, LocalDateTime paidAt) {

        return """
            <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
              <div style="max-width:600px;margin:auto;background:white;padding:28px;border-radius:10px;">

                <div style="text-align:center;margin-bottom:20px;">
                  <h1 style="color:#2e7d32;">💳 Payment Received</h1>
                  <p style="color:#555;">Hi <strong>%s</strong>, your payment has been confirmed!</p>
                </div>

                <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Order ID</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;font-family:monospace;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Amount Paid</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;color:#2e7d32;font-weight:bold;">
                      LKR %.2f
                    </td>
                  </tr>
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Payment Method</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Payment Time</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;">%s</td>
                  </tr>
                </table>

                <p style="margin-top:20px;color:#555;">
                  Your booking is now fully confirmed. See you at the session! 🎉
                </p>

                <hr style="border:none;border-top:1px solid #e0e0e0;margin-top:24px;">
                <p style="font-size:12px;color:#aaa;text-align:center;">
                  This is an automated receipt. Please keep it for your records.
                </p>
                %s
              </div>
            </div>
        """.formatted(fullName, orderId, amount, paymentMethod,
                paidAt != null ? paidAt.format(DISPLAY_FMT) : "—");
    }

    // ─────────────────────────────────────────────────────────────
    // PAYMENT FAILED  (sent to customer)
    // ─────────────────────────────────────────────────────────────
    public String buildPaymentFailedEmail(String fullName, String orderId, double amount) {
        return """
            <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
              <div style="max-width:600px;margin:auto;background:white;padding:28px;border-radius:10px;">

                <div style="text-align:center;margin-bottom:20px;">
                  <h1 style="color:#c62828;">❌ Payment Failed</h1>
                  <p style="color:#555;">Hi <strong>%s</strong>, unfortunately your payment could not be processed.</p>
                </div>

                <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Order ID</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;font-family:monospace;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Amount</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;">LKR %.2f</td>
                  </tr>
                </table>

                <div style="background:#ffebee;border-left:4px solid #c62828;padding:12px 16px;
                            border-radius:4px;margin-top:16px;">
                  Your reserved seats have been released. Please place a new booking to try again.
                </div>

                <hr style="border:none;border-top:1px solid #e0e0e0;margin-top:24px;">
                <p style="font-size:12px;color:#aaa;text-align:center;">
                  If you believe this is an error, please contact our support team.
                </p>
                %s
              </div>
            </div>
        """.formatted(fullName, orderId, amount);
    }

    // ─────────────────────────────────────────────────────────────
    // BOOKING CANCELLED  (sent to customer)
    // ─────────────────────────────────────────────────────────────
    public String buildBookingCancelledEmail(
            String fullName, String orderId, boolean refunded, double amount) {

        String refundBlock = refunded
                ? """
                  <div style="background:#e8f5e9;border-left:4px solid #4CAF50;padding:12px 16px;
                              border-radius:4px;margin-top:16px;">
                    ✅ A refund of <strong>LKR %.2f</strong> has been initiated and will reflect
                    in your account within 5–7 business days.
                  </div>
                  """.formatted(amount)
                : """
                  <div style="background:#fff8e1;border-left:4px solid #FFC107;padding:12px 16px;
                              border-radius:4px;margin-top:16px;">
                    No payment was collected, so no refund is applicable.
                  </div>
                  """;

        return """
            <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
              <div style="max-width:600px;margin:auto;background:white;padding:28px;border-radius:10px;">

                <div style="text-align:center;margin-bottom:20px;">
                  <h1 style="color:#e65100;">🚫 Booking Cancelled</h1>
                  <p style="color:#555;">Hi <strong>%s</strong>, your booking has been cancelled.</p>
                </div>

                <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Order ID</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;font-family:monospace;">%s</td>
                  </tr>
                </table>

                %s

                <hr style="border:none;border-top:1px solid #e0e0e0;margin-top:24px;">
                <p style="font-size:12px;color:#aaa;text-align:center;">
                  We hope to see you again soon.
                </p>
                %s
              </div>
            </div>
        """.formatted(fullName, orderId, refundBlock);
    }

    // ─────────────────────────────────────────────────────────────
    // SESSION COMPLETED — ADMIN ALERT
    // ─────────────────────────────────────────────────────────────
    public String buildSessionCompletedAdminEmail(
            String sessionName, String sessionId,
            LocalDateTime startTime, LocalDateTime endTime,
            int totalBookings, double totalRevenue) {

        return """
            <div style="font-family:Arial,sans-serif;background:#f4f6f8;padding:30px;">
              <div style="max-width:600px;margin:auto;background:white;padding:28px;border-radius:10px;">

                <div style="text-align:center;margin-bottom:20px;">
                  <h1 style="color:#1a237e;">📋 Session Completed</h1>
                  <p style="color:#555;">The following session has just ended.</p>
                </div>

                <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Session Name</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;"><strong>%s</strong></td>
                  </tr>
                  <tr>
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Session ID</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;font-family:monospace;">%s</td>
                  </tr>
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Start Time</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">End Time</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;">%s</td>
                  </tr>
                  <tr style="background:#f0f4f8;">
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Total Bookings</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;">%d</td>
                  </tr>
                  <tr>
                    <td style="padding:10px;font-weight:bold;border:1px solid #e0e0e0;">Total Revenue</td>
                    <td style="padding:10px;border:1px solid #e0e0e0;color:#2e7d32;font-weight:bold;">
                      LKR %.2f
                    </td>
                  </tr>
                </table>

                <hr style="border:none;border-top:1px solid #e0e0e0;margin-top:24px;">
                <p style="font-size:12px;color:#aaa;text-align:center;">
                  This is an automated admin alert from the Booking System.
                </p>
                %s
              </div>
            </div>
        """.formatted(sessionName, sessionId,
                startTime != null ? startTime.format(DISPLAY_FMT) : "—",
                endTime   != null ? endTime.format(DISPLAY_FMT)   : "—",
                totalBookings, totalRevenue);
    }
}