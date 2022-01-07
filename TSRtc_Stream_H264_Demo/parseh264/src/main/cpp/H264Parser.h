#pragma once

#include <iostream>

namespace HH {
    enum NaluType {
        IDR = 0x05,
        SEI = 0x06,
        SPS = 0x07,
        PPS = 0x08
    };

    class SpsParser {
    private:
        int32_t h264_decode_seq_parameter_set(const char *buf, uint32_t nLen, int32_t &Width,
                                              int32_t &Height);

        int32_t u(uint32_t BitCount, const char *buf, uint32_t &nStartBit);

        int32_t Se(const char *pBuff, uint32_t nLen, uint32_t &nStartBit);

        uint32_t Ue(const char *pBuff, uint32_t nLen, uint32_t &nStartBit);

    public:
        int32_t
        ParseVideoSize(const char *sps, uint32_t length, int32_t *videoWidth, int32_t *videoHeight,
                       int startcode);
    };

    class ParserH264 {
    private:
        SpsParser spsParser;

        bool isStartCode(const uint8_t *buf, int &len);

        bool NaluHeader(NaluType type, const uint8_t *buf, int &len);

        bool SPS(const uint8_t *buf, int &len);

        bool PPS(const uint8_t *buf, int &len);

        bool SEI(const uint8_t *buf, int &len);

        bool IDR(const uint8_t *buf, int &len);

        void getSpsLen(const uint8_t *buf, int &length);

    public:
        bool parserH264(const uint8_t *buffer, int length, int &width, int &height);
    };

}