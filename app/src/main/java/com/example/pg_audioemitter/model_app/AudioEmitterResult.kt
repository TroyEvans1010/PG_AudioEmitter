package com.example.pg_audioemitter.model_app

import com.google.protobuf.ByteString

sealed class AudioEmitterResult {
    class Done(val combinedByteString: ByteString) : AudioEmitterResult()
    class AudioChunk(val byteString: ByteString): AudioEmitterResult()
}
