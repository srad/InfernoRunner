package com.github.srad.infernorunner.entity;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

class MyMotionState extends btMotionState {
    private final AbstractModelInstance model;

    public MyMotionState(AbstractModelInstance model) {
        this.model = model;
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        //     if (!(model instanceof PlayerInstance)) {
        //         System.out.println("[MotionState::get] " + model.toString());
        //   }
   //     worldTrans.set(model.transform);
    }

    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
//        if (!(model instanceof PlayerInstance)) {
        //          System.out.println("[MotionState::set] " + model.toString());
        //     }
    //    model.transform.set(worldTrans);
    }
}
