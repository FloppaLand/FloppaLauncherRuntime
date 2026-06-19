package pro.gravit.launcher.gui.core.impl;

import javafx.scene.layout.Pane;

public abstract class VisualComponentBase implements UIComponent {
    protected Pane layout;
    boolean isInit;
    boolean isPostInit;

    @Override
    public Pane getLayout() {
        return layout;
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    @Override
    public abstract void init() throws Exception;

    @Override
    public void postInit() throws Exception {
        if (!isPostInit) {
            doPostInit();
            isPostInit = true;
        }
    }

    protected abstract void doInit();

    protected void doPostInit() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    public boolean isDisableReturnBack() {
        return false;
    }
}
