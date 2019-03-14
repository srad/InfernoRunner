package com.github.srad.infernorunner.entity.base;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

final class MyMotionState extends btMotionState {
    AbstractPhysicalEntity m;

    public MyMotionState(AbstractPhysicalEntity m) {
        this.m = m;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        worldTrans.set(m.transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        m.transform.set(worldTrans);
    }
}