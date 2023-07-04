package com.l0raxeo.input;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener
{

    private static KeyListener instance;
    private final boolean[] keyPressed = new boolean[350];
    private final boolean[] keyBeginPress = new boolean[350];

    private KeyListener() {}

    public static KeyListener getInstance()
    {
        if (instance == null)
            instance = new KeyListener();

        return instance;
    }

    public static void endFrame()
    {
        Arrays.fill(getInstance().keyBeginPress, false);
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods)
    {
        if (action == GLFW_PRESS)
        {
            getInstance().keyPressed[key] = true;
            getInstance().keyBeginPress[key] = true;
        }
        else if (action == GLFW_RELEASE)
        {
            getInstance().keyPressed[key] = false;
            getInstance().keyBeginPress[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return getInstance().keyPressed[keyCode];
    }

    public static boolean isKeyBeginPress(int keyCode)
    {
        return getInstance().keyBeginPress[keyCode];
    }

}
