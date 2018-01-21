import java.util.ArrayList;

public class MaxFeeTxHandler{

    private UTXOPool currentUTXOPool;

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    public MaxFeeTxHandler(UTXOPool utxoPool) {
        this.currentUTXOPool = new UTXOPool(utxoPool);
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
        double inputSum = 0;
        double outputSum = 0;

        UTXOPool utxoPool = new UTXOPool();

        for (int inputIndex = 0; inputIndex < tx.numInputs(); inputIndex++) {
            Transaction.Input txInput = tx.getInput(inputIndex);
            UTXO utxo = new UTXO(txInput.prevTxHash, txInput.outputIndex);
            Transaction.Output txOutput = currentUTXOPool.getTxOutput(utxo);
            /*Step #1*/
            if (!this.currentUTXOPool.contains(utxo)) {
                return false;
            }

            /*Step #2*/
            if (!Crypto.verifySignature(txOutput.address, tx.getRawDataToSign(inputIndex), txInput.signature)) {
                return false;
            }

            /*Step #3*/
            if (utxoPool.contains(utxo)) {
                return false;
            }

            utxoPool.addUTXO(utxo, txOutput);
            inputSum += txOutput.value;
        }

        for (Transaction.Output txOutput : tx.getOutputs()) {
            /*Step #4*/
            if (txOutput.value < 0) {
                return false;
            }

            outputSum += txOutput.value;
        }

        /*Step #5*/
        return !(inputSum < outputSum);
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        ArrayList<Transaction> txList = new ArrayList<>();
        double maxSum = 0;
        Transaction[] maxSumTransaction = new Transaction[1];
        for (Transaction currentTransaction : possibleTxs) {
            if (this.isValidTx(currentTransaction)) {
                txList.add(currentTransaction);

                double inputSum = 0;

                for (Transaction.Input input : currentTransaction.getInputs()) {
                    UTXO utxoForRemoving = new UTXO(input.prevTxHash, input.outputIndex);
                    inputSum += currentTransaction.getOutput(input.outputIndex).value;
                    currentUTXOPool.removeUTXO(utxoForRemoving);
                }

                double outputSum = 0;

                for (int txOutputIndex = 0; txOutputIndex < currentTransaction.numOutputs(); txOutputIndex++) {
                    UTXO utxoForAdding = new UTXO(currentTransaction.getHash(), txOutputIndex);
                    currentUTXOPool.addUTXO(utxoForAdding, currentTransaction.getOutput(txOutputIndex));
                    outputSum += currentTransaction.getOutput(txOutputIndex).value;
                }


                double transactionFee = inputSum - outputSum;
                if ( transactionFee > maxSum )
                {
                    maxSum = transactionFee;
                    maxSumTransaction[0] = currentTransaction;
                }
            }
        }

        return maxSumTransaction;
    }

}
