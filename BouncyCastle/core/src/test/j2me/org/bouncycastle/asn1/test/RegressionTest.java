package com.distrimind.bouncycastle.asn1.test;

import com.distrimind.bouncycastle.util.test.SimpleTest;
import com.distrimind.bouncycastle.util.test.Test;

public class RegressionTest
{
    public static Test[]    tests = {
        new CertificateTest(),
        new CMSTest(),
        new OCSPTest(),
        new OIDTest(),
        new PKCS10Test(),
        new PKCS12Test(),
        new X509NameTest(),
        new X500NameTest(),
        new X509ExtensionsTest(),
        new BitStringTest(),
        new MiscTest(),
        new X9Test(),
        new EncryptedPrivateKeyInfoTest(),
        new StringTest(),
        new RequestedCertificateUnitTest(),
        new OtherCertIDUnitTest(),
        new OtherSigningCertificateUnitTest(),
        new ContentHintsUnitTest(),
        new CertHashUnitTest(),
        new AdditionalInformationSyntaxUnitTest(),
        new AdmissionSyntaxUnitTest(),
        new AdmissionsUnitTest(),
        new DeclarationOfMajorityUnitTest(),
        new ProcurationSyntaxUnitTest(),
        new ProfessionInfoUnitTest(),
        new RestrictionUnitTest(),
        new NamingAuthorityUnitTest(),
        new MonetaryLimitUnitTest(),
        new DERApplicationSpecificTest(),
        new IssuingDistributionPointUnitTest(),
        new TargetInformationTest(),
        new SubjectKeyIdentifierTest(),
        new ESSCertIDv2UnitTest(),
        new ParsingTest(),
        new GeneralNameTest(),
        new RFC4519Test()
    };

    public static void main(String[] args)
    {
        SimpleTest.runTests(tests);
    }
}
