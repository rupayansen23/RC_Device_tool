package com.example.devicetoolv1.utils

object CRC {
    fun calculateCRC8(data: ByteArray): Byte {
        var crc = 0x00.toByte()
        for (b in data) {
            crc = (crc.toInt() xor b.toInt()).toByte()
            for (i in 0 until 8) {
                crc = if (crc.toInt() and 0x80 != 0) {
                    ((crc.toInt() shl 1) xor 0x07).toByte()
                } else {
                    (crc.toInt() shl 1).toByte()
                }
            }
        }
        return crc
    }
}
