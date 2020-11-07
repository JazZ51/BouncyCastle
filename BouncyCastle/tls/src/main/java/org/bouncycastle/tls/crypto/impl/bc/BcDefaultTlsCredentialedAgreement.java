package com.distrimind.bouncycastle.tls.crypto.impl.bc;

import java.io.IOException;

import com.distrimind.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.distrimind.bouncycastle.crypto.params.DHPrivateKeyParameters;
import com.distrimind.bouncycastle.crypto.params.DHPublicKeyParameters;
import com.distrimind.bouncycastle.crypto.params.ECPrivateKeyParameters;
import com.distrimind.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.distrimind.bouncycastle.tls.Certificate;
import com.distrimind.bouncycastle.tls.TlsCredentialedAgreement;
import com.distrimind.bouncycastle.tls.crypto.TlsCertificate;
import com.distrimind.bouncycastle.tls.crypto.TlsSecret;

/**
 * Credentialed class generating agreed secrets from a peer's public key for our end of the TLS connection using the BC light-weight API.
 */
public class BcDefaultTlsCredentialedAgreement
    implements TlsCredentialedAgreement
{
    protected TlsCredentialedAgreement agreementCredentials;

    public BcDefaultTlsCredentialedAgreement(BcTlsCrypto crypto, Certificate certificate, AsymmetricKeyParameter privateKey)
    {
        if (crypto == null)
        {
            throw new IllegalArgumentException("'crypto' cannot be null");
        }
        if (certificate == null)
        {
            throw new IllegalArgumentException("'certificate' cannot be null");
        }
        if (certificate.isEmpty())
        {
            throw new IllegalArgumentException("'certificate' cannot be empty");
        }
        if (privateKey == null)
        {
            throw new IllegalArgumentException("'privateKey' cannot be null");
        }
        if (!privateKey.isPrivate())
        {
            throw new IllegalArgumentException("'privateKey' must be private");
        }

        if (privateKey instanceof DHPrivateKeyParameters)
        {
            agreementCredentials = new DHCredentialedAgreement(crypto, certificate, (DHPrivateKeyParameters)privateKey);
        }
        else if (privateKey instanceof ECPrivateKeyParameters)
        {
            agreementCredentials = new ECCredentialedAgreement(crypto, certificate, (ECPrivateKeyParameters)privateKey);
        }
        else
        {
            throw new IllegalArgumentException("'privateKey' type not supported: "
                + privateKey.getClass().getName());
        }
    }

    public Certificate getCertificate()
    {
        return agreementCredentials.getCertificate();
    }

    public TlsSecret generateAgreement(TlsCertificate peerCertificate)
        throws IOException
    {
        return agreementCredentials.generateAgreement(peerCertificate);
    }

    private class DHCredentialedAgreement
        implements TlsCredentialedAgreement
    {
        final BcTlsCrypto crypto;
        final Certificate certificate;
        final DHPrivateKeyParameters privateKey;

        DHCredentialedAgreement(BcTlsCrypto crypto, Certificate certificate, DHPrivateKeyParameters privateKey)
        {
            this.crypto = crypto;
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        public TlsSecret generateAgreement(TlsCertificate peerCertificate) throws IOException
        {
            DHPublicKeyParameters peerPublicKey = BcTlsCertificate.convert(crypto, peerCertificate).getPubKeyDH();
            return BcTlsDHDomain.calculateDHAgreement(crypto, privateKey, peerPublicKey, false);
        }

        public Certificate getCertificate()
        {
            return certificate;
        }
    }

    private class ECCredentialedAgreement
        implements TlsCredentialedAgreement
    {
        final BcTlsCrypto crypto;
        final Certificate certificate;
        final ECPrivateKeyParameters privateKey;

        ECCredentialedAgreement(BcTlsCrypto crypto, Certificate certificate, ECPrivateKeyParameters privateKey)
        {
            this.crypto = crypto;
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        public TlsSecret generateAgreement(TlsCertificate peerCertificate) throws IOException
        {
            ECPublicKeyParameters peerPublicKey = BcTlsCertificate.convert(crypto, peerCertificate).getPubKeyEC();
            return BcTlsECDomain.calculateBasicAgreement(crypto, privateKey, peerPublicKey);
        }

        public Certificate getCertificate()
        {
            return certificate;
        }
    }
}
