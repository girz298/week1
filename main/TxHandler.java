package main;

import java.util.ArrayList;
import java.util.Iterator;

public class TxHandler {

    private UTXOPool copyOfPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public TxHandler(UTXOPool utxoPool) {
        this.copyOfPool =  new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool,
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        int inputSum = 0;
        int outputSumm = 0;

        for (int txNumber = 0; txNumber < tx.numInputs(); txNumber++) {
            Transaction.Input input = tx.getInput(txNumber);
            Transaction.Output output = tx.getOutput(txNumber);
            UTXO utxo = new UTXO(tx.getHash(), txNumber);

            if (output.value < 0) {
                return false;
            }

            if (!this.copyOfPool.contains(utxo)){
                return false;
            }

            if (!Crypto.verifySignature(output.address, tx.getRawDataToSign(txNumber), tx.getRawTx())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
        return possibleTxs;
    }

}
