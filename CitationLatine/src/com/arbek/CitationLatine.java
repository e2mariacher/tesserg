package com.arbek; // "C:\Program Files\Java\jdk-21\bin\java.exe" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\lib\idea_rt.jar=59897" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath C:\Users\ericm\Documents\GitHub\arbek\CitationLatine\out\production\CitationLatine com.arbek.CitationLatine

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

public class CitationLatine {
    static final String IV = "AAAAAAAAAAAAAAAA";
    static final String plaintext = "test text 124";
    static final String encryptionKey = "0123456789abcdef";

    public static void main(String[] args) {
        System.out.println("Citation Latine java.version: " + System.getProperty("java.version"));

        if (args.length == 0) {
            String enc = doZeJob(plaintext, encryptionKey);
            doZeJob(enc, encryptionKey);
        } else {
            File file_input = new File(args[0]);
            if (file_input.isFile()) {
                try {
                    String enc = doZeJob(new String(Files.readAllBytes(Paths.get(args[0]))), args[1]);
                    doZeJob(enc, args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String enc = doZeJob(args[0], args[1]);
                doZeJob(enc, args[1]);
            }
        }
    }

    public static String doZeJob(String text, String clef) {
        String base64encodedString = "";
        try {

            System.out.println("plain:    <---< " + text + " >--->");

            String seizetext = padRightMod16(text);
            System.out.println("seizetext[" + seizetext + "] " + seizetext.length());
            String seizeclef = clef.substring(0, 16);

            byte[] cipher = encrypt(seizetext, seizeclef);

            // Encode using basic encoder
            base64encodedString = Base64.getEncoder().encodeToString(cipher);
            System.out.println(base64encodedString);

            // Decode
            byte[] decipher = Base64.getDecoder().decode(base64encodedString);

            String decrypted = decrypt(decipher, seizeclef);

            System.out.println("decrypt:  " + decrypted);
            if (!text.contains(" ")) {
                System.out.println("decrypt2: " + decrypt(Base64.getDecoder().decode(text), seizeclef));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64encodedString;

    }

    public static String padRight(String s) {
        Random rand = new Random();
        StringBuilder spadded = new StringBuilder();
        for (int i = 1; i < 20; i++) {
            spadded.append(rand.nextInt(4568797 * i));
        }
        spadded.insert(0, rand.nextInt(1223457) + " " + s + " ");
        return spadded.substring(0, s.length() - (s.length() % 16) + 32);
    }

    public static String padRightMod16(String s) {
        return padRight(s);
    }


    public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(byte[] cipherText, String encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
        SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8)));
        return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
    }
}
