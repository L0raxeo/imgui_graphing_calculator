package com.l0raxeo.renderer;

import com.l0raxeo.imgui.ImGuiLayer;
import com.l0raxeo.input.KeyListener;
import com.l0raxeo.input.MouseListener;
import com.l0raxeo.renderer.draw.Draw;
import com.l0raxeo.scenes.Scene;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class AppWindow
{

    private static AppWindow instance = null;

    public static String WINDOW_TITLE;
    public static int WINDOW_WIDTH, WINDOW_HEIGHT;


    public static long glfwWindow;
    private ImGuiLayer imguiLayer;
    private final ImGuiImplGlfw imguiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public static Vector4f backdrop;

    private AppWindow()
    {
        WINDOW_TITLE = "ImGui Calculator Program";
        WINDOW_WIDTH = 1280;
        WINDOW_HEIGHT = 720;
        backdrop = new Vector4f(1, 1, 1, 1);
    }

    public static AppWindow getInstance()
    {
        if (instance == null)
            instance = new AppWindow();

        return instance;
    }

    public void run()
    {
        System.out.println("LWJGL Version: " + Version.getVersion());

        initWindow();
        loop();
    }

    private void initWindow()
    {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        String glslVersion = "#version 330";

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        glfwWindow = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);

        if (glfwWindow == NULL)
            throw new IllegalStateException("Failed to create the GLFW window");

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (window, width, height) ->
        {
            WINDOW_WIDTH = width;
            WINDOW_HEIGHT = height;
            glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        });

        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1);
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        imguiLayer = new ImGuiLayer(glfwWindow);
        imguiLayer.initImGui();
        imguiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);

        Scene newScene = Scene.getInstance();
        newScene.init();
    }

    private void loop()
    {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float deltaTime = -1.0f;
        float timer = beginTime + 1;

        while (!glfwWindowShouldClose(glfwWindow))
        {
            glfwPollEvents();

            Draw.beginFrame();

            glClearColor(backdrop.x, backdrop.y, backdrop.z, backdrop.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (deltaTime >= 0)
            {
                Scene.getInstance().update(deltaTime);
                Draw.draw();

                if (beginTime >= timer)
                    timer = beginTime + 1;
            }
            this.imguiLayer.update(deltaTime);

            KeyListener.endFrame();
            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow);

            endTime = (float) glfwGetTime();
            deltaTime = endTime - beginTime;
            beginTime = endTime;
        }

        Scene.getInstance().save();
    }

}
