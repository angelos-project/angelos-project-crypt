package org.angproj.crypto.c

/**
 * General interface fdr classes that produce and validate commitments.
 */
public interface Committer {
    /**
     * Generate a commitment for the passed in message.
     *
     * @param message the message to be committed to,
     * @return a Commitment
     */
    public fun commit(message: ByteArray): Commitment

    /**
     * Return true if the passed in commitment represents a commitment to the passed in maessage.
     *
     * @param commitment a commitment previously generated.
     * @param message the message that was expected to have been committed to.
     * @return true if commitment matches message, false otherwise.
     */
    public fun isRevealed(commitment: Commitment, message: ByteArray): Boolean
}
