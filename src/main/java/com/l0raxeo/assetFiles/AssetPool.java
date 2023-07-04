package com.l0raxeo.assetFiles;

import com.l0raxeo.renderer.Shader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AssetPool
{

    private static Map<String, Shader> shaders = new HashMap<>();

    public static Shader getShader(String resourcePath)
    {
        File file = new File(resourcePath);

        if (!AssetPool.shaders.containsKey(file.getAbsolutePath()))
        {
            Shader shader = new Shader(resourcePath);
            shader.compile();
            shaders.put(file.getAbsolutePath(), shader);
            return shader;
        }

        return AssetPool.shaders.get(file.getAbsolutePath());
    }

}
