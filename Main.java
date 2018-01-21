
public class Main {
    public static void main(String[] args) {
        UTXOPool utxoPool = new UTXOPool();
        TxHandler txHandler = new TxHandler(utxoPool);
    }
}
