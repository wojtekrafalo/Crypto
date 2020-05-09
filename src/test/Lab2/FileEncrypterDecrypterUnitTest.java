package test.Lab2;


import Lab2.FileEncrypterDecrypter;
import org.junit.Assert;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FileEncrypterDecrypterUnitTest {

    private String encryptAndDecrypt (String content, String path, String mode) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

        FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey, mode);
        fileEncrypterDecrypter.encryptString(content, path);

        return fileEncrypterDecrypter.decrypt(path);
    }

    private String encryptFromFileAndDecrypt (String inputPath, String outputPath, String mode) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeyException, InvalidAlgorithmParameterException {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

        FileEncrypterDecrypter fileEncrypterDecrypter = new FileEncrypterDecrypter(secretKey, mode);
        fileEncrypterDecrypter.encryptFile(inputPath, outputPath);

        return fileEncrypterDecrypter.decrypt(outputPath);
    }

    private void cleanUpFile(String path) {
        new File(path).delete();
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CBC() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "CBC";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_OFB() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "OFB";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CFB() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "CFB";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CTR() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "CTR";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenStringAndFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_PCBC() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        String originalContent = "foobar";
        String filePath = "baz.enc";
        String mode = "PCBC";

        String decryptedContent = encryptAndDecrypt(originalContent, filePath, mode);
        assertThat(decryptedContent, is(originalContent));
        cleanUpFile(filePath);
    }

    @Test
    public void givenFilename_whenEncryptingIntoFile_andDecryptingFileAgain_thenOriginalStringIsReturned_CBC() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        String inputPath = "C:\\myTrash\\pwr\\Crypto\\Crypto1\\src\\test\\files\\message.txt";
        String outputPath = "baz.enc";
        String mode = "CBC";
        String inputContent = Files.readString(Paths.get(inputPath));

        String decryptedContent = encryptFromFileAndDecrypt(inputPath, outputPath, mode);
        assertThat(decryptedContent, is(inputContent));
        cleanUpFile(outputPath);
    }

    /**
     * Tests below written during programming.
     * @throws IOException
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
     * @throws IOException
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
}