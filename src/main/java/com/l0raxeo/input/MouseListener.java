package com.l0raxeo.input;

import com.l0raxeo.renderer.Camera;
import com.l0raxeo.scenes.Scene;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static com.l0raxeo.renderer.AppWindow.WINDOW_HEIGHT;
import static com.l0raxeo.renderer.AppWindow.WINDOW_WIDTH;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener
{

    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX;
    private final boolean[] mouseButtonPressed = new boolean[3];
    private final boolean[] mouseButtonBeginPress = new boolean[3];
    private int mouseButtonDown = 0;
    private boolean isDragging;
    private boolean isScrolling;

    private MouseListener()
    {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static void clear() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().xPos = 0.0;
        get().yPos = 0.0;
        get().lastX = 0.0;
        get().lastY = 0.0;
        get().mouseButtonDown = 0;
        get().isDragging = false;
        get().isScrolling = false;
        Arrays.fill(get().mouseButtonPressed, false);
    }

    public static MouseListener get()
    {
        if (MouseListener.instance == null)
        {
            MouseListener.instance = new MouseListener();
        }

        return instance;
    }

    public static void mousePosCallback(long AppWindow, double xPos, double yPos)
    {
        if (get().mouseButtonDown > 0) {
            get().isDragging = true;
        }

        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] ||
                get().mouseButtonPressed[1] ||
                get().mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long AppWindow, int button, int action, int mods)
    {
        if (action == GLFW_PRESS) {
            get().mouseButtonDown++;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
                get().mouseButtonBeginPress[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            get().mouseButtonDown--;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().mouseButtonBeginPress[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static Vector2f screenToWorld(Vector2f screenCoords) {
        Vector2f normalizedScreenCords = new Vector2f(
                screenCoords.x / WINDOW_WIDTH,
                (WINDOW_HEIGHT - screenCoords.y) / WINDOW_HEIGHT
        );
        normalizedScreenCords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Scene.getInstance().camera;
        Vector4f tmp = new Vector4f(normalizedScreenCords.x, normalizedScreenCords.y,
                0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f worldToScreen(Vector2f worldCoords) {
        Camera camera = Scene.getInstance().camera;
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f AppWindowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        AppWindowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        AppWindowSpace.mul(new Vector2f(WINDOW_WIDTH, WINDOW_HEIGHT));

        return AppWindowSpace;
    }

    public static void mouseScrollCallback(long AppWindow, double xOffset, double yOffset)
    {
        get().scrollX = xOffset;
        get().scrollY = yOffset;

        get().isScrolling = true;
    }

    public static void endFrame()
    {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        Arrays.fill(get().mouseButtonBeginPress, false);
    }

    public static float getX()
    {
        return (float) get().xPos;
    }

    public static float getY()
    {
        return (float) get().yPos;
    }

    /**
     * @return new Vector2f(getX(), getY());
     */
    public static Vector2f getPos()
    {
        return new Vector2f(getX(), getY());
    }

    public static float getOrthoX()
    {
        float currentX = getX();
        currentX = (currentX / (float) WINDOW_WIDTH) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
        tmp.mul(Scene.getInstance().camera.getInverseProjection()).mul(Scene.getInstance().camera.getInverseView());
        currentX = tmp.x;

        return currentX;
    }

    public static float getOrthoY()
    {
        float currentY = WINDOW_HEIGHT - getY();
        currentY = (currentY / (float) WINDOW_HEIGHT) * 2.0f - 1.0f;
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);
        tmp.mul(Scene.getInstance().camera.getInverseProjection()).mul(Scene.getInstance().camera.getInverseView());
        currentY = tmp.y;

        return currentY;
    }

    public static float getDragX()
    {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDragY()
    {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX()
    {
        return (float) get().scrollX;
    }

    public static float getScrollY()
    {
        return (float) get().scrollY;
    }

    public static boolean isDragging()
    {
        return get().isDragging;
    }

    public static boolean isScrolling()
    {
        return get().isScrolling;
    }

    /**
     * GLFW_MOUSE_BUTTON_1 = left click
     * GLFW_MOUSE_BUTTON_2 = right click
     * GLFW_MOUSE_BUTTON_3 = middle click (scroll wheel click)
     *
     * <a href="https://www.glfw.org/docs/3.3/group__buttons.html">...</a>
     */
    public static boolean mouseButtonDown(int button)
    {
        if (button < get().mouseButtonPressed.length)
        {
            return get().mouseButtonPressed[button];
        }
        else
        {
            return false;
        }
    }

    public static boolean mouseButtonBeginPress(int button)
    {
        if (button < get().mouseButtonBeginPress.length)
        {
            return get().mouseButtonBeginPress[button];
        }
        else
        {
            return false;
        }
    }

}