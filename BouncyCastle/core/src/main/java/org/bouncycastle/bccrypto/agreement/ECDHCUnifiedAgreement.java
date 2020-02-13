package org.bouncycastle.bccrypto.agreement;

import java.math.BigInteger;

import org.bouncycastle.bccrypto.CipherParameters;
import org.bouncycastle.bccrypto.params.ECDHUPrivateParameters;
import org.bouncycastle.bccrypto.params.ECDHUPublicParameters;
import org.bouncycastle.bcutil.Arrays;
import org.bouncycastle.bcutil.BigIntegers;

/**
 * EC Unified static/ephemeral agreement as described in NIST SP 800-56A using EC co-factor Diffie-Hellman.
 */
public class ECDHCUnifiedAgreement
{
    private ECDHUPrivateParameters privParams;

    public void init(
        CipherParameters key)
    {
        this.privParams = (ECDHUPrivateParameters)key;
    }

    public int getFieldSize()
    {
        return (privParams.getStaticPrivateKey().getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    public byte[] calculateAgreement(CipherParameters pubKey)
    {
        ECDHUPublicParameters pubParams = (ECDHUPublicParameters)pubKey;

        ECDHCBasicAgreement sAgree = new ECDHCBasicAgreement();
        ECDHCBasicAgreement eAgree = new ECDHCBasicAgreement();

        sAgree.init(privParams.getStaticPrivateKey());

        BigInteger sComp = sAgree.calculateAgreement(pubParams.getStaticPublicKey());

        eAgree.init(privParams.getEphemeralPrivateKey());

        BigInteger eComp = eAgree.calculateAgreement(pubParams.getEphemeralPublicKey());

        return Arrays.concatenate(
            BigIntegers.asUnsignedByteArray(this.getFieldSize(), eComp),
            BigIntegers.asUnsignedByteArray(this.getFieldSize(), sComp));
    }
}