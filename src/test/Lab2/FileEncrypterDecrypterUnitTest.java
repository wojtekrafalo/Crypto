package test.Lab2;


import Lab2.*;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FileEncrypterDecrypterUnitTest {
    private static String defaultKeyStorePath = "keyStorePath_out";

    private String encryptAndDecrypt(String content, String path, String mode) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
//        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        String keyValue = "key_value_blank_";
        SecretKey secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");

        FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(mode, defaultKeyStorePath, secretKey, true);
        fileEncrypterDecrypter.encryptString(content, path);

        return fileEncrypterDecrypter.decrypt(path);
    }

    private String encryptFromFileAndDecrypt(String inputPath, String outputPath, String mode) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
//        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        String keyValue = "key_value_blank_";
        SecretKey secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");

        FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(mode, defaultKeyStorePath, secretKey, true);
        fileEncrypterDecrypter.encryptFile(inputPath, outputPath);

        return fileEncrypterDecrypter.decrypt(outputPath);
    }

    private void cleanUpFile(String path) {
        new File(path).delete();
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CBC() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "CBC";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_OFB() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "OFB";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CFB() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "CFB";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CTR() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "CTR";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_PCBC() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "PCBC";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenFile_Encrypting_DecryptingAgain_thenOriginalFileIsReturned_CBC() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String inputPath = "C:\\myTrash\\pwr\\Crypto\\Crypto1\\src\\test\\files\\message.txt";
        String outputPath = "baz.enc";
        String mode = "CBC";

//        String inputContent = Files.readString(Paths.get(inputPath));
        String inputContent = MyFileReader.readFile(inputPath);

        String decryptedContent = encryptFromFileAndDecrypt(inputPath, outputPath, mode);
        assertThat(decryptedContent, is(inputContent));
        cleanUpFile(outputPath);
    }

    @Test
    public void testKeystore_saveKey_loadKey_compareResults() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableEntryException {

        String pwd = "password";
        String alias = "normal_alias";
        String keyValue = "key_value";
        System.out.println("Key_normal: " + Arrays.toString(keyValue.getBytes()));
        MyKeyStore keyStore = new MyKeyStore(KeyStore.getDefaultType(), pwd, "keystoreFile_out");
        keyStore.createEmptyKeyStore();
        keyStore.loadKeyStore();

        SecretKey secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");
        char[] pwdArray = pwd.toCharArray();

        KeyStore.SecretKeyEntry secret = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(pwdArray);
        keyStore.setEntry(alias, secret, protParam);

        KeyStore.Entry loadedEntry = keyStore.getEntry(alias);
        loadedEntry.getAttributes().forEach(attribute -> System.out.println("attr_normal: " + attribute.getName() + " " + attribute.getValue()));

        Key resultKey = keyStore.getKey(alias, pwd.toCharArray());
        System.out.println("Key_normal: " + Arrays.toString(resultKey.getEncoded()));
        System.out.println("Key_normal: " + new String((resultKey.getEncoded())));

        String resultKeyValue = new String((resultKey.getEncoded()));
        Assert.assertEquals(keyValue, resultKeyValue);
    }

    @Test
    public void testOracle() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, IOException, UnrecoverableKeyException, InvalidKeyException {
        String keyValue = "key_value_blank_";
        SecretKey key = new SecretKeySpec(keyValue.getBytes(), "AES");
        FileEncrypterDecrypter fed = new FileEncrypterDecrypter("CBC", defaultKeyStorePath, key, true);
        Oracle oracle = new Oracle(fed);
        List<String> messages = new ArrayList<>(3);

        String in1 = "inputPath1";
        String in2 = "inputPath2";
        messages.add(in1);
        messages.add(in2);
        messages.add("inputPath3");
        List<String> outputs = oracle.useOracle(messages, false);

        oracle.useChallenge(in1, in2, "outputPath_out", false);
        boolean isCorrect = oracle.guessChallenge(1);
        System.out.println(isCorrect);

        Assert.assertEquals(outputs.get(0), "out_" + in1);
        Assert.assertEquals(outputs.get(1), "out_" + in2);
    }

    @Test
    public void givenFile_EncryptingWithConsecutiveIV() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        String inputPath = "C:\\myTrash\\pwr\\Crypto\\Crypto1\\src\\test\\files\\message.txt";
        String mode = "CBC";

