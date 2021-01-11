package com.example.pg_audioemitter.model_app

import com.google.protobuf.ByteString

sealed class AudioEmitterResult {
    object Done : AudioEmitterResult()
    class AudioChunk(val byteString: ByteString): AudioEmitterResult()
}
