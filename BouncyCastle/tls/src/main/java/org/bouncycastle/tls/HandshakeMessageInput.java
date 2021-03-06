package com.distrimind.bouncycastle.tls;

import java.io.ByteArrayInputStream;

import com.distrimind.bouncycastle.tls.crypto.TlsHash;

class HandshakeMessageInput
    extends ByteArrayInputStream
{
    HandshakeMessageInput(byte[] buf, int offset, int length)
    {
        super(buf, offset, length);
    }

    public boolean markSupported()
    {
        return false;
    }

    public void mark(int readAheadLimit)
    {
        throw new UnsupportedOperationException();
    }

    void updateHash(TlsHash hash)
    {
        hash.update(buf, mark, count - mark);
    }
}
