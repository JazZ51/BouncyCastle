package com.distrimind.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import com.distrimind.bouncycastle.jce.spec.ECNamedCurveGenParameterSpec;
import com.distrimind.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import com.distrimind.bouncycastle.jce.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;

import com.distrimind.bouncycastle.asn1.ASN1ObjectIdentifier;
import com.distrimind.bouncycastle.asn1.DERNull;
import com.distrimind.bouncycastle.asn1.x9.ECNamedCurveTable;
import com.distrimind.bouncycastle.asn1.x9.X962Parameters;
import com.distrimind.bouncycastle.asn1.x9.X9ECParameters;
import com.distrimind.bouncycastle.asn1.x9.X9ECPoint;
import com.distrimind.bouncycastle.jce.provider.BouncyCastleProvider;
import com.distrimind.bouncycastle.math.ec.ECCurve;

public class AlgorithmParametersSpi
    extends java.security.AlgorithmParametersSpi
{
    private ECParameterSpec ecParameterSpec;
    private String curveName;

    protected boolean isASN1FormatString(String format)
    {
        return format == null || format.equals("ASN.1");
    }

    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec)
        throws InvalidParameterSpecException
    {
        if (algorithmParameterSpec instanceof ECNamedCurveGenParameterSpec)
        {
            ECNamedCurveGenParameterSpec ecGenParameterSpec = (ECNamedCurveGenParameterSpec)algorithmParameterSpec;
            X9ECParameters params = ECNamedCurveTable.getByName(ecGenParameterSpec.getName());

            curveName = ecGenParameterSpec.getName();
            ecParameterSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
        }
        else if (algorithmParameterSpec instanceof ECParameterSpec)
        {
            curveName = null;
            ecParameterSpec = (ECParameterSpec)algorithmParameterSpec;
        }
        else
        {
            throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
        }
    }

    protected void engineInit(byte[] bytes)
        throws IOException
    {
        engineInit(bytes, "ASN.1");
    }

   
    protected void engineInit(byte[] bytes, String format)
        throws IOException
    {
        if (isASN1FormatString(format))
        {
            X962Parameters params = X962Parameters.getInstance(bytes);

            if (params.isNamedCurve())
            {
                curveName = ECNamedCurveTable.getName(ASN1ObjectIdentifier.getInstance(params.getParameters()));
                X9ECParameters curveParams = ECNamedCurveTable.getByName(curveName);

                ecParameterSpec = new ECNamedCurveParameterSpec(curveName, curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
                return;
            }

            X9ECParameters curveParams = X9ECParameters.getInstance(params.getParameters());

            ecParameterSpec = new ECParameterSpec(curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        }
        else
        {
            throw new IOException("Unknown encoded parameters format in AlgorithmParameters object: " + format);
        }
    }

   
    protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec)
        throws InvalidParameterSpecException
    {
        if (ECParameterSpec.class.isAssignableFrom(paramSpec) || paramSpec == AlgorithmParameterSpec.class)
        {
            return ecParameterSpec;
        }
        else if (ECNamedCurveGenParameterSpec.class.isAssignableFrom(paramSpec) && curveName != null)
        {
            return new ECNamedCurveGenParameterSpec(curveName);
        }
        throw new InvalidParameterSpecException("EC AlgorithmParameters cannot convert to " + paramSpec.getName());
    }

   
    protected byte[] engineGetEncoded()
        throws IOException
    {
        return engineGetEncoded("ASN.1");
    }

   
    protected byte[] engineGetEncoded(String format)
        throws IOException
    {
        if (isASN1FormatString(format))
        {
            X962Parameters params;

            if (ecParameterSpec == null)     // implicitly CA
            {
                params = new X962Parameters(DERNull.INSTANCE);
            }
            else if (curveName != null)
            {
                params = new X962Parameters(ECNamedCurveTable.getOID(curveName));
            }
            else
            {
                X9ECParameters ecP = new X9ECParameters(
                    ecParameterSpec.getCurve(),
                    new X9ECPoint(ecParameterSpec.getG(), false),
                    ecParameterSpec.getN(),
                    ecParameterSpec.getH(),
                    ecParameterSpec.getSeed());

                params = new X962Parameters(ecP);
            }

            return params.getEncoded();
        }

        throw new IOException("Unknown parameters format in AlgorithmParameters object: " + format);
    }

    protected String engineToString()
    {
        return "EC Parameters";
    }
}
