package Lab2;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Oracle {
    private final FileEncrypterDecrypter fileEncrypterDecrypter;
    private Random generator;
    private int generatedBit;

    public Oracle(FileEncrypterDecrypter fileEncrypterDecrypter) {
        this.generator = new Random();
        this.fileEncrypterDecrypter = fileEncrypterDecrypter;
    }

    public List<String> useOracle(List<String> messages) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException {

        List<String> outputs = new ArrayList<>(messages.size());
        for (String message : messages) {
            String out = "out_" + message;
            outputs.add(out);
            fileEncrypterDecrypter.encryptFile(message, out);
        }
        return outputs;
    }

    public void useChallenge(String m0, String m1, String outputPath) throws IOException, InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        this.generatedBit = generator.nextInt(2);
        if (this.generatedBit == 0) {
            fileEncrypterDecrypter.encryptFile(m0, outputPath);
        } else {
            fileEncrypterDecrypter.encryptFile(m1, outputPath);
        }
    }

    public boolean guessChallenge(int bit) {
        return bit == this.generatedBit;
    }
}
