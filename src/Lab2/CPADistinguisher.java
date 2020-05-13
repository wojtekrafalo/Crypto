package Lab2;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CPADistinguisher {
    private Oracle oracle;

    public CPADistinguisher(FileEncrypterDecrypter fed) {
        this.oracle = new Oracle(fed);
    }

    public boolean distinguish(String inputPath0, String inputPath1, String outputPath) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException {

        int base = FileEncrypterDecrypter.base;
        oracle.useChallenge(inputPath0, inputPath1, outputPath, true);

        FileInputStream fileIn = new FileInputStream(outputPath);
        byte[] fileIv = new byte[base];
        fileIn.read(fileIv);
        byte[] newIV = predictIV(fileIv, base);
//        System.out.println(Arrays.toString(fileIv));

        String inputContent0 = MyFileReader.readFile(inputPath0);
        String inputContent1 = MyFileReader.readFile(inputPath1);

        byte[] byteContent0 = inputContent0.getBytes();
        byte[] byteContent1 = inputContent1.getBytes();

        for (int i = 0; i < byteContent0.length; i++) {
            byteContent0[i] = (byte) (byteContent0[i] ^ newIV[i % base]);
            byteContent0[i] = (byte) (byteContent0[i] ^ fileIv[i % base]);
        }

        for (int i = 0; i < byteContent1.length; i++) {
            byteContent1[i] = (byte) (byteContent1[i] ^ newIV[i % base]);
            byteContent1[i] = (byte) (byteContent1[i] ^ fileIv[i % base]);
        }
//        System.out.println("Lengths: newIV: " + newIV.length + ", byteContent0: " + byteContent0.length + ", byteContent1: " + byteContent1.length);

        MyFileReader.writeToFile(inputPath0 + "_dist", new String(byteContent0));
        List<String> files = new ArrayList<>(1);
        files.add(inputPath0 + "_dist");

        files = oracle.useOracle(files, true);

        return false;
    }

    private byte[] predictIV(byte[] fileIV, int base) {
        byte[] newIV = new byte[base];
        for (int i=0; i<base; i++) {
            newIV[i] = fileIV[i];
        }
        for (int i = base - 1; i >= 0; i--) {
            byte ivElement = newIV[i];
            if ((int) ivElement + 1 >= 127) {
                newIV[i] = -128;
            } else {
                newIV[i] += 1;
                break;
            }
        }
        return newIV;
    }

}
