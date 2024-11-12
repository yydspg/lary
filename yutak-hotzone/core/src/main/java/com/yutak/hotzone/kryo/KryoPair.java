package com.yutak.hotzone.kryo;


import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;

public class KryoPair {
    private final Kryo kryo;
    private final Output output;
    private final Input input;

    public Kryo getKryo() {
        return kryo;
    }

    public Output getOutput() {
        return output;
    }

    public Input getInput() {
        return input;
    }

    public KryoPair(Kryo kryo, Output output, Input input) {
        this.kryo = kryo;
        this.output = output;
        this.input = input;
    }
}
