package com.distrimind.bouncycastle.tls.crypto.impl.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;

import com.distrimind.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.distrimind.bouncycastle.math.ec.ECCurve;
import com.distrimind.bouncycastle.math.ec.ECPoint;
import com.distrimind.bouncycastle.tls.AlertDescription;
import com.distrimind.bouncycastle.tls.NamedGroup;
import com.distrimind.bouncycastle.tls.TlsFatalAlert;
import com.distrimind.bouncycastle.tls.crypto.TlsAgreement;
import com.distrimind.bouncycastle.tls.crypto.TlsCryptoException;
import com.distrimind.bouncycastle.tls.crypto.TlsECConfig;
import com.distrimind.bouncycastle.tls.crypto.TlsECDomain;

/**
 * EC domain class for generating key pairs and performing key agreement.
 */
public class JceTlsECDomain
    implements TlsECDomain
{
    protected final JcaTlsCrypto crypto;
    protected final TlsECConfig ecConfig;
    protected final ECParameterSpec ecSpec;
    protected final ECCurve ecCurve;

    public JceTlsECDomain(JcaTlsCrypto crypto, TlsECConfig ecConfig)
    {
        int namedGroup = ecConfig.getNamedGroup();
        if (NamedGroup.refersToAnECDSACurve(namedGroup))
        {
            ECParameterSpec spec = ECUtil.getECParameterSpec(crypto, NamedGroup.getName(namedGroup));
            if (null != spec)
            {
                this.crypto = crypto;
                this.ecConfig = ecConfig;
                this.ecSpec =  spec;
                this.ecCurve = ECUtil.convertCurve(spec.getCurve(), spec.getOrder(), spec.getCofactor());
                return;
            }
        }

        throw new IllegalArgumentException("NamedGroup not supported: " + NamedGroup.getText(namedGroup));
    }

    public JceTlsSecret calculateECDHAgreement(PrivateKey privateKey, PublicKey publicKey)
        throws IOException
    {
        try
        {
            /*
             * RFC 4492 5.10. Note that this octet string (Z in IEEE 1363 terminology) as output by
             * FE2OSP, the Field Element to Octet String Conversion Primitive, has constant length for
             * any given field; leading zeros found in this octet string MUST NOT be truncated.
             *
             * We use the convention established by the JSSE to signal this by asking for "TlsPremasterSecret".
             */
            byte[] secret = crypto.calculateKeyAgreement("ECDH", privateKey, publicKey, "TlsPremasterSecret");

            return crypto.adoptLocalSecret(secret);
        }
        catch (GeneralSecurityException e)
        {
            throw new TlsCryptoException("cannot calculate secret", e);
        }
    }

    public TlsAgreement createECDH()
    {
        return new JceTlsECDH(this);
    }

    public ECPoint decodePoint(byte[] encoding)
        throws IOException
    {
        return ecCurve.decodePoint(encoding);
    }

    public PublicKey decodePublicKey(byte[] encoding)
        throws IOException
    {
        try
        {
            ECPoint point = decodePoint(encoding).normalize();
            BigInteger x = point.getAffineXCoord().toBigInteger();
            BigInteger y = point.getAffineYCoord().toBigInteger();

            ECPublicKeySpec keySpec = new ECPublicKeySpec(new java.security.spec.ECPoint(x, y), ecSpec);

            KeyFactory keyFact = crypto.getHelper().createKeyFactory("EC");
            return keyFact.generatePublic(keySpec);
        }
        catch (Exception e)
        {
            throw new TlsFatalAlert(AlertDescription.illegal_parameter, e);
        }
    }

    public byte[] encodePoint(ECPoint point) throws IOException
    {
        return point.getEncoded(false);
    }

    public byte[] encodePublicKey(PublicKey publicKey) throws IOException
    {
        // TODO Add new com.distrimind.bouncycastle.util.ECPointHolder with getEncodedPoint(boolean)

        if (publicKey instanceof com.distrimind.bouncycastle.jce.interfaces.ECPublicKey)
        {
            return encodePoint(((com.distrimind.bouncycastle.jce.interfaces.ECPublicKey)publicKey).getQ());
        }

        if (publicKey instanceof java.security.interfaces.ECPublicKey)
        {
            java.security.spec.ECPoint w = ((java.security.interfaces.ECPublicKey)publicKey).getW();
            return encodePoint(ecCurve.createPoint(w.getAffineX(), w.getAffineY()));
        }

        SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
        return spki.getPublicKeyData().getOctets();
    }

    public KeyPair generateKeyPair()
    {
        try
        {
            KeyPairGenerator keyPairGenerator = crypto.getHelper().createKeyPairGenerator("EC");
            keyPairGenerator.initialize(ecSpec, crypto.getSecureRandom());
            return keyPairGenerator.generateKeyPair();
        }
        catch (GeneralSecurityException e)
        {
            throw Exceptions.illegalStateException("unable to create key pair: " + e.getMessage(), e);
        }
    }
}
