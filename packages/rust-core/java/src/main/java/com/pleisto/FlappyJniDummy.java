package com.pleisto;

import java.util.Map;

class FlappyJniDummy extends FlappyJniLoader {
    public static native String echo(String code, boolean network, Map<String, String> envs, String cache_path);
}
