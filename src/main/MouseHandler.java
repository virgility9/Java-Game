package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

    public boolean leftClick = false;
    public boolean rightClick = false;
    // Edge-triggered click: set on press, cleared when a menu handler consumes
    // it. Menus must use consumeLeftClickEdge(), never the held leftClick
    // boolean — a held click was being read by three competing handlers and
    // could fire again in a different state after a state change.
    public boolean leftClickEdge = false;

    public boolean consumeLeftClickEdge() {
        boolean pressed = leftClickEdge;
        leftClickEdge = false;
        return pressed;
    }
    public int mouseX = 0;
    public int mouseY = 0;
    public int mouseWheelRotation = 0;

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = true;
            leftClickEdge = true;
        }
        if (e.getButton() == MouseEvent.BUTTON3) rightClick = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) leftClick = false;
        if (e.getButton() == MouseEvent.BUTTON3) rightClick = false;
    }

    // Tracks position while dragging
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // Tracks position while just moving (no button held) — needed for hover
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelRotation = e.getWheelRotation();
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {
        leftClick = false;
        rightClick = false;
    }

    public void resetMouseWheel() {
        mouseWheelRotation = 0;
    }
}