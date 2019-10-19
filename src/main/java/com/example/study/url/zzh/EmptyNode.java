package com.example.study.url.zzh;

import java.nio.ByteBuffer;

public class EmptyNode {

    private ByteBuffKey domainKey;

    private ByteBuffKey apiKey;

    private ByteBuffer buff;

    public EmptyNode(ByteBuffKey domainKey, ByteBuffKey apiKey, ByteBuffer buff) {
        this.domainKey = domainKey;
        this.apiKey = apiKey;
        this.buff = buff;
    }

    public ByteBuffKey getDomainKey() {
        return domainKey;
    }

    public void setDomainKey(ByteBuffKey domainKey) {
        this.domainKey = domainKey;
    }

    public ByteBuffKey getApiKey() {
        return apiKey;
    }

    public void setApiKey(ByteBuffKey apiKey) {
        this.apiKey = apiKey;
    }

    public ByteBuffer getBuff() {
        return buff;
    }

    public void setBuff(ByteBuffer buff) {
        this.buff = buff;
    }

}
