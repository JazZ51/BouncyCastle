package com.distrimind.bouncycastle.openpgp.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.distrimind.bouncycastle.bcpg.BCPGInputStream;
import com.distrimind.bouncycastle.openpgp.PGPLiteralData;
import com.distrimind.bouncycastle.openpgp.PGPUtil;
import com.distrimind.bouncycastle.util.Arrays;
import com.distrimind.bouncycastle.util.Strings;
import com.distrimind.bouncycastle.util.io.Streams;
import com.distrimind.bouncycastle.util.test.SimpleTest;

public class PGPUtilTest
    extends SimpleTest
{
    public String getName()
    {
        return "PGPUtilTest";
    }

    public void performTest()
        throws Exception
    {
        byte[] contentMessage = Strings.toByteArray("Hello, world!\r\nhello, World!\r\n");

        File dataFile = File.createTempFile("bcpg", ".txt");

        FileOutputStream fOut = new FileOutputStream(dataFile);

        fOut.write(contentMessage);

        fOut.close();

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPUtil.writeFileToLiteralData(bOut, 't', dataFile);

        testLiteralData("no buf", bOut.toByteArray(), dataFile.getName(), 't', contentMessage);

        bOut = new ByteArrayOutputStream();

        PGPUtil.writeFileToLiteralData(bOut, 't', dataFile, new byte[1 << 16]);

        testLiteralData("buf", bOut.toByteArray(), dataFile.getName(), 't', contentMessage);

        dataFile.delete();
    }

    private void testLiteralData(String id, byte[] data, String fileName, char type, byte[] content)
        throws IOException
    {
        PGPLiteralData ld = new PGPLiteralData(new BCPGInputStream(new ByteArrayInputStream(data)));

        isEquals(fileName, ld.getFileName());
        isTrue(type == ld.getFormat());

        byte[] bytes = Streams.readAll(ld.getDataStream());
        
        isTrue(id + " contents mismatch", Arrays.areEqual(bytes, content));
    }

    public static void main(
        String[]    args)
    {
        runTest(new PGPUtilTest());
    }
}