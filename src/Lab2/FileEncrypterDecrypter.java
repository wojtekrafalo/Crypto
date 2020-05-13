package Lab2;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class FileEncrypterDecrypter {

    private final boolean isTest;
    //    private SecretKey secretKey;
    private MyKeyStore keyStore;
    private String alias;
    private static int increment = 0;
    private Cipher cipher;
    private static ArrayList<String> dictionary;
    private static byte[] consecutiveIV;
    static int base = 16;

    static {
        dictionary = new ArrayList<>(5);
        dictionary.add("CBC");
        dictionary.add("OFB");
        dictionary.add("CTR");
        dictionary.add("CFB");
        dictionary.add("PCBC");

        consecutiveIV = new byte[base];
        Random random = new Random();
        for (int i = 0; i < base; i++) {
            consecutiveIV[i] = (byte) (random.nextInt(base * base) - base * base / 2);
        }
    }

    public FileEncrypterDecrypter(String modeOfEncryption, String keyStorePath, SecretKey secretKey, boolean isTest) throws NoSuchPaddingException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
        if (dictionary.contains(modeOfEncryption)) {
            this.isTest = isTest;
            this.alias = getNextAlias();

            String pwd = getPassword();
            char[] pwdArray = pwd.toCharArray();

            this.keyStore = new MyKeyStore(KeyStore.getDefaultType(), pwd, keyStorePath);
            keyStore.createEmptyKeyStore();
            keyStore.loadKeyStore();

            KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(pwdArray);
            keyStore.setEntry(alias, secret, protParam);

//            this.secretKey = secretKey;
            String tmpCipher = "AES/" + modeOfEncryption + "/PKCS5Padding";
            this.cipher = Cipher.getInstance(tmpCipher);
        } else throw new NoSuchAlgorithmException(modeOfEncryption);
    }

    private String getPassword() {
        String pwd = "password";
        if (!isTest) {
            System.out.println("Please, give password:");
            pwd = new Scanner(System.in).nextLine();
        }
        return pwd;
    }

    public FileEncrypterDecrypter(String modeOfEncryption, String keyStorePath, String keyValue) throws NoSuchAlgorithmException, NoSuchPaddingException, CertificateException, KeyStoreException, IOException {
        this(modeOfEncryption, keyStorePath, new SecretKeySpec(keyValue.getBytes(), "AES"), false);
    }

    public void encryptString(String content, String outputPath) throws InvalidKeyException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        cipher.init(Cipher.ENCRYPT_MODE, loadSecretKey());
        byte[] iv = cipher.getIV();

        try (
                FileOutputStream fileOut = new FileOutputStream(outputPath);
                CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)
        ) {
            fileOut.write(iv);
            cipherOut.write(content.getBytes());
        }

    }

    public void encryptFile(String inputPath, String outputPath) throws InvalidKeyException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        encrypt(inputPath, outputPath, false);
    }

    private void encrypt(String inputPath, String outputPath, boolean isIVConsecutive) throws InvalidKeyException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        cipher.init(Cipher.ENCRYPT_MODE, loadSecretKey());
        byte[] iv = getIV(isIVConsecutive);

//        String content = Files.readString(Paths.get(inputPath));
        String content = MyFileReader.readFile(inputPath);

        try (
                FileOutputStream fileOut = new FileOutputStream(outputPath);
                CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher)
        ) {
            fileOut.write(iv);
            cipherOut.write(content.getBytes());
        }
    }

    private byte[] getIV(boolean isIVConsecutive) {
        if (!isIVConsecutive) {
            return cipher.getIV();
        }

        for (int i = base - 1; i >= 0; i--) {
            byte ivElement = consecutiveIV[i];
            if ((int) ivElement + 1 >= 127) {
                consecutiveIV[i] = -128;
            } else {
                consecutiveIV[i] += 1;
                break;
            }
        }
//        System.out.println(Arrays.toString(consecutiveIV));
        return consecutiveIV;
    }

    public void encryptFileWithConsecutiveIV(String inputPath, String outputPath) throws InvalidKeyException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        this.encrypt(inputPath, outputPath, true);
    }

    public String decrypt(String inputPath) throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        String content;

        try (FileInputStream fileIn = new FileInputStream(inputPath)) {
            byte[] fileIv = new byte[base];
            fileIn.read(fileIv);
            cipher.init(Cipher.DECRYPT_MODE, loadSecretKey(), new IvParameterSpec(fileIv));

            try (
                    CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
                    InputStreamReader inputReader = new InputStreamReader(cipherIn);
                    BufferedReader reader = new BufferedReader(inputReader)
            ) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                content = sb.toString();
            }

        }
        return content;
    }

    private static String getNextAlias() {
        increment++;
        return String.valueOf(increment);
    }

    private SecretKey loadSecretKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        String pwd = getPassword();
        Key resultKey = keyStore.getKey(alias, pwd.toCharArray());
        return (SecretKey) resultKey;
    }
}