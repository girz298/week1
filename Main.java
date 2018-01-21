import java.security.PublicKey;

public class Main {
    public static void main(String[] args) {
        Transaction transaction = new Transaction();
        transaction.setHash("111222".getBytes());
        transaction.addInput(transaction.getHash(), 0);
        transaction.addOutput(3.2, new PublicKey() {
            @Override
            public String getAlgorithm() {
                return "SHA256";
            }

            @Override
            public String getFormat() {
                return "X.509";
            }

            @Override
            public byte[] getEncoded() {
                return "111222".getBytes();
            }
        });
        transaction.addSignature("111222".getBytes(), 0);

        UTXO utxo = new UTXO(transaction.getHash(), 0);

        UTXOPool utxoPool = new UTXOPool();
        utxoPool.addUTXO(utxo, transaction.getOutput(0));
        TxHandler txHandler = new TxHandler(utxoPool);
        System.out.println(txHandler.isValidTx(transaction));
    }
}
