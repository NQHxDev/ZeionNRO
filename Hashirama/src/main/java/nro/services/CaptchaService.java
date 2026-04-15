package nro.services;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author Văn Tuấn 0337766460
 *
 */
public class CaptchaService {
    private static final SecureRandom RNG = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // loại O,0,I,1
    private static final long TTL_MS = 120_000; // 2 phút
    private static final String PEPPER = "change-me-long-secret";

    private static final class Entry {
        final String hash; // sha256(answer + PEPPER)
        final long expAt;
        final String ip;
        int attempts;
        Entry(String hash, long expAt, String ip) {
            this.hash = hash; this.expAt = expAt; this.ip = ip; this.attempts = 0;
        }
    }
    private static final Map<Long, Entry> STORE = new ConcurrentHashMap<>();

    public static final class Challenge {
        public final byte[] png;
        public final String token; // nonce hex
        public final long ttlMs;
        Challenge(byte[] png, String token, long ttlMs) {
            this.png = png; this.token = token; this.ttlMs = ttlMs;
        }
    }

    public static Challenge generate(String ip) throws Exception {
        String code = randomCode(5 + RNG.nextInt(2)); // 5-6 ký tự
        BufferedImage img = render(code);
        byte[] png = toPNG(img);
        long nonce = RNG.nextLong(); // token
        long exp = System.currentTimeMillis() + TTL_MS;
        String hash = sha256(code.toUpperCase() + PEPPER);
        STORE.put(nonce, new Entry(hash, exp, ip));
        // dọn rác nhẹ (không cần quá cầu kỳ)
        STORE.entrySet().removeIf(e -> e.getValue().expAt < System.currentTimeMillis());
        return new Challenge(png, Long.toHexString(nonce), TTL_MS);
    }

    public static boolean verify(String token, String answer, String ip) {
        try {
            long nonce = Long.parseUnsignedLong(token, 16);
            Entry e = STORE.remove(nonce); // one-time
            if (e == null) return false;
            if (System.currentTimeMillis() > e.expAt) return false;
            if (e.ip != null && !e.ip.equals(ip)) {
                // nếu muốn chặt hơn, ràng buộc IP
                // return false;
            }
            if (answer == null) return false;
            String hash = sha256(answer.toUpperCase() + PEPPER);
            return hash.equals(e.hash);
        } catch (Exception ex) {
            return false;
        }
    }

    private static String randomCode(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHABET.charAt(RNG.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    private static BufferedImage render(String code) {
        int w = 150, h = 48;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);

        // noise nền
        for (int i = 0; i < 120; i++) {
            g.setColor(new Color(200 + RNG.nextInt(55), 200 + RNG.nextInt(55), 200 + RNG.nextInt(55)));
            int x = RNG.nextInt(w), y = RNG.nextInt(h);
            g.drawLine(x, y, x + RNG.nextInt(10) - 5, y + RNG.nextInt(10) - 5);
        }

        // vẽ chữ + nhẹ nhàng warping
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 32);
        int x = 12;
        for (char ch : code.toCharArray()) {
            double angle = Math.toRadians(RNG.nextInt(40) - 20);
            AffineTransform old = g.getTransform();
            g.rotate(angle, x, 30);
            g.setColor(new Color(60 + RNG.nextInt(80), 60 + RNG.nextInt(80), 60 + RNG.nextInt(80)));
            g.setFont(font);
            g.drawString(String.valueOf(ch), x, 33 + RNG.nextInt(6) - 3);
            g.setTransform(old);
            x += 22 + RNG.nextInt(8);
        }

        // vài đường cắt
        g.setColor(new Color(160, 160, 160));
        for (int i = 0; i < 2; i++) {
            int y = 10 + RNG.nextInt(h - 20);
            g.drawArc(-RNG.nextInt(30), y, w + RNG.nextInt(30), 30, RNG.nextInt(30), 180 + RNG.nextInt(60));
        }
        g.dispose();
        return img;
    }

    private static byte[] toPNG(BufferedImage img) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    private static String sha256(String s) {
        try {
            var md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(d);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
