package org.bouncycastle.bccrypto.ec;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.bcmath.ec.ECConstants;
import org.bouncycastle.bcutil.BigIntegers;

class ECUtil
{
    static BigInteger generateK(BigInteger n, SecureRandom random)
    {
        int nBitLength = n.bitLength();
        BigInteger k;
        do
        {
            k = BigIntegers.createRandomBigInteger(nBitLength, random);
        }
        while (k.equals(ECConstants.ZERO) || (k.compareTo(n) >= 0));
        return k;
    }
}