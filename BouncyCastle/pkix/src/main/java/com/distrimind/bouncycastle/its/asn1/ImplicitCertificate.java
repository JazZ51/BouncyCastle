package com.distrimind.bouncycastle.its.asn1;

import com.distrimind.bouncycastle.asn1.ASN1Sequence;

public class ImplicitCertificate
    extends CertificateBase
{
    private ImplicitCertificate(ASN1Sequence seq)
    {
        super(seq);
    }
}
