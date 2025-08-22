package com.salq.backend.config;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PrivateKeySignature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;

public class PdfSigningUtil {

    private static final String KEYSTORE_PASSWORD = "A438dfsqpzgjfe3q0";
    private static final String KEY_ALIAS = "salq";
    private static final String KEY_PASSWORD = "A438dfsqpzgjfe3q0";
    private static final String KEYSTORE_PATH = "keystore.p12";
    private static final String LOGO_PATH = "static/images/logo.png";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static byte[] signPdf(byte[] pdfData) throws GeneralSecurityException, IOException, DocumentException {

        System.out.println("Dig sign Method called....");
        KeyStore ks = loadKeyStore();

        PrivateKey pk = (PrivateKey) ks.getKey(KEY_ALIAS, KEY_PASSWORD.toCharArray());
        Certificate[] chain = ks.getCertificateChain(KEY_ALIAS);

        PdfReader reader = new PdfReader(pdfData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfStamper stamper = PdfStamper.createSignature(reader, baos, '\0', null, true);
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();

        appearance.setReason("Approved for Distribution");
        appearance.setLocation("India");
        appearance.setSignDate(Calendar.getInstance());
        appearance.setContact("SalQ");

        Rectangle rect = new Rectangle(36, 36, 300, 150);
        String uniqueFieldName = "Signature_" + System.currentTimeMillis();
        appearance.setVisibleSignature(rect, reader.getNumberOfPages(), uniqueFieldName);

        appearance.setAcro6Layers(true);

        PdfTemplate layer2 = appearance.getLayer(2);

        float rectWidth  = rect.getWidth();
        float rectHeight = rect.getHeight();
        float columnPadding = 5f;
        float logoColumnWidth = rectWidth * 0.4f;   // 40% for logo
        float textColumnWidth = rectWidth * 0.6f;   // 60% for text

        try {
            // Load and place logo in left column
            Image logo = Image.getInstance(new ClassPathResource(LOGO_PATH).getURL());
            logo.scaleToFit(logoColumnWidth - 2 * columnPadding, rectHeight - 2 * columnPadding);
            logo.setAbsolutePosition(columnPadding,
                    rectHeight - logo.getScaledHeight() - columnPadding);
            layer2.addImage(logo);

            // Text content to be placed in right column
            String text = "Signed by Narayana Groups\n" +
                    "Reason: Approved for Distribution\n" +
                    "Location: India\n" +
                    "Issued by: SalQ\n" +
                    "Date: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            ColumnText ct = new ColumnText(layer2);
            com.itextpdf.text.Font font = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 8);

            ct.setSimpleColumn(
                    new com.itextpdf.text.Paragraph(text, font),
                    logoColumnWidth + columnPadding,    // Left boundary (start of text column)
                    columnPadding,                     // Bottom
                    rectWidth - columnPadding,         // Right
                    rectHeight - columnPadding,        // Top
                    12,                                 // Leading
                    com.itextpdf.text.Element.ALIGN_LEFT
            );
            ct.go();

        } catch (Exception e) {
            System.err.println("Logo not found, signing without logo");

            String text = "\nSigned by Narayana Groups\n" +
                    "Reason: Approved for Distribution\n" +
                    "Location: India\n" +
                    "Issued by: SalQ\n" +
                    "Date: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            appearance.setLayer2Font(new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 8));
            appearance.setLayer2Text(text);
        }

        appearance.setLayer4Text("");

        ExternalDigest digest = new BouncyCastleDigest();
        PrivateKeySignature pks = new PrivateKeySignature(pk, "SHA-256", BouncyCastleProvider.PROVIDER_NAME);
        MakeSignature.signDetached(appearance, digest, pks, chain, null, null, null, 0,
                MakeSignature.CryptoStandard.CADES);

        reader.close();
        stamper.close();

        return baos.toByteArray();
    }

    private static KeyStore loadKeyStore() throws KeyStoreException, IOException,
            CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
        try (InputStream is = new ClassPathResource(KEYSTORE_PATH).getInputStream()) {
            ks.load(is, KEYSTORE_PASSWORD.toCharArray());
        }
        return ks;
    }
}
