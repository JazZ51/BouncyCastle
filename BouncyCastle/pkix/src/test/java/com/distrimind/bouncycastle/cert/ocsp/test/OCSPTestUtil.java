package com.distrimind.bouncycastle.cert.ocsp.test;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.KeyGenerator;

import com.distrimind.bouncycastle.asn1.x500.X500Name;
import com.distrimind.bouncycastle.asn1.x509.AccessDescription;
import com.distrimind.bouncycastle.asn1.x509.AuthorityInformationAccess;
import com.distrimind.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import com.distrimind.bouncycastle.asn1.x509.BasicConstraints;
import com.distrimind.bouncycastle.asn1.x509.ExtendedKeyUsage;
import com.distrimind.bouncycastle.asn1.x509.Extension;
import com.distrimind.bouncycastle.asn1.x509.GeneralName;
import com.distrimind.bouncycastle.asn1.x509.KeyPurposeId;
import com.distrimind.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import com.distrimind.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import com.distrimind.bouncycastle.asn1.x509.X509Extensions;
import com.distrimind.bouncycastle.asn1.x509.X509Name;
import com.distrimind.bouncycastle.cert.X509v1CertificateBuilder;
import com.distrimind.bouncycastle.cert.X509v3CertificateBuilder;
import com.distrimind.bouncycastle.cert.bc.BcX509ExtensionUtils;
import com.distrimind.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import com.distrimind.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import com.distrimind.bouncycastle.cert.jcajce.JcaX509v1CertificateBuilder;
import com.distrimind.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import com.distrimind.bouncycastle.operator.ContentSigner;
import com.distrimind.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.distrimind.bouncycastle.x509.X509V3CertificateGenerator;

public class OCSPTestUtil
{
    private static final String BC = "BC";

    public static SecureRandom     rand;
    public static KeyPairGenerator kpg, eckpg;
    public static KeyGenerator     desede128kg;
    public static KeyGenerator     desede192kg;
    public static KeyGenerator     rc240kg;
    public static KeyGenerator     rc264kg;
    public static KeyGenerator     rc2128kg;
    public static BigInteger       serialNumber;
    
    public static final boolean DEBUG = true;
    
    static
    {
        try
        {
            rand = new SecureRandom();

            kpg  = KeyPairGenerator.getInstance("RSA", "BC");
            kpg.initialize(1024, rand);

            serialNumber = new BigInteger("1");

            eckpg = KeyPairGenerator.getInstance("ECDSA", "BC");
            eckpg.initialize(192, rand);
        }
        catch(Exception ex)
        {
            throw new RuntimeException(ex.toString());
        }
    }
    
    public static KeyPair makeKeyPair()
    {
        return kpg.generateKeyPair();
    }

    public static KeyPair makeECKeyPair()
    {
        return eckpg.generateKeyPair();
    }

    public static X509Certificate makeCertificate(KeyPair _subKP, String _subDN)
        throws Exception
    {
        return makeCertificate(_subKP, _subDN, _subKP, _subDN, false);
    }