//        String inputContent = Files.readString(Paths.get(inputPath));
        String inputContent = MyFileReader.readFile(inputPath);

        String keyValue = "key_value_blank_";
        SecretKey secretKey = new SecretKeySpec(keyValue.getBytes(), "AES");

        FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(mode, defaultKeyStorePath, secretKey, true);

        String outputPath1 = "baz1.enc";
        fileEncrypterDecrypter.encryptFileWithConsecutiveIV(inputPath, outputPath1);
        String outputPath2 = "baz2.enc";
        fileEncrypterDecrypter.encryptFileWithConsecutiveIV(inputPath, outputPath2);
        String outputPath3 = "baz3.enc";
        fileEncrypterDecrypter.encryptFileWithConsecutiveIV(inputPath, outputPath3);
        String outputPath4 = "baz4.enc";
        fileEncrypterDecrypter.encryptFileWithConsecutiveIV(inputPath, outputPath4);
    }

    @Test
    public void test_StringToByteArray_and_viceVersa() {
        String input = "test_string";
        System.out.println(input);

        byte[] converted = input.getBytes();
        System.out.println(Arrays.toString(converted));

        converted[10]++;
        System.out.println(Arrays.toString(converted));

        String resultString = new String(converted);
        System.out.println(resultString);

        byte a = 5, b = 7;
        byte[] c = new byte[1];
        c[0] = (byte) (a ^ b);
        System.out.println(c[0]);
        System.out.println(new String(c));
    }

    /**
     * Tests below written during programming.
     *
     * @throws IOException an error while reading file.
     */
    @Test
    public void readFileIndirectPath() throws IOException {
        String filePath = "fileTest";
        String fileContent =
                "File content.\n" +
                        "File content 2.\n" +
                        "\n" +
                        "File 3.\n" +
                        "\n" +
                        "No file more.\n" +
                        "\n" +
                        "I said no more!!!";

        FileWriter writer = new FileWriter(filePath);
        writer.write(fileContent);
        writer.close();

        String resultContent = Files.readString(Paths.get(filePath));

        Assert.assertEquals(fileContent, resultContent);
        new File(filePath).delete();
    }

    /**
     * Tests below written during programming.
     *
     * @throws IOException an error while reading file.
     */
    @Test
    public void readFileDirectPath() throws IOException {
        String filePath = "C:\\myTrash\\pwr\\Crypto\\Crypto1\\fileTest";
        String fileContent =
                "File content.\n" +
                        "File content 2.\n" +
                        "\n" +
                        "File 3.\n" +
                        "\n" +
                        "No file more.\n" +
                        "\n" +
                        "I said no more!!!";

        FileWriter writer = new FileWriter(filePath);
        writer.write(fileContent);
        writer.close();

        String resultContent = Files.readString(Paths.get(filePath));

        Assert.assertEquals(fileContent, resultContent);
        new File(filePath).delete();
    }


    /**
     * Tests below written during programming.
     */
    @Test
    public void testScanner() {
        Scanner scan = new Scanner(System.in);
        String s = scan.nextLine();
        int i = scan.nextInt();
        System.out.println(s);
        System.out.println(i);
    }

    /**
     * Tests below written during programming.
     *
     * @throws CertificateException     an error when certificate is not correct.
     * @throws NoSuchAlgorithmException an error when algorithm is not correct.
     * @throws KeyStoreException        an error when keyStore is not correct.
     * @throws NoSuchPaddingException   an error when padding is not correct.
     * @throws IOException              an error while reading file.
     */
    @Test
    public void testStatic() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, IOException {
        FileEncrypterDecrypter fed1 = new FileEncrypterDecrypter("CBC", defaultKeyStorePath, new SecretKeySpec("value".getBytes(), "AES"), true);
        FileEncrypterDecrypter fed2 = new FileEncrypterDecrypter("CBC", defaultKeyStorePath, new SecretKeySpec("value".getBytes(), "AES"), true);
        FileEncrypterDecrypter fed3 = new FileEncrypterDecrypter("CBC", defaultKeyStorePath, new SecretKeySpec("value".getBytes(), "AES"), true);
        FileEncrypterDecrypter fed4 = new FileEncrypterDecrypter("CBC", defaultKeyStorePath, new SecretKeySpec("value".getBytes(), "AES"), true);
    }

    @Test
    public void testIVGeneration() {
        int base = 16;
        Random random = new Random();
        byte[] iv = new byte[base * 20];
        for (int i = 0; i < base * 20; i++) {
            iv[i] = (byte) (random.nextInt(base * base) - base * base / 2);
        }
        System.out.println(Arrays.toString(iv));
    }

    @Test
    public void testIVIncrement() {
        int base = 16;
        Random random = new Random();
        byte[] iv = new byte[base];
        for (int i = 0; i < base; i++) {
            iv[i] = (byte) (random.nextInt(base * base) - base * base / 2);
        }
        System.out.println(Arrays.toString(iv));
        byte lastElement = iv[base - 1];

        for (int i = base - 1; i >= 0; i--) {
            byte ivElement = iv[i];
            if ((int) ivElement + 1 >= 127) {
                iv[i] = -128;
            } else {
                iv[i] += 1;
                break;
            }
        }
        System.out.println(Arrays.toString(iv));
        Assert.assertEquals(iv[base - 1], lastElement + 1);
    }

    public static void main(String[] args) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException, IOException, UnrecoverableKeyException, InvalidKeyException {
        FileEncrypterDecrypter fed = new FileEncrypterDecrypter("CBC", defaultKeyStorePath, new SecretKeySpec("key_value_blank_".getBytes(), "AES"), true);
        CPADistinguisher distinguisher = new CPADistinguisher(fed);

        distinguisher.distinguish("inputPath1", "inputPath2", "outputPath3");



    }
}
