package com.distrimind.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;

import com.distrimind.bouncycastle.crypto.AsymmetricBlockCipher;
import com.distrimind.bouncycastle.crypto.CipherParameters;
import com.distrimind.bouncycastle.crypto.Digest;
import com.distrimind.bouncycastle.crypto.digests.MD5Digest;
import com.distrimind.bouncycastle.crypto.digests.RIPEMD128Digest;
import com.distrimind.bouncycastle.crypto.digests.RIPEMD160Digest;
import com.distrimind.bouncycastle.crypto.digests.SHA1Digest;
import com.distrimind.bouncycastle.crypto.digests.SHA224Digest;
import com.distrimind.bouncycastle.crypto.digests.SHA256Digest;
import com.distrimind.bouncycastle.crypto.digests.SHA384Digest;
import com.distrimind.bouncycastle.crypto.digests.SHA512Digest;
import com.distrimind.bouncycastle.crypto.digests.WhirlpoolDigest;
import com.distrimind.bouncycastle.crypto.digests.SHA512tDigest;
import com.distrimind.bouncycastle.crypto.engines.RSABlindedEngine;
import com.distrimind.bouncycastle.crypto.signers.ISO9796d2Signer;
import com.distrimind.bouncycastle.crypto.signers.X931Signer;

public class X931SignatureSpi
    extends Signature
{
    private X931Signer signer;

    protected X931SignatureSpi(
        Digest digest,
        AsymmetricBlockCipher cipher)
    {
        super(digest.getAlgorithmName() + "withRSA/X9.31");

        signer = new X931Signer(cipher, digest);
    }

    protected void engineInitVerify(
        PublicKey publicKey)
        throws InvalidKeyException
    {
        CipherParameters param = RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey);

        signer.init(false, param);
    }

    protected void engineInitSign(
        PrivateKey privateKey)
        throws InvalidKeyException
    {
        CipherParameters param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey);

        signer.init(true, param);
    }

    protected void engineUpdate(
        byte    b)
        throws SignatureException
    {
        signer.update(b);
    }

    protected void engineUpdate(
        byte[]  b,
        int     off,
        int     len) 
        throws SignatureException
    {
        signer.update(b, off, len);
    }

    protected byte[] engineSign()
        throws SignatureException
    {
        try
        {
            byte[]  sig = signer.generateSignature();

            return sig;
        }
        catch (Exception e)
        {
            throw new SignatureException(e.toString());
        }
    }

    protected boolean engineVerify(
        byte[]  sigBytes) 
        throws SignatureException
    {
        boolean yes = signer.verifySignature(sigBytes);

        return yes;
    }

    protected void engineSetParameter(
        AlgorithmParameterSpec params)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated replaced with <a href = "#engineSetParameter(java.security.spec.AlgorithmParameterSpec)">
     */
    protected void engineSetParameter(
        String param,
        Object value)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    protected Object engineGetParameter(
        String param)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    static public class RIPEMD128WithRSAEncryption
        extends X931SignatureSpi
    {
        public RIPEMD128WithRSAEncryption()
        {
            super(new RIPEMD128Digest(), new RSABlindedEngine());
        }
    }

    static public class RIPEMD160WithRSAEncryption
        extends X931SignatureSpi
    {
        public RIPEMD160WithRSAEncryption()
        {
            super(new RIPEMD160Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA1WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA1WithRSAEncryption()
        {
            super(new SHA1Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA224WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA224WithRSAEncryption()
        {
            super(new SHA224Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA256WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA256WithRSAEncryption()
        {
            super(new SHA256Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA384WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA384WithRSAEncryption()
        {
            super(new SHA384Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA512WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA512WithRSAEncryption()
        {
            super(new SHA512Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA512_224WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA512_224WithRSAEncryption()
        {
            super(new SHA512tDigest(224), new RSABlindedEngine());
        }
    }

    static public class SHA512_256WithRSAEncryption
        extends X931SignatureSpi
    {
        public SHA512_256WithRSAEncryption()
        {
            super(new SHA512tDigest(256), new RSABlindedEngine());
        }
    }

    static public class WhirlpoolWithRSAEncryption
        extends X931SignatureSpi
    {
        public WhirlpoolWithRSAEncryption()
        {
            super(new WhirlpoolDigest(), new RSABlindedEngine());
        }
    }
}
