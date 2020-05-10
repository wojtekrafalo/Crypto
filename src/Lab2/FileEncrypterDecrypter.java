package Lab2;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileEncrypterDecrypter {

    private SecretKey secretKey;
    private Cipher cipher;
    private ArrayList<String> dictionary;

    public FileEncrypterDecrypter(String cipher, String keyStorePath, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.createDictionary();

        if (this.dictionary.contains(cipher)) {
            this.secretKey = secretKey;
            String tmpCipher = "AES/" + cipher + "/PKCS5Padding";
            this.cipher = Cipher.getInstance(tmpCipher);
        } else throw new NoSuchAlgorithmException(cipher);
    }

    public FileEncrypterDecrypter (String cipher, String keystorePath) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this(cipher, keystorePath, KeyGenerator.getInstance("AES").generateKey());
    }

    private void createDictionary() {
        dictionary = new ArrayList<>(5);
        dictionary.add("CBC");
        dictionary.add("OFB");
        dictionary.add("CTR");
        dictionary.add("CFB");
        dictionary.add("PCBC");
    }

    public void encryptString(String content, String outputPath) throws InvalidKeyException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();

        try (
                FileOutputStream fileOut = new FileOutputStream(outputPath);
                CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)
        ) {
            fileOut.write(iv);
            cipherOut.write(content.getBytes());
        }

    }

    public void encryptFile(String inputPath, String outputPath) throws InvalidKeyException, IOException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();
//        String content = Files.readString(Paths.get(inputPath));
        FileInputStream inputStream = new FileInputStream(inputPath);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String content = sb.toString();

        try (
                FileOutputStream fileOut = new FileOutputStream(outputPath);
                CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)
        ) {
            fileOut.write(iv);
            cipherOut.write(content.getBytes());
        }
    }

    public String decrypt(String inputPath) throws InvalidAlgorithmParameterException, InvalidKeyException, IOException {

        String content;

        try (FileInputStream fileIn = new FileInputStream(inputPath)) {
            byte[] fileIv = new byte[16];
            fileIn.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));

            try (
                    CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                    InputStreamReader inputReader = new InputStreamReader(cipherIn);
                    BufferedReader reader = new BufferedReader(inputReader)
            ) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                content = sb.toString();
            }

        }
        return content;
    }
}