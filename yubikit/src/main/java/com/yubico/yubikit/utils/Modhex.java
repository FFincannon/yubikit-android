/*
 * Copyright (C) 2019 Yubico.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yubico.yubikit.utils;

import android.util.SparseIntArray;

import com.yubico.yubikit.exceptions.NotSupportedOperation;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Modhex mapping: https://developers.yubico.com/yubico-c/Manuals/modhex.1.html
 */
public class Modhex {
    private final static char[] ALPHABET = "cbdefghijklnrtuv".toCharArray();

    private static final SparseIntArray table = new SparseIntArray();
    static {
        for (int i = 0; i < ALPHABET.length; i++) {
            table.put(ALPHABET[i], i);
        }
    }

    /**
     * Decodes Modhex encoded string.
     *
     * @param modhex a Modhex encoded string.
     * @return decoded byte array
     */
    public static byte[] decode(String modhex) {
        if (modhex.length() % 2 != 0) {
            throw new IllegalArgumentException("Input string length is not a multiple of 2");
        }

        byte byteValue = 0;
        char[] chars = modhex.toLowerCase().toCharArray();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (int i = 0; i < chars.length; i++) {
            // find hex code for each symbol
            int code = table.get(chars[i], -1);
            if (code == -1) {
                throw new IllegalArgumentException("Input string contains non-modhex character(s).");
            }

            // 2 symbols merged into 1 byte
            boolean shift = i % 2 == 0;
            if (shift) {
                byteValue = (byte) (code << 4);
            } else {
                byteValue |= code;
                outputStream.write(byteValue);
            }
        }
        return outputStream.toByteArray();
    }

    /**
     * Encode data as Modhex.
      * @param bytes the data to encode
     * @return A Modhex encoded string
     */
    public static String encode(byte[] bytes) {
        StringBuilder output = new StringBuilder();
        for (byte b : bytes) {
            output.append(ALPHABET[(b >> 4) & 0xF]).append(ALPHABET[b & 0xF]);
        }
        return output.toString();
    }
}