    public static X509Certificate makeRootCertificate(KeyPair _subKP, String _subDN)
        throws Exception
    {
        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(BC).build(_subKP.getPrivate());
        X509v1CertificateBuilder certGen = new JcaX509v1CertificateBuilder(
            new X500Name(_subDN), allocateSerialNumber(),
            new Date(System.currentTimeMillis() - 50000), new Date(System.currentTimeMillis() + 50000),
            new X500Name(_subDN), _subKP.getPublic());

        return new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));
    }

    public static X509Certificate makeCertificate(KeyPair _subKP, String _subDN, KeyPair _issKP, X509Certificate _issCert, boolean _ca)
        throws Exception
    {
        com.distrimind.bouncycastle.asn1.x509.Certificate cert =  com.distrimind.bouncycastle.asn1.x509.Certificate.getInstance(_issCert.getEncoded());

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(BC).build(_issKP.getPrivate());
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
            cert.getSubject(), allocateSerialNumber(),
            new Date(System.currentTimeMillis() - 50000), new Date(System.currentTimeMillis() + 50000),
            new X500Name(_subDN), _subKP.getPublic());

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        certGen.addExtension(
            Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(_issCert));

        certGen.addExtension(
            Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(_subKP.getPublic()));

        certGen.addExtension(
            Extension.basicConstraints, false, new BasicConstraints(_ca));

        return new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));
    }

    public static X509Certificate makeCertificateWithOCSP(KeyPair _subKP, String _subDN, KeyPair _issKP, X509Certificate _issCert, boolean _ca, String uri)
        throws Exception
    {
        com.distrimind.bouncycastle.asn1.x509.Certificate cert =  com.distrimind.bouncycastle.asn1.x509.Certificate.getInstance(_issCert.getEncoded());

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(BC).build(_issKP.getPrivate());
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
            cert.getSubject(), allocateSerialNumber(),
            new Date(System.currentTimeMillis() - 50000), new Date(System.currentTimeMillis() + 50000),
            new X500Name(_subDN), _subKP.getPublic());

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        certGen.addExtension(
            Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(_issCert));

        certGen.addExtension(
            Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(_subKP.getPublic()));

        certGen.addExtension(
            Extension.basicConstraints, false, new BasicConstraints(_ca));

        certGen.addExtension(
            Extension.authorityInfoAccess, false, new AuthorityInformationAccess(new AccessDescription(AccessDescription.id_ad_ocsp,
                                                        new GeneralName(GeneralName.uniformResourceIdentifier, uri))));

        return new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));
    }

    public static X509Certificate makeCertificate(KeyPair _subKP, String _subDN, KeyPair _issKP, X509Certificate _issCert, KeyPurposeId keyPurpose)
        throws Exception
    {
        com.distrimind.bouncycastle.asn1.x509.Certificate cert =  com.distrimind.bouncycastle.asn1.x509.Certificate.getInstance(_issCert.getEncoded());

        ContentSigner sigGen = new JcaContentSignerBuilder("SHA256WithRSAEncryption").setProvider(BC).build(_issKP.getPrivate());
        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
            cert.getSubject(), allocateSerialNumber(),
            new Date(System.currentTimeMillis() - 50000), new Date(System.currentTimeMillis() + 50000),
            new X500Name(_subDN), _subKP.getPublic());

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        certGen.addExtension(
            Extension.authorityKeyIdentifier, false, extUtils.createAuthorityKeyIdentifier(_issCert));

        certGen.addExtension(
            Extension.subjectKeyIdentifier, false, extUtils.createSubjectKeyIdentifier(_subKP.getPublic()));

        certGen.addExtension(
            Extension.basicConstraints, false, new BasicConstraints(false));

        certGen.addExtension(
            Extension.extendedKeyUsage, false, new ExtendedKeyUsage(keyPurpose));

        return new JcaX509CertificateConverter().setProvider(BC).getCertificate(certGen.build(sigGen));
    }

    public static X509Certificate makeECDSACertificate(KeyPair _subKP,
            String _subDN, KeyPair _issKP, String _issDN)
            throws Exception
    {

        return makeECDSACertificate(_subKP, _subDN, _issKP, _issDN, false);
    }

    public static X509Certificate makeCACertificate(KeyPair _subKP,
            String _subDN, KeyPair _issKP, String _issDN)
            throws Exception
    {

        return makeCertificate(_subKP, _subDN, _issKP, _issDN, true);
    }

    public static X509Certificate makeCertificate(KeyPair _subKP,
            String _subDN, KeyPair _issKP, String _issDN, boolean _ca)
            throws Exception
    {
        return makeCertificate(_subKP,_subDN, _issKP, _issDN, "SHA1withRSA", _ca);
    }

    public static X509Certificate makeECDSACertificate(KeyPair _subKP,
            String _subDN, KeyPair _issKP, String _issDN, boolean _ca)
            throws Exception
    {
        return makeCertificate(_subKP,_subDN, _issKP, _issDN, "SHA1WithECDSA", _ca);
    }

    public static X509Certificate makeCertificate(KeyPair _subKP,
            String _subDN, KeyPair _issKP, String _issDN)
            throws Exception
    {
        return makeCertificate(_subKP, _subDN, _issKP, _issDN, "SHA1withRSA", false);
    }

    public static X509Certificate makeCertificate(KeyPair _subKP,
            String _subDN, KeyPair _issKP, String _issDN, String algorithm, boolean _ca)
            throws Exception
    {

        PublicKey _subPub = _subKP.getPublic();
        PrivateKey _issPriv = _issKP.getPrivate();
        PublicKey _issPub = _issKP.getPublic();

        X509V3CertificateGenerator _v3CertGen = new X509V3CertificateGenerator();

        _v3CertGen.reset();
        _v3CertGen.setSerialNumber(allocateSerialNumber());
        _v3CertGen.setIssuerDN(new X509Name(_issDN));
        _v3CertGen.setNotBefore(new Date(System.currentTimeMillis()));
        _v3CertGen.setNotAfter(new Date(System.currentTimeMillis()
                + (1000L * 60 * 60 * 24 * 100)));
        _v3CertGen.setSubjectDN(new X509Name(_subDN));
        _v3CertGen.setPublicKey(_subPub);
        _v3CertGen.setSignatureAlgorithm(algorithm);

        _v3CertGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
                createSubjectKeyId(_subPub));

        _v3CertGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false,
                createAuthorityKeyId(_issPub));

        _v3CertGen.addExtension(X509Extensions.BasicConstraints, false,
                new BasicConstraints(_ca));

        X509Certificate _cert = _v3CertGen.generate(_issPriv);

        _cert.checkValidity(new Date());
        _cert.verify(_issPub);

        return _cert;
    }

    /*
     * 
     * INTERNAL METHODS
     * 
     */

    private static AuthorityKeyIdentifier createAuthorityKeyId(PublicKey _pubKey)
            throws IOException
    {
        SubjectPublicKeyInfo _info = SubjectPublicKeyInfo.getInstance(_pubKey.getEncoded());

        return new AuthorityKeyIdentifier(_info);
    }

    private static SubjectKeyIdentifier createSubjectKeyId(PublicKey _pubKey)
            throws IOException
    {
        return new BcX509ExtensionUtils().createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(_pubKey.getEncoded()));
    }

    private static BigInteger allocateSerialNumber()
    {
        BigInteger _tmp = serialNumber;
        serialNumber = serialNumber.add(BigInteger.valueOf(1));
        return _tmp;
    }
}
