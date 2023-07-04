package com.l0raxeo.scenes;

import com.l0raxeo.input.MouseListener;
import com.l0raxeo.renderer.Camera;
import com.l0raxeo.renderer.draw.Draw;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Scene
{

    private static Scene instance;

    public Camera camera;
    private String imGuiSaveFilePath = "assets/saves/imgui.ini";

    private Scene() {}

    public static Scene getInstance()
    {
        if (instance == null)
            instance = new Scene();

        return instance;
    }

    public void init()
    {
        imGuiSaveFilePath = "assets/saves/imgui.ini";
        camera = new Camera(new Vector2f(0, 0));
    }

    public void update(float deltaTime)
    {
        Draw.addLine2D(new Vector2f(500, 50), new Vector2f(400, 200), new Vector3f(0.25f, 0.75f, 1f));
        updateCameraPan();
        updateCameraZoom();
    }

    private void updateCameraPan()
    {
        if (MouseListener.isDragging())
        {
            float dragX = MouseListener.getDragX();
            float dragY = MouseListener.getDragY();

            camera.position.x += (dragX * camera.getZoom());
            camera.position.y += (-dragY * camera.getZoom());
        }
    }

    private void updateCameraZoom()
    {
        if (MouseListener.isScrolling())
        {
            float wheel = -MouseListener.getScrollY();
            float SCROLL_CONSTANT = 0.02f;
            camera.zoom(wheel * SCROLL_CONSTANT);
            camera.adjustProjection();
        }
    }

    public void save()
    {
        ImGui.saveIniSettingsToDisk(imGuiSaveFilePath);
    }

}