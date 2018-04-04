package com.wangyu.ibutterknife;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * author by wangyu
 * Use For
 * created on 2018/3/30.
 */

public class ViewBind {


    public static void bind(Activity target) {
        try {
            Class<?> inject = target.getClass().getClassLoader().loadClass(target.getClass().getName() + "_Inject");
            Constructor<?> cons = inject.getConstructor(target.getClass());
            cons.newInstance(target);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void bind(Fragment target){
        try {
            Class<?> inject = target.getClass().getClassLoader().loadClass(target.getClass().getName() + "_Inject");
            Constructor<?> cons = inject.getConstructor(target.getClass());
            cons.newInstance(target);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
