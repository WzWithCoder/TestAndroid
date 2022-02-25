package com.example.wangzheng.plugin;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * 1.startActivity的时候最终会走到AMS的startActivity方法，
 * 2.系统会检查一堆的信息验证这个Activity是否合法。
 * 3.然后会回调ActivityThread的Handler里的 handleLaunchActivity
 * 4.在这里走到了performLaunchActivity方法去创建Activity并回调一系列生命周期的方法
 * 5.创建Activity的时候会创建一个LoaderApk对象，然后使用这个对象的getClassLoader来创建Activity
 * 6.我们查看getClassLoader()方法发现返回的是PathClassLoader，然后他继承自BaseDexClassLoader
 * 7.然后我们查看BaseDexClassLoader发现他创建时创建了一个DexPathList类型的pathList对象，然后在findClass时调用了pathList.findClass的方法
 * 8.然后我们查看DexPathList类中的findClass发现他内部维护了一个Element[] dexElements的dex数组，findClass时是从数组中遍历查找的，
 *
 * @Desc: 加载插件
 * @Author: wangzheng
 * Create on: 2018/1/24
 */

public class PluginLoader {
    public static final String TARGET_INTENT = "extra://target_intent";

    public static void hook(Context context, String apkPath) throws Exception {
        File file = new File(apkPath);
        if (!file.exists()) {
            return;
        }

        //load apk resource
        Resources resources = getResources(context, apkPath);
        //attachBaseContext();
        //add plugin dex to host
        hookPathClassLoader(context, apkPath);
        //代理ActivityManagerNative,hook startActivity,replace TargetActivity to HoldActivity
        hookActivityManagerNative(context);
        //hook ActivityThread.mH,set Handler.mCallback to HandlerCallback
        hookActivityThreadHandler();
    }

    public static void test(Context context){
        try {
            //代理ActivityManagerNative,hook startActivity,replace TargetActivity to HoldActivity
            hookActivityManagerNative(context);
            //hook ActivityThread.mH,set Handler.mCallback to HandlerCallback
            hookActivityThreadHandler();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将插件的Dex加入到宿主的Dex列表
     *
     * @param context
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     */
    private static void hookPathClassLoader(Context context, String apkPath) throws
            IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        //dex优化后路径
        String cachePath = context.getCacheDir().getAbsolutePath();
        PathClassLoader hostPathClassLoader = (PathClassLoader) context.getClassLoader();
        //创建一个属于我们自己插件的ClassLoader，我们分析过只能使用DexClassLoader
        DexClassLoader pluginClassLoader = new DexClassLoader(apkPath, cachePath, cachePath, hostPathClassLoader);

        Object hostPathList = ReflectKit.getPathList(hostPathClassLoader);
        Object pluginPathList = ReflectKit.getDexElements(pluginClassLoader);
        Object dexElements = ReflectKit.combineArray(
                //宿主dex数组
                ReflectKit.getDexElements(hostPathList),
                //插件dex数组
                ReflectKit.getDexElements(pluginPathList));
        //将合并的pathList设置到宿主的ClassLoader
        ReflectKit.setField(hostPathList, hostPathList.getClass(), "dexElements", dexElements);
    }

    /**
     * PluginLoader AMS
     * 把真正要启动的Activity临时替换为在AndroidManifest.xml中声明的替身Activity"
     * 进而骗过AMS校验
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static void hookActivityManagerNative(Context context) throws ClassNotFoundException,
            IllegalAccessException, NoSuchFieldException {
        //Singleton<IActivityManager> singletonObject = ActivityManagerNative.gDefault;
        Object singletonObject = ReflectKit.getFieldValue(null,
                Class.forName("android.app.ActivityManagerNative"),
                "gDefault");

        //IActivityManager activityManagerProxy = Singleton.mInstance;AMS代理
        Field mInstanceField = ReflectKit.getField(
                Class.forName("android.util.Singleton"),
                "mInstance");
        //
        Object activityManagerProxy = mInstanceField.get(singletonObject);

        //创建ActivityManagerNative的代理对象, 然后替换这个字段
        Object proxy = Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{Class.forName("android.app.IActivityManager")},
                new IActivityManagerHandler(context, activityManagerProxy));
        mInstanceField.set(singletonObject, proxy);
    }

    /**
     * 由于之前我们用替身欺骗了AMS; 现在我们要换回我们真正需要启动的Activity
     * 到最终要启动Activity的时候,会交给ActivityThread 的一个内部类叫做 H 来完成
     * H 会完成这个消息转发; 最终调用它的callback
     */
    public static void hookActivityThreadHandler() throws
            ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            NoSuchFieldException {
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");

        //获取当前的ActivityThread对象
        Object currentActivityThread = ReflectKit.invokeMethod(null,
                activityThreadClass,
                "currentActivityThread");

        //由于ActivityThread一个进程只有一个,我们获取这个对象的mH
        Handler mH = (Handler) ReflectKit.getFieldValue(
                currentActivityThread, activityThreadClass, "mH");

        //设置我们自己的CallBackField
        ReflectKit.setField(mH, Handler.class
                , "mCallback"
                , new HandlerCallback());
    }

    public static void hookInstrumentation() throws
            ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            NoSuchFieldException {
        //Class.forName(“”);的作用是要求JVM查找并加载指定的类，首先要明白，java里面任何class都要装载在虚拟机上才能运行，
        //而静态代码是和class绑定的，class装载成功就表示执行了你的静态代码了，而且以后不会再走这段静态代码了。
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        //获取主线程对象 如果底层方法是静态的，那么可以忽略指定的 obj 参数。该参数可以为 null
        Object activityThreadObject = ReflectKit.invokeMethod(null,
                activityThreadClass,
                "currentActivityThread");

        Field mInstrumentationField = ReflectKit.getField(activityThreadClass, "mInstrumentation");
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(activityThreadObject);
        //需要静态代理mInstrumentation对象，这里做简单处理，直接替换
        mInstrumentationField.set(activityThreadObject, new Instrumentation() {
            @Override
            public Activity newActivity(ClassLoader cl, String className, Intent intent)
                    throws InstantiationException, IllegalAccessException, ClassNotFoundException {
                return super.newActivity(cl, className, intent);
            }
        });
    }

    public static Resources getResources(Context context, String apkPath) throws
            IllegalAccessException, InstantiationException,
            NoSuchMethodException, InvocationTargetException {
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, apkPath);

        Method ensureStringBlocks = AssetManager.class.getDeclaredMethod("ensureStringBlocks");
        ensureStringBlocks.setAccessible(true);
        ensureStringBlocks.invoke(assetManager);

        Resources superResources = context.getResources();
        return new Resources(assetManager, superResources
                .getDisplayMetrics(), superResources.getConfiguration());
    }
}
