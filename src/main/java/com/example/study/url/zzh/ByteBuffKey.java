package com.example.study.url.zzh;

public class ByteBuffKey {

    private byte[] key;

    private int offset;

    private int len;

    public ByteBuffKey(byte[] key, int offset, int len) {
        this.key = key;
        this.offset = offset;
        this.len = len;
    }

    /**
     * 重置
     * 
     * @param key
     * @param offset
     * @param len
     */
    public ByteBuffKey reset(byte[] key, int offset, int len) {
        this.key = key;
        this.offset = offset;
        this.len = len;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ByteBuffKey)) {
            return false;
        }
        ByteBuffKey mo = (ByteBuffKey) obj;

        byte[] other = mo.getKey();

        if (mo.getLen() != len) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            if (other[mo.getOffset() + i] != key[offset + i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (int i = 0; i < len; i++)
            h = 31 * h + key[i + offset];
        return h;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

}
