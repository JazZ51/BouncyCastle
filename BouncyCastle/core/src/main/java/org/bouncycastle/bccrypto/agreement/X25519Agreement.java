package org.bouncycastle.bccrypto.agreement;

import org.bouncycastle.bccrypto.CipherParameters;
import org.bouncycastle.bccrypto.RawAgreement;
import org.bouncycastle.bccrypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.bccrypto.params.X25519PublicKeyParameters;

public final class X25519Agreement
    implements RawAgreement
{
    private X25519PrivateKeyParameters privateKey;

    public void init(CipherParameters parameters)
    {
        this.privateKey = (X25519PrivateKeyParameters)parameters;
    }

    public int getAgreementSize()
    {
        return X25519PrivateKeyParameters.SECRET_SIZE;
    }

    public void calculateAgreement(CipherParameters publicKey, byte[] buf, int off)
    {
        privateKey.generateSecret((X25519PublicKeyParameters)publicKey, buf, off);
    }
}