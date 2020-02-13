package org.bouncycastle.bccrypto.tls;

/**
 * RFC 2246
 * <p>
 * Note that the values here are implementation-specific and arbitrary. It is recommended not to
 * depend on the particular values (e.g. serialization).
 *
 * @deprecated Migrate to the (D)TLS API in org.bouncycastle.tls (bctls jar).
 */
public class CipherType
{
    public static final int stream = 0;
    public static final int block = 1;

    /*
     * RFC 5246
     */
    public static final int aead = 2;
}