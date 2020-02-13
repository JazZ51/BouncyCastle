package org.bouncycastle.bccrypto.test;

import org.bouncycastle.bcutil.encoders.Hex;
import org.bouncycastle.bccrypto.CipherParameters;
import org.bouncycastle.bccrypto.macs.VMPCMac;
import org.bouncycastle.bccrypto.params.KeyParameter;
import org.bouncycastle.bccrypto.params.ParametersWithIV;
import org.bouncycastle.bcutil.Arrays;
import org.bouncycastle.bcutil.test.SimpleTest;

public class VMPCMacTest extends SimpleTest
{
    public String getName()
    {
        return "VMPC-MAC";
    }

    public static void main(String[] args)
    {
        runTest(new VMPCMacTest());
    }

    static byte[] output1 = Hex.decode("9BDA16E2AD0E284774A3ACBC8835A8326C11FAAD");

    public void performTest() throws Exception
    {
        CipherParameters kp = new KeyParameter(
            Hex.decode("9661410AB797D8A9EB767C21172DF6C7"));
        CipherParameters kpwiv = new ParametersWithIV(kp,
            Hex.decode("4B5C2F003E67F39557A8D26F3DA2B155"));

        byte[] m = new byte[512];

        int offset = 117;
        for (int i = 0; i < 256; i++)
        {
            m[offset + i] = (byte) i;
        }

        VMPCMac mac = new VMPCMac();
        mac.init(kpwiv);

        mac.update(m, offset, 256);

        byte[] out = new byte[20];
        mac.doFinal(out, 0);

        if (!Arrays.areEqual(out, output1))
        {
            fail("Fail", new String(Hex.encode(output1)), new String(Hex.encode(out)));
        }
    }
}