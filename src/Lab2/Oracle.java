package Lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Oracle {
    private final FileEncrypterDecrypter fileEncrypterDecrypter;
    private final OracleMode mode;
    private Random generator;

    public Oracle(FileEncrypterDecrypter fileEncrypterDecrypter, OracleMode mode) {
        this.generator = new Random();
        this.fileEncrypterDecrypter = fileEncrypterDecrypter;
        this.mode = mode;
    }

    List<String> useOracle(int q, List<String> messages) throws Exception {
        if (mode != OracleMode.ORACLE_MODE)
            throw new Exception("Wrong mode!");
        if (messages.size() != q)
            throw new Exception("Wrong size of message collection!");

        List<String> outputs = new ArrayList<>(q);
        for (String message : messages) {
            String out = "out_" + message;
            outputs.add(out);
            fileEncrypterDecrypter.encryptFile(message, out);
        }
        return outputs;
    }

    void useChallenge(String m0, String m1, String outputPath) throws Exception {
        if (mode != OracleMode.CHALLENGE_MODE)
            throw new Exception("Wrong mode!");
        int bit = generator.nextInt(2);
        if (bit == 0) {
            fileEncrypterDecrypter.encryptFile(m0, outputPath);
        } else {
            fileEncrypterDecrypter.encryptFile(m1, outputPath);
        }
    }
}
